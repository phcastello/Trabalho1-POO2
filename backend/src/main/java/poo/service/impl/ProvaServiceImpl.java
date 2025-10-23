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
import poo.dao.ProvaDao;
import poo.model.Prova;
import poo.service.ProvaService;

@Service
public class ProvaServiceImpl implements ProvaService {

  private final ProvaDao dao;

  public ProvaServiceImpl(ProvaDao dao) {
    this.dao = dao;
  }

  @Override
  public Prova create(Prova prova) {
    try {
      return dao.create(prova);
    } catch (DataAccessException ex) {
      throw translateUpsertException(ex);
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
    Objects.requireNonNull(id, "id não pode ser nulo");
    Prova toUpdate = new Prova();
    toUpdate.setId(id);
    toUpdate.setDepartamentoId(prova.getDepartamentoId());
    toUpdate.setTitulo(prova.getTitulo());
    toUpdate.setData(prova.getData());
    toUpdate.setDescricao(prova.getDescricao());

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
        "Não é possível remover a prova porque existem registros relacionados.",
        ex
      );
    }
  }

  private RuntimeException translateUpsertException(DataAccessException ex) {
    if (ex instanceof DuplicateKeyException) {
      return new ResponseStatusException(
        HttpStatus.CONFLICT,
        "Já existe uma prova com este título na mesma data para o departamento.",
        ex
      );
    }
    if (ex instanceof DataIntegrityViolationException) {
      return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos para a prova.", ex);
    }
    return ex;
  }
}

