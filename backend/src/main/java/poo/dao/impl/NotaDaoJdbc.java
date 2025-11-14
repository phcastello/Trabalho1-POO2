package poo.dao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import poo.dao.NotaDao;
import poo.dao.support.JdbcTableMetadata;
import poo.model.Nota;

@Repository
public class NotaDaoJdbc implements NotaDao {

  private static final @NonNull JdbcTableMetadata<Nota> TABLE = JdbcTableMetadata
    .<Nota>builder("nota")
    .columns("aluno_id", "prova_id", "valor", "observacao")
    .auditable()
    .defaultOrderBy("prova_id, aluno_id")
    .rowMapper((rs, rowNum) -> {
      Nota nota = new Nota();
      nota.setAlunoId(rs.getLong("aluno_id"));
      nota.setProvaId(rs.getLong("prova_id"));
      nota.setValor(rs.getObject("valor", BigDecimal.class));
      nota.setObservacao(rs.getString("observacao"));
      JdbcTableMetadata.populateAuditColumns(rs, nota::setCreatedAt, nota::setUpdatedAt);
      return nota;
    })
    .build();

  private static final @NonNull RowMapper<Nota> ROW_MAPPER = TABLE.rowMapper();
  private static final @NonNull String INSERT_SQL = TABLE.insertReturningSql("aluno_id, prova_id, valor, observacao");
  private static final @NonNull String SELECT_BY_ID_SQL = TABLE.selectByIdSql("aluno_id = ? AND prova_id = ?");
  private static final @NonNull String UPDATE_SQL = TABLE.updateReturningSql(
    "valor = ?, observacao = ?",
    "aluno_id = ? AND prova_id = ?"
  );
  private static final @NonNull String DELETE_SQL = TABLE.deleteSql("aluno_id = ? AND prova_id = ?");

  private final JdbcTemplate jdbc;

  public NotaDaoJdbc(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Nota create(Nota nota) {
    return jdbc.queryForObject(INSERT_SQL, ROW_MAPPER,
      nota.getAlunoId(),
      nota.getProvaId(),
      nota.getValor(),
      nota.getObservacao()
    );
  }

  @Override
  public Optional<Nota> findById(Long alunoId, Long provaId) {
    List<Nota> list = jdbc.query(SELECT_BY_ID_SQL, ROW_MAPPER, alunoId, provaId);
    return list.stream().findFirst();
  }

  @Override
  public List<Nota> findAll(Long alunoId, Long provaId) {
    StringBuilder sql = new StringBuilder(TABLE.baseSelectSql());
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

    if (TABLE.orderByClause() != null) {
      sql.append(" ORDER BY ").append(TABLE.orderByClause());
    }
    return jdbc.query(Objects.requireNonNull(sql.toString()), ROW_MAPPER, params.toArray());
  }

  @Override
  public Optional<Nota> update(Nota nota) {
    List<Nota> list = jdbc.query(UPDATE_SQL, ROW_MAPPER,
      nota.getValor(),
      nota.getObservacao(),
      nota.getAlunoId(),
      nota.getProvaId()
    );
    return list.stream().findFirst();
  }

  @Override
  public boolean delete(Long alunoId, Long provaId) {
    return jdbc.update(DELETE_SQL, alunoId, provaId) > 0;
  }
}
