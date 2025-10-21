package poo.dao.impl;

import poo.dao.UsuarioDao;
import poo.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UsuarioDaoJdbc implements UsuarioDao {

  private final JdbcTemplate jdbc;
  public UsuarioDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

  private static final RowMapper<Usuario> RM = (rs, i) -> {
    Usuario u = new Usuario();
    u.setId(rs.getLong("id"));
    u.setUsername(rs.getString("username"));
    u.setNome(rs.getString("nome"));
    return u;
  };

  @Override
  public Optional<Usuario> autenticar(String username, String senhaPlana) {
    // Usa pgcrypto: password_hash = crypt('senha', password_hash)
    List<Usuario> list = jdbc.query("""
      SELECT id, username, nome
      FROM usuario
      WHERE username = ?
        AND password_hash = crypt(?, password_hash)
    """, RM, username, senhaPlana);
    return list.stream().findFirst();
  }

  @Override
  public Optional<Usuario> findById(Long id) {
    List<Usuario> list = jdbc.query("SELECT id, username, nome FROM usuario WHERE id = ?", RM, id);
    return list.stream().findFirst();
  }
}
