package poo.controller;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public abstract class BaseCrudController {

  protected <T> T requireFound(Optional<T> value, String notFoundMessage) {
    return value.orElseThrow(() -> notFound(notFoundMessage));
  }

  protected ResponseEntity<Void> deleteOrNotFound(boolean removed, String notFoundMessage) {
    if (!removed) {
      throw notFound(notFoundMessage);
    }
    return ResponseEntity.noContent().build();
  }

  protected @NonNull URI createdUri(String pathTemplate, Object... uriVariables) {
    String safePath = Objects.requireNonNull(pathTemplate, "pathTemplate must not be null");
    Object[] safeVariables = Objects.requireNonNull(uriVariables, "uriVariables must not be null");
    URI builtUri = ServletUriComponentsBuilder.fromCurrentRequest().path(safePath).buildAndExpand(safeVariables).toUri();
    return Objects.requireNonNull(builtUri, "created URI must not be null");
  }

  protected ResponseStatusException notFound(String message) {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
  }
}
