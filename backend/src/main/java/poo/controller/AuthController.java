package poo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import poo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService auth;
  public AuthController(AuthService auth) { this.auth = auth; }

  public static class LoginDTO {
    @NotBlank public String username;
    @NotBlank public String password;
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDTO dto, HttpSession session) {
    return auth.login(dto.username, dto.password)
      .<ResponseEntity<?>>map(u -> {
        session.setAttribute("userId", u.getId());
        session.setAttribute("userName", u.getNome());
        return ResponseEntity.ok(Map.of("id", u.getId(), "username", u.getUsername(), "nome", u.getNome()));
      })
      .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "invalid_credentials")));
  }

  @GetMapping("/me")
  public ResponseEntity<?> me(HttpSession session) {
    Object id = session.getAttribute("userId");
    Object name = session.getAttribute("userName");
    if (id == null) return ResponseEntity.status(401).body(Map.of("error", "unauthenticated"));
    return ResponseEntity.ok(Map.of("id", id, "nome", name));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpSession session) {
    session.invalidate();
    return ResponseEntity.noContent().build();
  }
}
