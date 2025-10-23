package poo.dao;

import java.util.List;
import java.util.Optional;
import poo.model.Nota;

public interface NotaDao {
  Nota create(Nota nota);
  Optional<Nota> findById(Long alunoId, Long provaId);
  List<Nota> findAll(Long alunoId, Long provaId);
  Optional<Nota> update(Nota nota);
  boolean delete(Long alunoId, Long provaId);
}

