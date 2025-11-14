package poo.dao.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import poo.dao.AlunoDao;
import poo.dao.support.JdbcTableMetadata;
import poo.model.Aluno;

@Repository
public class AlunoDaoJdbc implements AlunoDao {

  private static final @NonNull JdbcTableMetadata<Aluno> TABLE = JdbcTableMetadata
    .<Aluno>builder("aluno")
    .columns("id", "ra", "nome", "email", "departamento_id", "data_nascimento")
    .auditable()
    .defaultOrderBy("nome")
    .rowMapper((rs, rowNum) -> {
      Aluno aluno = new Aluno();
      aluno.setId(rs.getLong("id"));
      aluno.setRa(rs.getString("ra"));
      aluno.setNome(rs.getString("nome"));
      aluno.setEmail(rs.getString("email"));
      aluno.setDepartamentoId(rs.getLong("departamento_id"));
      aluno.setDataNascimento(rs.getObject("data_nascimento", LocalDate.class));
      JdbcTableMetadata.populateAuditColumns(rs, aluno::setCreatedAt, aluno::setUpdatedAt);
      return aluno;
    })
    .build();

  private static final @NonNull RowMapper<Aluno> ROW_MAPPER = TABLE.rowMapper();
  private static final @NonNull String INSERT_SQL = TABLE.insertReturningSql("ra, nome, email, departamento_id, data_nascimento");
  private static final @NonNull String SELECT_BY_ID_SQL = TABLE.selectByIdSql("id = ?");
  private static final @NonNull String SELECT_ALL_SQL = TABLE.selectAllSql();
  private static final @NonNull String UPDATE_SQL = TABLE.updateReturningSql(
    "ra = ?, nome = ?, email = ?, departamento_id = ?, data_nascimento = ?",
    "id = ?"
  );
  private static final @NonNull String DELETE_SQL = TABLE.deleteSql("id = ?");

  private final JdbcTemplate jdbc;

  public AlunoDaoJdbc(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Aluno create(Aluno aluno) {
    return jdbc.queryForObject(INSERT_SQL, ROW_MAPPER,
      aluno.getRa(),
      aluno.getNome(),
      aluno.getEmail(),
      aluno.getDepartamentoId(),
      toSqlDate(aluno.getDataNascimento())
    );
  }

  @Override
  public Optional<Aluno> findById(Long id) {
    List<Aluno> list = jdbc.query(SELECT_BY_ID_SQL, ROW_MAPPER, id);
    return list.stream().findFirst();
  }

  @Override
  public List<Aluno> findAll() {
    return jdbc.query(SELECT_ALL_SQL, ROW_MAPPER);
  }

  @Override
  public Optional<Aluno> update(Aluno aluno) {
    Objects.requireNonNull(aluno.getId(), "Aluno id must not be null");
    List<Aluno> list = jdbc.query(UPDATE_SQL, ROW_MAPPER,
      aluno.getRa(),
      aluno.getNome(),
      aluno.getEmail(),
      aluno.getDepartamentoId(),
      toSqlDate(aluno.getDataNascimento()),
      aluno.getId()
    );
    return list.stream().findFirst();
  }

  @Override
  public boolean delete(Long id) {
    return jdbc.update(DELETE_SQL, id) > 0;
  }

  private static Date toSqlDate(LocalDate data) {
    return data != null ? Date.valueOf(data) : null;
  }
}
