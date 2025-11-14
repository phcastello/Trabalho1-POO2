package poo.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;
import poo.service.support.CrudServiceSupport.UpsertErrorDescriptor;

class CrudServiceSupportTest {

  private final CrudServiceSupport support = new CrudServiceSupport();
  private static final @NonNull UpsertErrorDescriptor DESCRIPTOR = CrudServiceSupport.conflictBadRequest(
    "duplicado",
    "invalido"
  );

  @Test
  void updaterBuildsEntityWithIdAndCopy() {
    DummyEntity entity = support
      .updater(DummyEntity::new)
      .withId(10L, DummyEntity::setId, "id")
      .copy("nome", DummyEntity::setNome)
      .build();

    assertEquals(10L, entity.getId());
    assertEquals("nome", entity.getNome());
  }

  @Test
  void updaterRejectsNullId() {
    CrudServiceSupport.UpdateBuilder<DummyEntity> builder = support.updater(DummyEntity::new);
    NullPointerException ex = assertThrows(
      NullPointerException.class,
      () -> builder.withId(null, DummyEntity::setId, "id")
    );
    assertEquals("id não pode ser nulo", ex.getMessage());
  }

  @Test
  void translateUpsertHandlesDuplicate() {
    DuplicateKeyException duplicate = new DuplicateKeyException("dup");
    ResponseStatusException ex = (ResponseStatusException) support.translateUpsertException(duplicate, DESCRIPTOR);
    assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    assertEquals("duplicado", ex.getReason());
  }

  @Test
  void translateUpsertHandlesIntegrity() {
    DataIntegrityViolationException integrity = new DataIntegrityViolationException("bad");
    ResponseStatusException ex = (ResponseStatusException) support.translateUpsertException(integrity, DESCRIPTOR);
    assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    assertEquals("invalido", ex.getReason());
  }

  private static class DummyEntity {
    private Long id;
    private String nome;

    Long getId() {
      return id;
    }

    void setId(Long id) {
      this.id = id;
    }

    String getNome() {
      return nome;
    }

    void setNome(String nome) {
      this.nome = nome;
    }
  }
}
