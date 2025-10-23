package poo.model;

import java.time.Instant;

public class Departamento {
  private Long id;
  private String nome;
  private String sigla;
  private Instant createdAt;
  private Instant updatedAt;

  public Departamento() {}

  public Departamento(Long id, String nome, String sigla, Instant createdAt, Instant updatedAt) {
    this.id = id;
    this.nome = nome;
    this.sigla = sigla;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public String getSigla() {
    return sigla;
  }

  public void setSigla(String sigla) {
    this.sigla = sigla;
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

