package poo.model;

import java.time.Instant;
import java.time.LocalDate;

public class Prova {
  private Long id;
  private Long departamentoId;
  private String titulo;
  private LocalDate data;
  private String descricao;
  private Instant createdAt;
  private Instant updatedAt;

  public Prova() {}

  public Prova(
    Long id,
    Long departamentoId,
    String titulo,
    LocalDate data,
    String descricao,
    Instant createdAt,
    Instant updatedAt
  ) {
    this.id = id;
    this.departamentoId = departamentoId;
    this.titulo = titulo;
    this.data = data;
    this.descricao = descricao;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getDepartamentoId() {
    return departamentoId;
  }

  public void setDepartamentoId(Long departamentoId) {
    this.departamentoId = departamentoId;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public LocalDate getData() {
    return data;
  }

  public void setData(LocalDate data) {
    this.data = data;
  }

  public String getDescricao() {
    return descricao;
  }

  public void setDescricao(String descricao) {
    this.descricao = descricao;
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

