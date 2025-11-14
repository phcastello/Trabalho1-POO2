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
import poo.controller.dto.AlunoRequest;
import poo.controller.mapper.AlunoMapper;
import poo.model.Aluno;
import poo.service.AlunoService;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController extends BaseCrudController {

  private final AlunoService service;

  public AlunoController(AlunoService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<Aluno> create(@Valid @RequestBody AlunoRequest dto) {
    Aluno created = service.create(AlunoMapper.toEntity(dto));
    URI location = createdUri("/{id}", created.getId());
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  public List<Aluno> list() {
    return service.listAll();
  }

  @GetMapping("/{id}")
  public Aluno get(@PathVariable Long id) {
    return requireFound(service.findById(id), "Aluno não encontrado.");
  }

  @PutMapping("/{id}")
  public Aluno update(@PathVariable Long id, @Valid @RequestBody AlunoRequest dto) {
    return requireFound(service.update(id, AlunoMapper.toEntity(dto)), "Aluno não encontrado.");
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    return deleteOrNotFound(service.delete(id), "Aluno não encontrado.");
  }
}
