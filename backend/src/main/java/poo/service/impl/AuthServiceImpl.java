package poo.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import poo.dao.UsuarioDao;
import poo.model.Usuario;
import poo.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
  private final UsuarioDao dao;
  public AuthServiceImpl(UsuarioDao dao) { this.dao = dao; }

  @Override
  public Optional<Usuario> login(String username, String senhaPlana) {
    return dao.autenticar(username, senhaPlana);
  }

  @Override
  public Optional<Usuario> me(Long id) {
    return dao.findById(id);
  }
}
