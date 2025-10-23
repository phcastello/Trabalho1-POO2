package poo.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import poo.dao.DepartamentoDao;
import poo.model.Departamento;
import poo.service.DepartamentoService;

@Service
public class DepartamentoServiceImpl implements DepartamentoService {

  private final DepartamentoDao dao;

  public DepartamentoServiceImpl(DepartamentoDao dao) {
    this.dao = dao;
  }

  @Override
  public Departamento create(Departamento departamento) {
    try {
      return dao.create(departamento);
    } catch (DataAccessException ex) {
      throw translateUpsertException(ex);
    }
  }

  @Override
  public List<Departamento> listAll() {
    return dao.findAll();
  }

  @Override
  public Optional<Departamento> findById(Long id) {
    return dao.findById(id);
  }

  @Override
  public Optional<Departamento> update(Long id, Departamento departamento) {
    Objects.requireNonNull(id, "id não pode ser nulo");
    Departamento toUpdate = new Departamento();
    toUpdate.setId(id);
    toUpdate.setNome(departamento.getNome());
    toUpdate.setSigla(departamento.getSigla());

    try {
      return dao.update(toUpdate);
    } catch (DataAccessException ex) {
      throw translateUpsertException(ex);
    }
  }

  @Override
  public boolean delete(Long id) {
    try {
      return dao.delete(id);
    } catch (DataIntegrityViolationException ex) {
      throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Não é possível remover o departamento porque existem registros relacionados.",
        ex
      );
    }
  }

  private RuntimeException translateUpsertException(DataAccessException ex) {
    if (ex instanceof DuplicateKeyException) {
      return new ResponseStatusException(HttpStatus.CONFLICT, "Departamento com este nome já cadastrado.", ex);
    }
    if (ex instanceof DataIntegrityViolationException) {
      return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos para o departamento.", ex);
    }
    return ex;
  }
}

