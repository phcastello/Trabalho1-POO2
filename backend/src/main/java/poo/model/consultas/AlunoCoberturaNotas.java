package poo.model.consultas;

import java.math.BigDecimal;

public class AlunoCoberturaNotas {

  private Long alunoId;
  private String ra;
  private String nome;
  private String departamentoNome;
  private Integer provasAvaliadas;
  private BigDecimal mediaNotas;
  private Boolean semNotas;

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

  public Integer getProvasAvaliadas() {
    return provasAvaliadas;
  }

  public void setProvasAvaliadas(Integer provasAvaliadas) {
    this.provasAvaliadas = provasAvaliadas;
  }

  public BigDecimal getMediaNotas() {
    return mediaNotas;
  }

  public void setMediaNotas(BigDecimal mediaNotas) {
    this.mediaNotas = mediaNotas;
  }

  public Boolean getSemNotas() {
    return semNotas;
  }

  public void setSemNotas(Boolean semNotas) {
    this.semNotas = semNotas;
  }
}
