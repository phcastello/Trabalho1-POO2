package poo.dao;

import poo.model.Usuario;
import java.util.Optional;

public interface UsuarioDao {
  Optional<Usuario> autenticar(String username, String senhaPlana); // retorna se login ok
  Optional<Usuario> findById(Long id);
}
