package poo.dao;

import java.util.List;
import java.util.Optional;
import poo.model.Aluno;

public interface AlunoDao {
  Aluno create(Aluno aluno);
  Optional<Aluno> findById(Long id);
  List<Aluno> findAll();
  Optional<Aluno> update(Aluno aluno);
  boolean delete(Long id);
}

