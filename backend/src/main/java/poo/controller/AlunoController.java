package poo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import poo.model.Aluno;
import poo.service.AlunoService;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

  private final AlunoService service;

  public AlunoController(AlunoService service) {
    this.service = service;
  }

  public static class AlunoRequest {
    @NotBlank public String ra;
    @NotBlank public String nome;
    @Email public String email;
    @NotNull public Long departamentoId;
    @PastOrPresent public LocalDate dataNascimento;
  }

  @PostMapping
  public ResponseEntity<Aluno> create(@Valid @RequestBody AlunoRequest dto) {
    Aluno novo = toAluno(dto);
    Aluno created = service.create(novo);

    URI location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(created.getId())
      .toUri();

    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  public List<Aluno> list() {
    return service.listAll();
  }

  @GetMapping("/{id}")
  public Aluno get(@PathVariable Long id) {
    return service
      .findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
  }

  @PutMapping("/{id}")
  public Aluno update(@PathVariable Long id, @Valid @RequestBody AlunoRequest dto) {
    Aluno dados = toAluno(dto);
    return service
      .update(id, dados)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado."));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean removed = service.delete(id);
    if (!removed) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado.");
    }
    return ResponseEntity.noContent().build();
  }

  private static Aluno toAluno(AlunoRequest dto) {
    Aluno aluno = new Aluno();
    aluno.setRa(dto.ra != null ? dto.ra.trim() : null);
    aluno.setNome(dto.nome != null ? dto.nome.trim() : null);
    aluno.setEmail(normalizeEmail(dto.email));
    aluno.setDepartamentoId(dto.departamentoId);
    aluno.setDataNascimento(dto.dataNascimento);
    return aluno;
  }

  private static String normalizeEmail(String email) {
    return (email == null || email.isBlank()) ? null : email.trim();
  }
}

