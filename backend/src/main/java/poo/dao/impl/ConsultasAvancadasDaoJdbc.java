package poo.dao.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import poo.dao.ConsultasAvancadasDao;
import poo.model.consultas.AlunoCoberturaNotas;
import poo.model.consultas.AlunoModalidadeEquilibrada;
import poo.model.consultas.DepartamentoDesempenho;

@Repository
public class ConsultasAvancadasDaoJdbc implements ConsultasAvancadasDao {

  private static final @NonNull String SQL_RANKING_DEPARTAMENTOS = nonNull("""
      SELECT
        d.id AS departamento_id,
        d.nome AS departamento_nome,
        ROUND(AVG(n.valor)::numeric, 2) AS media_notas,
        MIN(n.valor) AS menor_nota,
        MAX(n.valor) AS maior_nota,
        COUNT(DISTINCT n.aluno_id) AS alunos_avaliados,
        COUNT(*) AS notas_lancadas
      FROM departamento d
      JOIN aluno a ON a.departamento_id = d.id
      JOIN nota n ON n.aluno_id = a.id
      GROUP BY d.id, d.nome
      ORDER BY media_notas DESC, notas_lancadas DESC
    """);

  private static final @NonNull String SQL_MODALIDADES = nonNull("""
      WITH alunos_prova AS (
        SELECT n.aluno_id, COUNT(DISTINCT n.prova_id) AS avaliacoes_prova
        FROM nota n
        JOIN prova p ON p.id = n.prova_id
        WHERE p.titulo ILIKE '%prova%'
        GROUP BY n.aluno_id
      ),
      alunos_projeto AS (
        SELECT n.aluno_id, COUNT(DISTINCT n.prova_id) AS projetos_entregues
        FROM nota n
        JOIN prova p ON p.id = n.prova_id
        WHERE p.titulo ILIKE '%projeto%'
        GROUP BY n.aluno_id
      ),
      intersecao AS (
        SELECT aluno_id FROM alunos_prova
        INTERSECT
        SELECT aluno_id FROM alunos_projeto
      )
      SELECT
        a.id AS aluno_id,
        a.ra,
        a.nome,
        d.nome AS departamento_nome,
        ap.avaliacoes_prova,
        apr.projetos_entregues
      FROM intersecao i
      JOIN aluno a ON a.id = i.aluno_id
      JOIN departamento d ON d.id = a.departamento_id
      JOIN alunos_prova ap ON ap.aluno_id = i.aluno_id
      JOIN alunos_projeto apr ON apr.aluno_id = i.aluno_id
      ORDER BY a.nome
    """);

  private static final @NonNull String SQL_COBERTURA_NOTAS = nonNull("""
      SELECT
        a.id AS aluno_id,
        a.ra,
        a.nome,
        d.nome AS departamento_nome,
        COUNT(n.prova_id) AS provas_avaliadas,
        ROUND(COALESCE(AVG(n.valor), 0)::numeric, 2) AS media_notas,
        CASE WHEN COUNT(n.prova_id) = 0 THEN TRUE ELSE FALSE END AS sem_notas
      FROM aluno a
      LEFT JOIN nota n ON n.aluno_id = a.id
      JOIN departamento d ON d.id = a.departamento_id
      GROUP BY a.id, a.ra, a.nome, d.nome
      ORDER BY sem_notas DESC, a.nome
    """);

  private static final @NonNull RowMapper<DepartamentoDesempenho> DESEMPENHO_ROW_MAPPER = (rs, rowNum) -> {
    DepartamentoDesempenho dto = new DepartamentoDesempenho();
    dto.setDepartamentoId(rs.getLong("departamento_id"));
    dto.setDepartamentoNome(rs.getString("departamento_nome"));
    dto.setMediaNotas(rs.getObject("media_notas", BigDecimal.class));
    dto.setMenorNota(rs.getObject("menor_nota", BigDecimal.class));
    dto.setMaiorNota(rs.getObject("maior_nota", BigDecimal.class));
    dto.setAlunosAvaliados(rs.getInt("alunos_avaliados"));
    dto.setNotasLancadas(rs.getInt("notas_lancadas"));
    return dto;
  };

  private static final @NonNull RowMapper<AlunoModalidadeEquilibrada> MODALIDADES_ROW_MAPPER = (rs, rowNum) -> {
    AlunoModalidadeEquilibrada dto = new AlunoModalidadeEquilibrada();
    dto.setAlunoId(rs.getLong("aluno_id"));
    dto.setRa(rs.getString("ra"));
    dto.setNome(rs.getString("nome"));
    dto.setDepartamentoNome(rs.getString("departamento_nome"));
    dto.setAvaliacoesProva(rs.getInt("avaliacoes_prova"));
    dto.setProjetosEntregues(rs.getInt("projetos_entregues"));
    return dto;
  };

  private static final @NonNull RowMapper<AlunoCoberturaNotas> COBERTURA_ROW_MAPPER = (rs, rowNum) -> {
    AlunoCoberturaNotas dto = new AlunoCoberturaNotas();
    dto.setAlunoId(rs.getLong("aluno_id"));
    dto.setRa(rs.getString("ra"));
    dto.setNome(rs.getString("nome"));
    dto.setDepartamentoNome(rs.getString("departamento_nome"));
    dto.setProvasAvaliadas(rs.getInt("provas_avaliadas"));
    dto.setMediaNotas(rs.getObject("media_notas", BigDecimal.class));
    dto.setSemNotas(rs.getBoolean("sem_notas"));
    return dto;
  };

  private final JdbcTemplate jdbc;

  public ConsultasAvancadasDaoJdbc(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public List<DepartamentoDesempenho> listarRankingDepartamentos() {
    return jdbc.query(SQL_RANKING_DEPARTAMENTOS, DESEMPENHO_ROW_MAPPER);
  }

  @Override
  public List<AlunoModalidadeEquilibrada> listarAlunosModalidades() {
    return jdbc.query(SQL_MODALIDADES, MODALIDADES_ROW_MAPPER);
  }

  @Override
  public List<AlunoCoberturaNotas> listarCoberturaNotas() {
    return jdbc.query(SQL_COBERTURA_NOTAS, COBERTURA_ROW_MAPPER);
  }

  @NonNull
  private static <T> T nonNull(T value) {
    if (value == null) {
      throw new IllegalStateException("Unexpected null value");
    }
    return value;
  }
}
