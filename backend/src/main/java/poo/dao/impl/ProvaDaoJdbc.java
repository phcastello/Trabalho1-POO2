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
import poo.dao.ProvaDao;
import poo.model.Prova;

@Repository
public class ProvaDaoJdbc implements ProvaDao {

  private static final String COLUMNS = "id, departamento_id, titulo, data, descricao, created_at, updated_at";

  private static final RowMapper<Prova> ROW_MAPPER = (rs, rowNum) -> {
    Prova prova = new Prova();
    prova.setId(rs.getLong("id"));
    prova.setDepartamentoId(rs.getLong("departamento_id"));
    prova.setTitulo(rs.getString("titulo"));
    prova.setData(rs.getObject("data", LocalDate.class));
    prova.setDescricao(rs.getString("descricao"));

    OffsetDateTime created = rs.getObject("created_at", OffsetDateTime.class);
    if (created != null) {
      prova.setCreatedAt(created.toInstant());
    }
    OffsetDateTime updated = rs.getObject("updated_at", OffsetDateTime.class);
    if (updated != null) {
      prova.setUpdatedAt(updated.toInstant());
    }
    return prova;
  };

  private final JdbcTemplate jdbc;

  public ProvaDaoJdbc(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Prova create(Prova prova) {
    return jdbc.queryForObject("""
      INSERT INTO prova (departamento_id, titulo, data, descricao)
      VALUES (?, ?, ?, ?)
      RETURNING %s
    """.formatted(COLUMNS), ROW_MAPPER,
      prova.getDepartamentoId(),
      prova.getTitulo(),
      toSqlDate(prova.getData()),
      prova.getDescricao()
    );
  }

  @Override
  public Optional<Prova> findById(Long id) {
    List<Prova> list =
      jdbc.query("SELECT %s FROM prova WHERE id = ?".formatted(COLUMNS), ROW_MAPPER, id);
    return list.stream().findFirst();
  }

  @Override
  public List<Prova> findAll() {
    return jdbc.query("SELECT %s FROM prova ORDER BY data DESC, titulo".formatted(COLUMNS), ROW_MAPPER);
  }

  @Override
  public Optional<Prova> update(Prova prova) {
    Objects.requireNonNull(prova.getId(), "Prova id must not be null");
    List<Prova> list = jdbc.query("""
      UPDATE prova
      SET departamento_id = ?, titulo = ?, data = ?, descricao = ?
      WHERE id = ?
      RETURNING %s
    """.formatted(COLUMNS), ROW_MAPPER,
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
    return jdbc.update("DELETE FROM prova WHERE id = ?", id) > 0;
  }

  private static Date toSqlDate(LocalDate data) {
    return data != null ? Date.valueOf(data) : null;
  }
}

