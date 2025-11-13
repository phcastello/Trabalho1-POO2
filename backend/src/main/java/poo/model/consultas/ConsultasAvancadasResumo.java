package poo.model.consultas;

import java.util.ArrayList;
import java.util.List;

public class ConsultasAvancadasResumo {

  private List<DepartamentoDesempenho> rankingDepartamentos = new ArrayList<>();
  private List<AlunoModalidadeEquilibrada> alunosModalidades = new ArrayList<>();
  private List<AlunoCoberturaNotas> coberturaNotas = new ArrayList<>();

  public List<DepartamentoDesempenho> getRankingDepartamentos() {
    return rankingDepartamentos;
  }

  public void setRankingDepartamentos(List<DepartamentoDesempenho> rankingDepartamentos) {
    this.rankingDepartamentos = rankingDepartamentos != null ? rankingDepartamentos : new ArrayList<>();
  }

  public List<AlunoModalidadeEquilibrada> getAlunosModalidades() {
    return alunosModalidades;
  }

  public void setAlunosModalidades(List<AlunoModalidadeEquilibrada> alunosModalidades) {
    this.alunosModalidades = alunosModalidades != null ? alunosModalidades : new ArrayList<>();
  }

  public List<AlunoCoberturaNotas> getCoberturaNotas() {
    return coberturaNotas;
  }

  public void setCoberturaNotas(List<AlunoCoberturaNotas> coberturaNotas) {
    this.coberturaNotas = coberturaNotas != null ? coberturaNotas : new ArrayList<>();
  }
}
