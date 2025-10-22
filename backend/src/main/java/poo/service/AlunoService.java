package poo.service;

import java.util.List;
import java.util.Optional;
import poo.model.Aluno;

public interface AlunoService {
  Aluno create(Aluno aluno);
  List<Aluno> listAll();
  Optional<Aluno> findById(Long id);
  Optional<Aluno> update(Long id, Aluno aluno);
  boolean delete(Long id);
}

