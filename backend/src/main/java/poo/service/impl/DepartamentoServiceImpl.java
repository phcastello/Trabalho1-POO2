package poo.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import poo.dao.DepartamentoDao;
import poo.model.Departamento;
import poo.service.DepartamentoService;
import poo.service.support.CrudServiceSupport;
import poo.service.support.CrudServiceSupport.UpsertErrorDescriptor;

@Service
public class DepartamentoServiceImpl implements DepartamentoService {

  private final DepartamentoDao dao;
  private final CrudServiceSupport support;

  private static final @NonNull UpsertErrorDescriptor DEPARTAMENTO_ERRORS = CrudServiceSupport.conflictBadRequest(
    "Departamento com este nome já cadastrado.",
    "Dados inválidos para o departamento."
  );

  public DepartamentoServiceImpl(DepartamentoDao dao, CrudServiceSupport support) {
    this.dao = dao;
    this.support = support;
  }

  @Override
  public Departamento create(Departamento departamento) {
    try {
      return dao.create(departamento);
    } catch (DataAccessException ex) {
      throw support.translateUpsertException(ex, DEPARTAMENTO_ERRORS);
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
    Departamento toUpdate = support
      .updater(Departamento::new)
      .withId(id, Departamento::setId, "id")
      .copy(departamento, (target, source) -> {
        target.setNome(source.getNome());
        target.setSigla(source.getSigla());
      })
      .build();

    try {
      return dao.update(toUpdate);
    } catch (DataAccessException ex) {
      throw support.translateUpsertException(ex, DEPARTAMENTO_ERRORS);
    }
  }

  @Override
  public boolean delete(Long id) {
    try {
      return dao.delete(id);
    } catch (DataIntegrityViolationException ex) {
      throw support.translateDeleteException(
        ex,
        "Não é possível remover o departamento porque existem registros relacionados."
      );
    }
  }
}
