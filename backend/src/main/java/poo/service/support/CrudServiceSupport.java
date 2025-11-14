package poo.service.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CrudServiceSupport {

  public <T> UpdateBuilder<T> updater(@NonNull Supplier<T> factory) {
    return new UpdateBuilder<>(factory);
  }

  public @NonNull RuntimeException translateUpsertException(@NonNull DataAccessException ex, @NonNull UpsertErrorDescriptor descriptor) {
    if (ex instanceof DuplicateKeyException) {
      return new ResponseStatusException(
        descriptor.duplicateStatus(),
        descriptor.duplicateMessage(),
        ex
      );
    }
    if (ex instanceof DataIntegrityViolationException) {
      return new ResponseStatusException(
        descriptor.integrityStatus(),
        descriptor.integrityMessage(),
        ex
      );
    }
    return ex;
  }

  public @NonNull ResponseStatusException translateDeleteException(@NonNull DataIntegrityViolationException ex, @NonNull String message) {
    return new ResponseStatusException(HttpStatus.CONFLICT, message, ex);
  }

  public static @NonNull UpsertErrorDescriptor conflictBadRequest(@NonNull String duplicateMessage, @NonNull String integrityMessage) {
    return new UpsertErrorDescriptor(HttpStatus.CONFLICT, duplicateMessage, HttpStatus.BAD_REQUEST, integrityMessage);
  }

  public record UpsertErrorDescriptor(
    @NonNull HttpStatus duplicateStatus,
    @NonNull String duplicateMessage,
    @NonNull HttpStatus integrityStatus,
    @NonNull String integrityMessage
  ) {
    public UpsertErrorDescriptor {
      Objects.requireNonNull(duplicateStatus, "duplicateStatus must not be null");
      Objects.requireNonNull(duplicateMessage, "duplicateMessage must not be null");
      Objects.requireNonNull(integrityStatus, "integrityStatus must not be null");
      Objects.requireNonNull(integrityMessage, "integrityMessage must not be null");
    }
  }

  public static final class UpdateBuilder<T> {
    private final Supplier<T> factory;
    private final List<Consumer<T>> steps = new ArrayList<>();

    private UpdateBuilder(Supplier<T> factory) {
      this.factory = Objects.requireNonNull(factory, "factory must not be null");
    }

    public <ID> UpdateBuilder<T> withId(ID id, @NonNull BiConsumer<T, ID> setter, String fieldName) {
      Objects.requireNonNull(setter, "setter must not be null");
      String label = (fieldName == null || fieldName.isBlank()) ? "id" : fieldName;
      Objects.requireNonNull(id, label + " não pode ser nulo");
      steps.add(target -> setter.accept(target, id));
      return this;
    }

    public <S> UpdateBuilder<T> copy(S source, @NonNull BiConsumer<T, S> copier) {
      Objects.requireNonNull(copier, "copier must not be null");
      steps.add(target -> copier.accept(target, source));
      return this;
    }

    public @NonNull T build() {
      T target = Objects.requireNonNull(factory.get(), "factory must not create null instances");
      steps.forEach(step -> step.accept(target));
      return target;
    }
  }
}
