package poo.model.consultas;

public class AlunoModalidadeEquilibrada {

  private Long alunoId;
  private String ra;
  private String nome;
  private String departamentoNome;
  private Integer avaliacoesProva;
  private Integer projetosEntregues;

  public Long getAlunoId() {
    return alunoId;
  }

  public void setAlunoId(Long alunoId) {
    this.alunoId = alunoId;
  }

  public String getRa() {
    return ra;
  }

  public void setRa(String ra) {
    this.ra = ra;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getDepartamentoNome() {
    return departamentoNome;
  }

  public void setDepartamentoNome(String departamentoNome) {
    this.departamentoNome = departamentoNome;
  }

  public Integer getAvaliacoesProva() {
    return avaliacoesProva;
  }

  public void setAvaliacoesProva(Integer avaliacoesProva) {
    this.avaliacoesProva = avaliacoesProva;
  }

  public Integer getProjetosEntregues() {
    return projetosEntregues;
  }

  public void setProjetosEntregues(Integer projetosEntregues) {
    this.projetosEntregues = projetosEntregues;
  }
}
