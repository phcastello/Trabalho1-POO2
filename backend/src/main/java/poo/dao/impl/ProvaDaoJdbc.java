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
import poo.dao.ProvaDao;
import poo.dao.support.JdbcTableMetadata;
import poo.model.Prova;

@Repository
public class ProvaDaoJdbc implements ProvaDao {

  private static final @NonNull JdbcTableMetadata<Prova> TABLE = JdbcTableMetadata
    .<Prova>builder("prova")
    .columns("id", "departamento_id", "titulo", "data", "descricao")
    .auditable()
    .defaultOrderBy("data DESC, titulo")
    .rowMapper((rs, rowNum) -> {
      Prova prova = new Prova();
      prova.setId(rs.getLong("id"));
      prova.setDepartamentoId(rs.getLong("departamento_id"));
      prova.setTitulo(rs.getString("titulo"));
      prova.setData(rs.getObject("data", LocalDate.class));
      prova.setDescricao(rs.getString("descricao"));
      JdbcTableMetadata.populateAuditColumns(rs, prova::setCreatedAt, prova::setUpdatedAt);
      return prova;
    })
    .build();

  private static final @NonNull RowMapper<Prova> ROW_MAPPER = TABLE.rowMapper();
  private static final @NonNull String INSERT_SQL = TABLE.insertReturningSql("departamento_id, titulo, data, descricao");
  private static final @NonNull String SELECT_BY_ID_SQL = TABLE.selectByIdSql("id = ?");
  private static final @NonNull String SELECT_ALL_SQL = TABLE.selectAllSql();
  private static final @NonNull String UPDATE_SQL = TABLE.updateReturningSql(
    "departamento_id = ?, titulo = ?, data = ?, descricao = ?",
    "id = ?"
  );
  private static final @NonNull String DELETE_SQL = TABLE.deleteSql("id = ?");

  private final JdbcTemplate jdbc;

  public ProvaDaoJdbc(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Prova create(Prova prova) {
    return jdbc.queryForObject(INSERT_SQL, ROW_MAPPER,
      prova.getDepartamentoId(),
      prova.getTitulo(),
      toSqlDate(prova.getData()),
      prova.getDescricao()
    );
  }

  @Override
  public Optional<Prova> findById(Long id) {
    List<Prova> list = jdbc.query(SELECT_BY_ID_SQL, ROW_MAPPER, id);
    return list.stream().findFirst();
  }

  @Override
  public List<Prova> findAll() {
    return jdbc.query(SELECT_ALL_SQL, ROW_MAPPER);
  }

  @Override
  public Optional<Prova> update(Prova prova) {
    Objects.requireNonNull(prova.getId(), "Prova id must not be null");
    List<Prova> list = jdbc.query(UPDATE_SQL, ROW_MAPPER,
      prova.getDepartamentoId(),
      prova.getTitulo(),
      toSqlDate(prova.getData()),
      prova.getDescricao(),
      prova.getId()
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
