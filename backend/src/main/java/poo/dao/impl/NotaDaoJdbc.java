package poo.dao.impl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import poo.dao.NotaDao;
import poo.model.Nota;

@Repository
public class NotaDaoJdbc implements NotaDao {

  private static final String COLUMNS = "aluno_id, prova_id, valor, observacao, created_at, updated_at";

  private static final RowMapper<Nota> ROW_MAPPER = (rs, rowNum) -> {
    Nota nota = new Nota();
    nota.setAlunoId(rs.getLong("aluno_id"));
    nota.setProvaId(rs.getLong("prova_id"));
    nota.setValor(rs.getObject("valor", BigDecimal.class));
    nota.setObservacao(rs.getString("observacao"));

    OffsetDateTime created = rs.getObject("created_at", OffsetDateTime.class);
    if (created != null) {
      nota.setCreatedAt(created.toInstant());
    }
    OffsetDateTime updated = rs.getObject("updated_at", OffsetDateTime.class);
    if (updated != null) {
      nota.setUpdatedAt(updated.toInstant());
    }
    return nota;
  };

  private final JdbcTemplate jdbc;

  public NotaDaoJdbc(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Nota create(Nota nota) {
    return jdbc.queryForObject("""
      INSERT INTO nota (aluno_id, prova_id, valor, observacao)
      VALUES (?, ?, ?, ?)
      RETURNING %s
    """.formatted(COLUMNS), ROW_MAPPER,
      nota.getAlunoId(),
      nota.getProvaId(),
      nota.getValor(),
      nota.getObservacao()
    );
  }

  @Override
  public Optional<Nota> findById(Long alunoId, Long provaId) {
    List<Nota> list = jdbc.query("""
      SELECT %s FROM nota
      WHERE aluno_id = ? AND prova_id = ?
    """.formatted(COLUMNS), ROW_MAPPER, alunoId, provaId);
    return list.stream().findFirst();
  }

  @Override
  public List<Nota> findAll(Long alunoId, Long provaId) {
    StringBuilder sql = new StringBuilder("SELECT %s FROM nota".formatted(COLUMNS));
    List<Object> params = new ArrayList<>();
    boolean hasWhere = false;

    if (alunoId != null) {
      sql.append(hasWhere ? " AND" : " WHERE").append(" aluno_id = ?");
      params.add(alunoId);
      hasWhere = true;
    }
    if (provaId != null) {
      sql.append(hasWhere ? " AND" : " WHERE").append(" prova_id = ?");
      params.add(provaId);
    }

    sql.append(" ORDER BY prova_id, aluno_id");
    return jdbc.query(sql.toString(), ROW_MAPPER, params.toArray());
  }

  @Override
  public Optional<Nota> update(Nota nota) {
    List<Nota> list = jdbc.query("""
      UPDATE nota
      SET valor = ?, observacao = ?
      WHERE aluno_id = ? AND prova_id = ?
      RETURNING %s
    """.formatted(COLUMNS), ROW_MAPPER,
      nota.getValor(),
      nota.getObservacao(),
      nota.getAlunoId(),
      nota.getProvaId()
    );
    return list.stream().findFirst();
  }

  @Override
  public boolean delete(Long alunoId, Long provaId) {
    return jdbc.update("DELETE FROM nota WHERE aluno_id = ? AND prova_id = ?", alunoId, provaId) > 0;
  }
}
