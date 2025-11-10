package poo.dao.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import poo.dao.AlunoDao;
import poo.model.Aluno;

@Repository
public class AlunoDaoJdbc implements AlunoDao {

  private static final @NonNull String COLUMNS =
      nonNull("id, ra, nome, email, departamento_id, data_nascimento, created_at, updated_at");
  private static final @NonNull String INSERT_SQL = nonNull("""
      INSERT INTO aluno (ra, nome, email, departamento_id, data_nascimento)
      VALUES (?, ?, ?, ?, ?)
      RETURNING %s
    """.formatted(COLUMNS));
  private static final @NonNull String SELECT_BY_ID_SQL =
      nonNull("SELECT %s FROM aluno WHERE id = ?".formatted(COLUMNS));
  private static final @NonNull String SELECT_ALL_SQL =
      nonNull("SELECT %s FROM aluno ORDER BY nome".formatted(COLUMNS));
  private static final @NonNull String UPDATE_SQL = nonNull("""
      UPDATE aluno
      SET ra = ?, nome = ?, email = ?, departamento_id = ?, data_nascimento = ?
      WHERE id = ?
      RETURNING %s
    """.formatted(COLUMNS));

  private static final @NonNull RowMapper<Aluno> ROW_MAPPER = (rs, rowNum) -> {
    Aluno aluno = new Aluno();
    aluno.setId(rs.getLong("id"));
    aluno.setRa(rs.getString("ra"));
    aluno.setNome(rs.getString("nome"));
    aluno.setEmail(rs.getString("email"));
    aluno.setDepartamentoId(rs.getLong("departamento_id"));
    aluno.setDataNascimento(rs.getObject("data_nascimento", LocalDate.class));

    OffsetDateTime created = rs.getObject("created_at", OffsetDateTime.class);
    if (created != null) {
      aluno.setCreatedAt(created.toInstant());
    }
    OffsetDateTime updated = rs.getObject("updated_at", OffsetDateTime.class);
    if (updated != null) {
      aluno.setUpdatedAt(updated.toInstant());
    }
    return aluno;
  };

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
    return jdbc.update("DELETE FROM aluno WHERE id = ?", id) > 0;
  }

  private static Date toSqlDate(LocalDate data) {
    return data != null ? Date.valueOf(data) : null;
  }

  @NonNull
  private static <T> T nonNull(T value) {
    if (value == null) {
      throw new IllegalStateException("Unexpected null value");
    }
    return value;
  }
}
