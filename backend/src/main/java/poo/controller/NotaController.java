package poo.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
import poo.controller.dto.NotaCreateRequest;
import poo.controller.dto.NotaUpdateRequest;
import poo.controller.mapper.NotaMapper;
import poo.model.Nota;
import poo.service.NotaService;

@RestController
@RequestMapping("/api/notas")
public class NotaController extends BaseCrudController {

  private final NotaService service;

  public NotaController(NotaService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<Nota> create(@Valid @RequestBody NotaCreateRequest dto) {
    Nota created = service.create(NotaMapper.toEntity(dto));
    URI location = createdUri("/{alunoId}/{provaId}", created.getAlunoId(), created.getProvaId());
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
    return requireFound(service.findById(alunoId, provaId), "Nota não encontrada.");
  }

  @PutMapping("/{alunoId}/{provaId}")
  public Nota update(
    @PathVariable Long alunoId,
    @PathVariable Long provaId,
    @Valid @RequestBody NotaUpdateRequest dto
  ) {
    return requireFound(
      service.update(alunoId, provaId, NotaMapper.toEntity(alunoId, provaId, dto)),
      "Nota não encontrada."
    );
  }

  @DeleteMapping("/{alunoId}/{provaId}")
  public ResponseEntity<Void> delete(@PathVariable Long alunoId, @PathVariable Long provaId) {
    return deleteOrNotFound(service.delete(alunoId, provaId), "Nota não encontrada.");
  }
}
