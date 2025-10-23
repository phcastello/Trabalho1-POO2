package poo.service;

import java.util.List;
import java.util.Optional;
import poo.model.Departamento;

public interface DepartamentoService {
  Departamento create(Departamento departamento);
  List<Departamento> listAll();
  Optional<Departamento> findById(Long id);
  Optional<Departamento> update(Long id, Departamento departamento);
  boolean delete(Long id);
}

