package poo.model;

import java.math.BigDecimal;
import java.time.Instant;

public class Nota {
  private Long alunoId;
  private Long provaId;
  private BigDecimal valor;
  private String observacao;
  private Instant createdAt;
  private Instant updatedAt;

  public Nota() {}

  public Nota(
    Long alunoId,
    Long provaId,
    BigDecimal valor,
    String observacao,
    Instant createdAt,
    Instant updatedAt
  ) {
    this.alunoId = alunoId;
    this.provaId = provaId;
    this.valor = valor;
    this.observacao = observacao;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getAlunoId() {
    return alunoId;
  }

  public void setAlunoId(Long alunoId) {
    this.alunoId = alunoId;
  }

  public Long getProvaId() {
    return provaId;
  }

  public void setProvaId(Long provaId) {
    this.provaId = provaId;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  public String getObservacao() {
    return observacao;
  }

  public void setObservacao(String observacao) {
    this.observacao = observacao;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}

