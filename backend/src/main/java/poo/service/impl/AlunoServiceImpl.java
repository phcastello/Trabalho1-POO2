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
import poo.dao.AlunoDao;
import poo.model.Aluno;
import poo.service.AlunoService;

@Service
public class AlunoServiceImpl implements AlunoService {

  private final AlunoDao dao;

  public AlunoServiceImpl(AlunoDao dao) {
    this.dao = dao;
  }

  @Override
  public Aluno create(Aluno aluno) {
    try {
      return dao.create(aluno);
    } catch (DataAccessException ex) {
      throw translateUpsertException(ex);
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
    Objects.requireNonNull(id, "id não pode ser nulo");
    Aluno toUpdate = new Aluno();
    toUpdate.setId(id);
    toUpdate.setRa(aluno.getRa());
    toUpdate.setNome(aluno.getNome());
    toUpdate.setEmail(aluno.getEmail());
    toUpdate.setDepartamentoId(aluno.getDepartamentoId());
    toUpdate.setDataNascimento(aluno.getDataNascimento());

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
        "Não é possível remover o aluno porque existem registros relacionados.",
        ex
      );
    }
  }

  private RuntimeException translateUpsertException(DataAccessException ex) {
    if (ex instanceof DuplicateKeyException) {
      return new ResponseStatusException(HttpStatus.CONFLICT, "RA ou e-mail já cadastrado.", ex);
    }
    if (ex instanceof DataIntegrityViolationException) {
      return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos para o aluno.", ex);
    }
    return ex;
  }
}

