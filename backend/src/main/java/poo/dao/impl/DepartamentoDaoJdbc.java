package poo.dao.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import poo.dao.DepartamentoDao;
import poo.dao.support.JdbcTableMetadata;
import poo.model.Departamento;

@Repository
public class DepartamentoDaoJdbc implements DepartamentoDao {

  private static final @NonNull JdbcTableMetadata<Departamento> TABLE = JdbcTableMetadata
    .<Departamento>builder("departamento")
    .columns("id", "nome", "sigla")
    .auditable()
    .defaultOrderBy("nome")
    .rowMapper((rs, rowNum) -> {
      Departamento departamento = new Departamento();
      departamento.setId(rs.getLong("id"));
      departamento.setNome(rs.getString("nome"));
      departamento.setSigla(rs.getString("sigla"));
      JdbcTableMetadata.populateAuditColumns(rs, departamento::setCreatedAt, departamento::setUpdatedAt);
      return departamento;
    })
    .build();

  private static final @NonNull RowMapper<Departamento> ROW_MAPPER = TABLE.rowMapper();
  private static final @NonNull String INSERT_SQL = TABLE.insertReturningSql("nome, sigla");
  private static final @NonNull String SELECT_BY_ID_SQL = TABLE.selectByIdSql("id = ?");
  private static final @NonNull String SELECT_ALL_SQL = TABLE.selectAllSql();
  private static final @NonNull String UPDATE_SQL = TABLE.updateReturningSql("nome = ?, sigla = ?", "id = ?");
  private static final @NonNull String DELETE_SQL = TABLE.deleteSql("id = ?");

  private final JdbcTemplate jdbc;

  public DepartamentoDaoJdbc(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Departamento create(Departamento departamento) {
    return jdbc.queryForObject(
      INSERT_SQL,
      ROW_MAPPER,
      departamento.getNome(),
      departamento.getSigla()
    );
  }

  @Override
  public Optional<Departamento> findById(Long id) {
    List<Departamento> list = jdbc.query(SELECT_BY_ID_SQL, ROW_MAPPER, id);
    return list.stream().findFirst();
  }

  @Override
  public List<Departamento> findAll() {
    return jdbc.query(SELECT_ALL_SQL, ROW_MAPPER);
  }

  @Override
  public Optional<Departamento> update(Departamento departamento) {
    Objects.requireNonNull(departamento.getId(), "Departamento id must not be null");
    List<Departamento> list = jdbc.query(
      UPDATE_SQL,
      ROW_MAPPER,
      departamento.getNome(),
      departamento.getSigla(),
      departamento.getId()
    );
    return list.stream().findFirst();
  }

  @Override
  public boolean delete(Long id) {
    return jdbc.update(DELETE_SQL, id) > 0;
  }
}
