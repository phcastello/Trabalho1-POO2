package poo.dao;

import java.util.List;
import java.util.Optional;
import poo.model.Departamento;

public interface DepartamentoDao {
  Departamento create(Departamento departamento);
  Optional<Departamento> findById(Long id);
  List<Departamento> findAll();
  Optional<Departamento> update(Departamento departamento);
  boolean delete(Long id);
}

