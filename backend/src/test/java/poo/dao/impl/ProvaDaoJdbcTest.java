package poo.dao.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;
import org.springframework.test.util.ReflectionTestUtils;
import poo.model.Prova;

@ExtendWith(MockitoExtension.class)
class ProvaDaoJdbcTest {

  private static final @NonNull String INSERT_SQL =
      Objects.requireNonNull(readStaticString("INSERT_SQL"));
  private static final @NonNull String SELECT_BY_ID_SQL =
      Objects.requireNonNull(readStaticString("SELECT_BY_ID_SQL"));
  private static final @NonNull String UPDATE_SQL =
      Objects.requireNonNull(readStaticString("UPDATE_SQL"));
  private static final @NonNull RowMapper<Prova> ROW_MAPPER = readRowMapper();

  @Mock
  private JdbcTemplate jdbcTemplate;

  @InjectMocks
  private ProvaDaoJdbc dao;

  @Test
  void shouldCreateProva_whenFieldsValid() {
    // Given
    LocalDate data = LocalDate.of(2023, 5, 10);
    Prova novaProva = buildProva(null, 42L, "Álgebra I", data, "Etapa final");
    Prova persisted = buildProva(7L, 42L, "Álgebra I", data, "Etapa final");
    when(jdbcTemplate.queryForObject(
        INSERT_SQL,
        ROW_MAPPER,
        42L,
        "Álgebra I",
        Date.valueOf(data),
        "Etapa final"
    )).thenReturn(persisted);

    // When
    Prova resultado = dao.create(novaProva);

    // Then
    assertSame(persisted, resultado, "create deve devolver exatamente o registro retornado pelo JdbcTemplate");
    verify(jdbcTemplate).queryForObject(
        INSERT_SQL,
        ROW_MAPPER,
        42L,
        "Álgebra I",
        Date.valueOf(data),
        "Etapa final"
    );
  }

  @Test
  void shouldReturnEmptyOptional_whenFindByIdDoesNotMatch() {
    // Given
    Long idInexistente = 999L;
    when(jdbcTemplate.query(SELECT_BY_ID_SQL, ROW_MAPPER, idInexistente)).thenReturn(List.of());

    // When
    Optional<Prova> resultado = dao.findById(idInexistente);

    // Then
    assertTrue(resultado.isEmpty(), "findById deve devolver Optional.empty quando a consulta não gera resultados");
    verify(jdbcTemplate).query(SELECT_BY_ID_SQL, ROW_MAPPER, idInexistente);
  }

  @Test
  void shouldUpdateProva_whenStateIsValidAndIdPresent() {
    // Given
    LocalDate novaData = LocalDate.of(2024, 3, 15);
    Prova provaAlterada = buildProva(11L, 5L, "Calculo II", novaData, "Conteúdo revisto");
    Prova atualizado = buildProva(11L, 5L, "Calculo II", novaData, "Conteúdo revisto");
    when(jdbcTemplate.query(
        UPDATE_SQL,
        ROW_MAPPER,
        5L,
        "Calculo II",
        Date.valueOf(novaData),
        "Conteúdo revisto",
        11L
    )).thenReturn(List.of(atualizado));

    // When
    Optional<Prova> resultado = dao.update(provaAlterada);

    // Then
    assertTrue(resultado.isPresent(), "update deve devolver Optional contendo a linha alterada");
    assertSame(atualizado, resultado.orElseThrow(), "update precisa refletir exatamente o objeto retornado pelo JdbcTemplate");
    verify(jdbcTemplate).query(
        UPDATE_SQL,
        ROW_MAPPER,
        5L,
        "Calculo II",
        Date.valueOf(novaData),
        "Conteúdo revisto",
        11L
    );
  }

  @Test
  void shouldThrowException_whenUpdateCalledWithoutId() {
    // Given
    Prova semId = buildProva(null, 9L, "Sem ID", LocalDate.of(2024, 1, 1), "Teste");

    // When
    NullPointerException exception = assertThrows(
        NullPointerException.class,
        () -> dao.update(semId),
        "update precisa rejeitar Prova sem id"
    );

    // Then
    assertEquals("Prova id must not be null", exception.getMessage(), "mensagem deve explicar qual campo está ausente");
    verifyNoInteractions(jdbcTemplate);
  }

  @Test
  void shouldReturnFalse_whenDeleteDoesNotAffectRows() {
    // Given
    long id = 77L;
    when(jdbcTemplate.update("DELETE FROM prova WHERE id = ?", id)).thenReturn(0);

    // When
    boolean resultado = dao.delete(id);

    // Then
    assertFalse(resultado, "delete deve ser falso quando nenhum registro foi apagado");
    verify(jdbcTemplate).update("DELETE FROM prova WHERE id = ?", id);
  }

  private static Prova buildProva(Long id, Long departamentoId, String titulo, LocalDate data, String descricao) {
    Prova prova = new Prova();
    prova.setId(id);
    prova.setDepartamentoId(departamentoId);
    prova.setTitulo(titulo);
    prova.setData(data);
    prova.setDescricao(descricao);
    prova.setCreatedAt(Instant.parse("2024-01-01T00:00:00Z"));
    prova.setUpdatedAt(Instant.parse("2024-01-02T00:00:00Z"));
    return prova;
  }

  private static @NonNull String readStaticString(@NonNull String fieldName) {
    Object value = Objects.requireNonNull(
        ReflectionTestUtils.getField(ProvaDaoJdbc.class, fieldName),
        "Não foi possível ler o campo " + fieldName);
    if (value instanceof String stringValue) {
      return stringValue;
    }
    throw new IllegalStateException("O campo " + fieldName + " não é uma String");
  }

  @SuppressWarnings("unchecked")
  private static @NonNull RowMapper<Prova> readRowMapper() {
    Object value = ReflectionTestUtils.getField(ProvaDaoJdbc.class, "ROW_MAPPER");
    return (RowMapper<Prova>) Objects.requireNonNull(value, "Não foi possível ler ROW_MAPPER");
  }
}
