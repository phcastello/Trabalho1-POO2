package poo.dao;

import java.util.List;
import poo.model.consultas.AlunoCoberturaNotas;
import poo.model.consultas.AlunoModalidadeEquilibrada;
import poo.model.consultas.DepartamentoDesempenho;

public interface ConsultasAvancadasDao {
  List<DepartamentoDesempenho> listarRankingDepartamentos();
  List<AlunoModalidadeEquilibrada> listarAlunosModalidades();
  List<AlunoCoberturaNotas> listarCoberturaNotas();
}
