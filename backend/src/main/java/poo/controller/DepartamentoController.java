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
import poo.controller.dto.DepartamentoRequest;
import poo.controller.mapper.DepartamentoMapper;
import poo.model.Departamento;
import poo.service.DepartamentoService;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController extends BaseCrudController {

  private final DepartamentoService service;

  public DepartamentoController(DepartamentoService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<Departamento> create(@Valid @RequestBody DepartamentoRequest dto) {
    Departamento created = service.create(DepartamentoMapper.toEntity(dto));
    URI location = createdUri("/{id}", created.getId());
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  public List<Departamento> list() {
    return service.listAll();
  }

  @GetMapping("/{id}")
  public Departamento get(@PathVariable Long id) {
    return requireFound(service.findById(id), "Departamento não encontrado.");
  }

  @PutMapping("/{id}")
  public Departamento update(@PathVariable Long id, @Valid @RequestBody DepartamentoRequest dto) {
    return requireFound(service.update(id, DepartamentoMapper.toEntity(dto)), "Departamento não encontrado.");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    return deleteOrNotFound(service.delete(id), "Departamento não encontrado.");
  }
}
