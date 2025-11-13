package poo.model.consultas;

import java.math.BigDecimal;

public class DepartamentoDesempenho {

  private Long departamentoId;
  private String departamentoNome;
  private BigDecimal mediaNotas;
  private BigDecimal menorNota;
  private BigDecimal maiorNota;
  private Integer alunosAvaliados;
  private Integer notasLancadas;

  public Long getDepartamentoId() {
    return departamentoId;
  }

  public void setDepartamentoId(Long departamentoId) {
    this.departamentoId = departamentoId;
  }

  public String getDepartamentoNome() {
    return departamentoNome;
  }

  public void setDepartamentoNome(String departamentoNome) {
    this.departamentoNome = departamentoNome;
  }

  public BigDecimal getMediaNotas() {
    return mediaNotas;
  }

  public void setMediaNotas(BigDecimal mediaNotas) {
    this.mediaNotas = mediaNotas;
  }

  public BigDecimal getMenorNota() {
    return menorNota;
  }

  public void setMenorNota(BigDecimal menorNota) {
    this.menorNota = menorNota;
  }

  public BigDecimal getMaiorNota() {
    return maiorNota;
  }

  public void setMaiorNota(BigDecimal maiorNota) {
    this.maiorNota = maiorNota;
  }

  public Integer getAlunosAvaliados() {
    return alunosAvaliados;
  }

  public void setAlunosAvaliados(Integer alunosAvaliados) {
    this.alunosAvaliados = alunosAvaliados;
  }

  public Integer getNotasLancadas() {
    return notasLancadas;
  }

  public void setNotasLancadas(Integer notasLancadas) {
    this.notasLancadas = notasLancadas;
  }
}
