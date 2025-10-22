package poo.model;

import java.time.Instant;
import java.time.LocalDate;

public class Aluno {
  private Long id;
  private String ra;
  private String nome;
  private String email;
  private Long departamentoId;
  private LocalDate dataNascimento;
  private Instant createdAt;
  private Instant updatedAt;

  public Aluno() {}

  public Aluno(
    Long id,
    String ra,
    String nome,
    String email,
    Long departamentoId,
    LocalDate dataNascimento,
    Instant createdAt,
    Instant updatedAt
  ) {
    this.id = id;
    this.ra = ra;
    this.nome = nome;
    this.email = email;
    this.departamentoId = departamentoId;
    this.dataNascimento = dataNascimento;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getDepartamentoId() {
    return departamentoId;
  }

  public void setDepartamentoId(Long departamentoId) {
    this.departamentoId = departamentoId;
  }

  public LocalDate getDataNascimento() {
    return dataNascimento;
  }

  public void setDataNascimento(LocalDate dataNascimento) {
    this.dataNascimento = dataNascimento;
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
