package poo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
import poo.model.Prova;
import poo.service.ProvaService;

@RestController
@RequestMapping("/api/provas")
public class ProvaController {

  private final ProvaService service;

  public ProvaController(ProvaService service) {
    this.service = service;
  }

  public static class ProvaRequest {
    @NotNull public Long departamentoId;
    @NotBlank public String titulo;
    @NotNull public LocalDate data;
    public String descricao;
  }

  @PostMapping
  public ResponseEntity<Prova> create(@Valid @RequestBody ProvaRequest dto) {
    Prova nova = toProva(dto);
    Prova created = service.create(nova);

    URI location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{id}")
      .buildAndExpand(created.getId())
      .toUri();

    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  public List<Prova> list() {
    return service.listAll();
  }

  @GetMapping("/{id}")
  public Prova get(@PathVariable Long id) {
    return service
      .findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não encontrada."));
  }

  @PutMapping("/{id}")
  public Prova update(@PathVariable Long id, @Valid @RequestBody ProvaRequest dto) {
    Prova dados = toProva(dto);
    return service
      .update(id, dados)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não encontrada."));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean removed = service.delete(id);
    if (!removed) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prova não encontrada.");
    }
    return ResponseEntity.noContent().build();
  }

  private static Prova toProva(ProvaRequest dto) {
    Prova prova = new Prova();
    prova.setDepartamentoId(dto.departamentoId);
    prova.setTitulo(dto.titulo != null ? dto.titulo.trim() : null);
    prova.setData(dto.data);
    prova.setDescricao(normalizeDescricao(dto.descricao));
    return prova;
  }

  private static String normalizeDescricao(String descricao) {
    if (descricao == null) return null;
    String trimmed = descricao.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}
