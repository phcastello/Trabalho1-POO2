package poo.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import poo.dao.NotaDao;
import poo.model.Nota;
import poo.service.NotaService;
import poo.service.support.CrudServiceSupport;
import poo.service.support.CrudServiceSupport.UpsertErrorDescriptor;

@Service
public class NotaServiceImpl implements NotaService {

  private final NotaDao dao;
  private final CrudServiceSupport support;

  private static final @NonNull UpsertErrorDescriptor NOTA_ERRORS = CrudServiceSupport.conflictBadRequest(
    "Nota para esta combinação de aluno e prova já cadastrada.",
    "Dados inválidos para a nota. Verifique aluno, prova e valor informado."
  );

  public NotaServiceImpl(NotaDao dao, CrudServiceSupport support) {
    this.dao = dao;
    this.support = support;
  }

  @Override
  public Nota create(Nota nota) {
    try {
      return dao.create(nota);
    } catch (DataAccessException ex) {
      throw support.translateUpsertException(ex, NOTA_ERRORS);
    }
  }

  @Override
  public List<Nota> listAll(Long alunoId, Long provaId) {
    return dao.findAll(alunoId, provaId);
  }

  @Override
  public Optional<Nota> findById(Long alunoId, Long provaId) {
    return dao.findById(alunoId, provaId);
  }

  @Override
  public Optional<Nota> update(Long alunoId, Long provaId, Nota nota) {
    Nota toUpdate = support
      .updater(Nota::new)
      .withId(alunoId, Nota::setAlunoId, "alunoId")
      .withId(provaId, Nota::setProvaId, "provaId")
      .copy(nota, (target, source) -> {
        target.setValor(source.getValor());
        target.setObservacao(source.getObservacao());
      })
      .build();

    try {
      return dao.update(toUpdate);
    } catch (DataAccessException ex) {
      throw support.translateUpsertException(ex, NOTA_ERRORS);
    }
  }

  @Override
  public boolean delete(Long alunoId, Long provaId) {
    return dao.delete(alunoId, provaId);
  }
}
