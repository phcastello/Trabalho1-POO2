package poo.service;

import java.util.List;
import java.util.Optional;
import poo.model.Nota;

public interface NotaService {
  Nota create(Nota nota);
  List<Nota> listAll(Long alunoId, Long provaId);
  Optional<Nota> findById(Long alunoId, Long provaId);
  Optional<Nota> update(Long alunoId, Long provaId, Nota nota);
  boolean delete(Long alunoId, Long provaId);
}

