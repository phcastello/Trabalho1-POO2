package poo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.net.URI;
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
import poo.model.Departamento;
import poo.service.DepartamentoService;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {

  private final DepartamentoService service;

  public DepartamentoController(DepartamentoService service) {
    this.service = service;
  }

  public static class DepartamentoRequest {
    @NotBlank public String nome;
    @Size(min = 1, max = 10) public String sigla;
  }

  @PostMapping
  public ResponseEntity<Departamento> create(@Valid @RequestBody DepartamentoRequest dto) {
    Departamento novo = toDepartamento(dto);
    Departamento created = service.create(novo);

    URI location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(created.getId())
      .toUri();

    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  public List<Departamento> list() {
    return service.listAll();
  }

  @GetMapping("/{id}")
  public Departamento get(@PathVariable Long id) {
    return service
      .findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Departamento não encontrado."));
  }

  @PutMapping("/{id}")
  public Departamento update(@PathVariable Long id, @Valid @RequestBody DepartamentoRequest dto) {
    Departamento dados = toDepartamento(dto);
    return service
      .update(id, dados)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Departamento não encontrado."));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean removed = service.delete(id);
    if (!removed) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Departamento não encontrado.");
    }
    return ResponseEntity.noContent().build();
  }

  private static Departamento toDepartamento(DepartamentoRequest dto) {
    Departamento departamento = new Departamento();
    departamento.setNome(dto.nome != null ? dto.nome.trim() : null);
    departamento.setSigla(normalizeSigla(dto.sigla));
    return departamento;
  }

  private static String normalizeSigla(String sigla) {
    if (sigla == null) return null;
    String trimmed = sigla.trim();
    return trimmed.isEmpty() ? null : trimmed.toUpperCase();
  }
}

