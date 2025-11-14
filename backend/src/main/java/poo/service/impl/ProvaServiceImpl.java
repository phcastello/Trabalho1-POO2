package poo.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import poo.dao.ProvaDao;
import poo.model.Prova;
import poo.service.ProvaService;
import poo.service.support.CrudServiceSupport;
import poo.service.support.CrudServiceSupport.UpsertErrorDescriptor;

@Service
public class ProvaServiceImpl implements ProvaService {

  private final ProvaDao dao;
  private final CrudServiceSupport support;

  private static final @NonNull UpsertErrorDescriptor PROVA_ERRORS = CrudServiceSupport.conflictBadRequest(
    "Já existe uma prova com este título na mesma data para o departamento.",
    "Dados inválidos para a prova."
  );

  public ProvaServiceImpl(ProvaDao dao, CrudServiceSupport support) {
    this.dao = dao;
    this.support = support;
  }

  @Override
  public Prova create(Prova prova) {
    try {
      return dao.create(prova);
    } catch (DataAccessException ex) {
      throw support.translateUpsertException(ex, PROVA_ERRORS);
    }
  }

  @Override
  public List<Prova> listAll() {
    return dao.findAll();
  }

  @Override
  public Optional<Prova> findById(Long id) {
    return dao.findById(id);
  }

  @Override
  public Optional<Prova> update(Long id, Prova prova) {
    Prova toUpdate = support
      .updater(Prova::new)
      .withId(id, Prova::setId, "id")
      .copy(prova, (target, source) -> {
        target.setDepartamentoId(source.getDepartamentoId());
        target.setTitulo(source.getTitulo());
        target.setData(source.getData());
        target.setDescricao(source.getDescricao());
      })
      .build();

    try {
      return dao.update(toUpdate);
    } catch (DataAccessException ex) {
      throw support.translateUpsertException(ex, PROVA_ERRORS);
    }
  }

  @Override
  public boolean delete(Long id) {
    try {
      return dao.delete(id);
    } catch (DataIntegrityViolationException ex) {
      throw support.translateDeleteException(
        ex,
        "Não é possível remover a prova porque existem registros relacionados."
      );
    }
  }
}
