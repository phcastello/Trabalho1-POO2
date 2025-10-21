package poo.service;

import java.util.Optional;

import poo.model.Usuario;

public interface AuthService {
  Optional<Usuario> login(String username, String senhaPlana);
  Optional<Usuario> me(Long id);
}
