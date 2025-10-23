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
import poo.dao.NotaDao;
import poo.model.Nota;
import poo.service.NotaService;

@Service
public class NotaServiceImpl implements NotaService {

  private final NotaDao dao;

  public NotaServiceImpl(NotaDao dao) {
    this.dao = dao;
  }

  @Override
  public Nota create(Nota nota) {
    try {
      return dao.create(nota);
    } catch (DataAccessException ex) {
      throw translateUpsertException(ex);
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
    Objects.requireNonNull(alunoId, "alunoId não pode ser nulo");
    Objects.requireNonNull(provaId, "provaId não pode ser nulo");

    Nota toUpdate = new Nota();
    toUpdate.setAlunoId(alunoId);
    toUpdate.setProvaId(provaId);
    toUpdate.setValor(nota.getValor());
    toUpdate.setObservacao(nota.getObservacao());

    try {
      return dao.update(toUpdate);
    } catch (DataAccessException ex) {
      throw translateUpsertException(ex);
    }
  }

  @Override
  public boolean delete(Long alunoId, Long provaId) {
    return dao.delete(alunoId, provaId);
  }

  private RuntimeException translateUpsertException(DataAccessException ex) {
    if (ex instanceof DuplicateKeyException) {
      return new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Nota para esta combinação de aluno e prova já cadastrada.",
        ex
      );
    }
    if (ex instanceof DataIntegrityViolationException) {
      return new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Dados inválidos para a nota. Verifique aluno, prova e valor informado.",
        ex
      );
    }
    return ex;
  }
}

