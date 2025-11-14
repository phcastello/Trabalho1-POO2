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
import org.springframework.web.bind.annotation.RestController;
import poo.controller.dto.ProvaRequest;
import poo.controller.mapper.ProvaMapper;
import poo.model.Prova;
import poo.service.ProvaService;

@RestController
@RequestMapping("/api/provas")
public class ProvaController extends BaseCrudController {

  private final ProvaService service;

  public ProvaController(ProvaService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<Prova> create(@Valid @RequestBody ProvaRequest dto) {
    Prova created = service.create(ProvaMapper.toEntity(dto));
    URI location = createdUri("/{id}", created.getId());
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  public List<Prova> list() {
    return service.listAll();
  }

  @GetMapping("/{id}")
  public Prova get(@PathVariable Long id) {
    return requireFound(service.findById(id), "Prova não encontrada.");
  }

  @PutMapping("/{id}")
  public Prova update(@PathVariable Long id, @Valid @RequestBody ProvaRequest dto) {
    return requireFound(service.update(id, ProvaMapper.toEntity(dto)), "Prova não encontrada.");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    return deleteOrNotFound(service.delete(id), "Prova não encontrada.");
  }
}
