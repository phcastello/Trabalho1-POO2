package poo.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import poo.dao.AlunoDao;
import poo.model.Aluno;
import poo.service.AlunoService;
import poo.service.support.CrudServiceSupport;
import poo.service.support.CrudServiceSupport.UpsertErrorDescriptor;

@Service
public class AlunoServiceImpl implements AlunoService {

  private final AlunoDao dao;
  private final CrudServiceSupport support;

  private static final @NonNull UpsertErrorDescriptor ALUNO_ERRORS = CrudServiceSupport.conflictBadRequest(
    "RA ou e-mail já cadastrado.",
    "Dados inválidos para o aluno."
  );

  public AlunoServiceImpl(AlunoDao dao, CrudServiceSupport support) {
    this.dao = dao;
    this.support = support;
  }

  @Override
  public Aluno create(Aluno aluno) {
    try {
      return dao.create(aluno);
    } catch (DataAccessException ex) {
      throw support.translateUpsertException(ex, ALUNO_ERRORS);
    }
  }

  @Override
  public List<Aluno> listAll() {
    return dao.findAll();
  }

  @Override
  public Optional<Aluno> findById(Long id) {
    return dao.findById(id);
  }

  @Override
  public Optional<Aluno> update(Long id, Aluno aluno) {
    Aluno toUpdate = support
      .updater(Aluno::new)
      .withId(id, Aluno::setId, "id")
      .copy(aluno, (target, source) -> {
        target.setRa(source.getRa());
        target.setNome(source.getNome());
        target.setEmail(source.getEmail());
        target.setDepartamentoId(source.getDepartamentoId());
        target.setDataNascimento(source.getDataNascimento());
      })
      .build();

    try {
      return dao.update(toUpdate);
    } catch (DataAccessException ex) {
      throw support.translateUpsertException(ex, ALUNO_ERRORS);
    }
  }

  @Override
  public boolean delete(Long id) {
    try {
      return dao.delete(id);
    } catch (DataIntegrityViolationException ex) {
      throw support.translateDeleteException(
        ex,
        "Não é possível remover o aluno porque existem registros relacionados."
      );
    }
  }
}
