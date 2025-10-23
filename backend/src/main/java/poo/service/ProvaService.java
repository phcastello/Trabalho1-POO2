package poo.service;

import java.util.List;
import java.util.Optional;
import poo.model.Prova;

public interface ProvaService {
  Prova create(Prova prova);
  List<Prova> listAll();
  Optional<Prova> findById(Long id);
  Optional<Prova> update(Long id, Prova prova);
  boolean delete(Long id);
}

