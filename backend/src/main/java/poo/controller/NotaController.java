package poo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import poo.model.Nota;
import poo.service.NotaService;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

  private final NotaService service;

  public NotaController(NotaService service) {
    this.service = service;
  }

  public static class NotaCreateRequest {
    @NotNull public Long alunoId;
    @NotNull public Long provaId;
    @NotNull @DecimalMin(value = "0.0") @DecimalMax(value = "10.0") public BigDecimal valor;
    public String observacao;
  }

  public static class NotaUpdateRequest {
    @NotNull @DecimalMin(value = "0.0") @DecimalMax(value = "10.0") public BigDecimal valor;
    public String observacao;
  }

  @PostMapping
  public ResponseEntity<Nota> create(@Valid @RequestBody NotaCreateRequest dto) {
    Nota nova = toNota(dto);
    Nota created = service.create(nova);

    URI location = ServletUriComponentsBuilder
      .fromCurrentRequest()
      .path("/{alunoId}/{provaId}")
      .buildAndExpand(created.getAlunoId(), created.getProvaId())
      .toUri();

    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  public List<Nota> list(
    @RequestParam(name = "alunoId", required = false) Long alunoId,
    @RequestParam(name = "provaId", required = false) Long provaId
  ) {
    return service.listAll(alunoId, provaId);
  }

  @GetMapping("/{alunoId}/{provaId}")
  public Nota get(@PathVariable Long alunoId, @PathVariable Long provaId) {
    return service
      .findById(alunoId, provaId)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota não encontrada."));
  }

  @PutMapping("/{alunoId}/{provaId}")
  public Nota update(
    @PathVariable Long alunoId,
    @PathVariable Long provaId,
    @Valid @RequestBody NotaUpdateRequest dto
  ) {
    Nota dados = toNota(alunoId, provaId, dto);
    return service
      .update(alunoId, provaId, dados)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota não encontrada."));
  }

  @DeleteMapping("/{alunoId}/{provaId}")
  public ResponseEntity<Void> delete(@PathVariable Long alunoId, @PathVariable Long provaId) {
    boolean removed = service.delete(alunoId, provaId);
    if (!removed) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota não encontrada.");
    }
    return ResponseEntity.noContent().build();
  }

  private static Nota toNota(NotaCreateRequest dto) {
    Nota nota = new Nota();
    nota.setAlunoId(dto.alunoId);
    nota.setProvaId(dto.provaId);
    nota.setValor(dto.valor);
    nota.setObservacao(normalizeObservacao(dto.observacao));
    return nota;
  }

  private static Nota toNota(Long alunoId, Long provaId, NotaUpdateRequest dto) {
    Nota nota = new Nota();
    nota.setAlunoId(alunoId);
    nota.setProvaId(provaId);
    nota.setValor(dto.valor);
    nota.setObservacao(normalizeObservacao(dto.observacao));
    return nota;
  }

  private static String normalizeObservacao(String observacao) {
    if (observacao == null) return null;
    String trimmed = observacao.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }
}

