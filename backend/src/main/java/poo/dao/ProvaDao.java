package poo.dao;

import java.util.List;
import java.util.Optional;
import poo.model.Prova;

public interface ProvaDao {
  Prova create(Prova prova);
  Optional<Prova> findById(Long id);
  List<Prova> findAll();
  Optional<Prova> update(Prova prova);
  boolean delete(Long id);
}

