package poo.dao.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import poo.dao.DepartamentoDao;
import poo.model.Departamento;

@Repository
public class DepartamentoDaoJdbc implements DepartamentoDao {

  private static final String COLUMNS = "id, nome, sigla, created_at, updated_at";

  private static final RowMapper<Departamento> ROW_MAPPER = (rs, rowNum) -> {
    Departamento departamento = new Departamento();
    departamento.setId(rs.getLong("id"));
    departamento.setNome(rs.getString("nome"));
    departamento.setSigla(rs.getString("sigla"));

    OffsetDateTime created = rs.getObject("created_at", OffsetDateTime.class);
    if (created != null) {
      departamento.setCreatedAt(created.toInstant());
    }
    OffsetDateTime updated = rs.getObject("updated_at", OffsetDateTime.class);
    if (updated != null) {
      departamento.setUpdatedAt(updated.toInstant());
    }
    return departamento;
  };

  private final JdbcTemplate jdbc;

  public DepartamentoDaoJdbc(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Departamento create(Departamento departamento) {
    return jdbc.queryForObject("""
      INSERT INTO departamento (nome, sigla)
      VALUES (?, ?)
      RETURNING %s
    """.formatted(COLUMNS), ROW_MAPPER, departamento.getNome(), departamento.getSigla());
  }

  @Override
  public Optional<Departamento> findById(Long id) {
    List<Departamento> list =
      jdbc.query("SELECT %s FROM departamento WHERE id = ?".formatted(COLUMNS), ROW_MAPPER, id);
    return list.stream().findFirst();
  }

  @Override
  public List<Departamento> findAll() {
    return jdbc.query("SELECT %s FROM departamento ORDER BY nome".formatted(COLUMNS), ROW_MAPPER);
  }

  @Override
  public Optional<Departamento> update(Departamento departamento) {
    Objects.requireNonNull(departamento.getId(), "Departamento id must not be null");
    List<Departamento> list = jdbc.query("""
      UPDATE departamento
      SET nome = ?, sigla = ?
      WHERE id = ?
      RETURNING %s
    """.formatted(COLUMNS), ROW_MAPPER, departamento.getNome(), departamento.getSigla(), departamento.getId());
    return list.stream().findFirst();
  }

  @Override
  public boolean delete(Long id) {
    return jdbc.update("DELETE FROM departamento WHERE id = ?", id) > 0;
  }
}
