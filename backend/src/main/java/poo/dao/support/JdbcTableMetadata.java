package poo.dao.support;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

public final class JdbcTableMetadata<T> {

  private final @NonNull String tableName;
  private final @NonNull String columnsCsv;
  private final @NonNull RowMapper<T> rowMapper;
  private final String defaultOrderBy;

  private JdbcTableMetadata(
    @NonNull String tableName,
    @NonNull String columnsCsv,
    @NonNull RowMapper<T> rowMapper,
    String defaultOrderBy
  ) {
    this.tableName = tableName;
    this.columnsCsv = columnsCsv;
    this.rowMapper = rowMapper;
    this.defaultOrderBy = defaultOrderBy;
  }

  public static <T> Builder<T> builder(@NonNull String tableName) {
    return new Builder<>(tableName);
  }

  public @NonNull String tableName() {
    return tableName;
  }

  public @NonNull String columns() {
    return columnsCsv;
  }

  public @NonNull RowMapper<T> rowMapper() {
    return rowMapper;
  }

  public @NonNull String baseSelectSql() {
    return "SELECT " + columnsCsv + " FROM " + tableName;
  }

  public @NonNull String selectAllSql() {
    return defaultOrderBy == null ? baseSelectSql() : baseSelectSql() + " ORDER BY " + defaultOrderBy;
  }

  public String orderByClause() {
    return defaultOrderBy;
  }

  public @NonNull String selectByIdSql(@NonNull String whereClause) {
    return baseSelectSql() + " WHERE " + whereClause;
  }

  public @NonNull String insertReturningSql(@NonNull String insertColumns) {
    return "INSERT INTO " +
      tableName +
      " (" +
      insertColumns +
      ") VALUES (" +
      placeholders(insertColumns) +
      ") RETURNING " +
      columnsCsv;
  }

  public @NonNull String updateReturningSql(@NonNull String setClause, @NonNull String whereClause) {
    return "UPDATE " +
      tableName +
      " SET " +
      setClause +
      " WHERE " +
      whereClause +
      " RETURNING " +
      columnsCsv;
  }

  public @NonNull String deleteSql(@NonNull String whereClause) {
    return "DELETE FROM " + tableName + " WHERE " + whereClause;
  }

  public static void populateAuditColumns(
    ResultSet rs,
    Consumer<Instant> createdSetter,
    Consumer<Instant> updatedSetter
  ) throws SQLException {
    OffsetDateTime created = rs.getObject("created_at", OffsetDateTime.class);
    if (created != null && createdSetter != null) {
      createdSetter.accept(created.toInstant());
    }
    OffsetDateTime updated = rs.getObject("updated_at", OffsetDateTime.class);
    if (updated != null && updatedSetter != null) {
      updatedSetter.accept(updated.toInstant());
    }
  }

  private static @NonNull String placeholders(@NonNull String columnList) {
    int count = (int) Arrays
      .stream(columnList.replace("\n", " ").split(","))
      .map(String::trim)
      .filter(part -> !part.isEmpty())
      .count();
    if (count == 0) {
      throw new IllegalArgumentException("Column list must not be empty");
    }
    return Objects.requireNonNull(String.join(", ", Collections.nCopies(count, "?")));
  }

  public static final class Builder<T> {
    private final @NonNull String tableName;
    private final List<String> columns = new ArrayList<>();
    private RowMapper<T> rowMapper;
    private String defaultOrderBy;
    private boolean auditable;

    private Builder(String tableName) {
      this.tableName = Objects.requireNonNull(tableName, "tableName must not be null");
    }

    public Builder<T> columns(String... names) {
      Arrays
        .stream(names)
        .map(String::trim)
        .filter(name -> !name.isEmpty())
        .forEach(columns::add);
      return this;
    }

    public Builder<T> auditable() {
      this.auditable = true;
      return this;
    }

    public Builder<T> rowMapper(RowMapper<T> mapper) {
      this.rowMapper = Objects.requireNonNull(mapper, "rowMapper must not be null");
      return this;
    }

    public Builder<T> defaultOrderBy(String clause) {
      this.defaultOrderBy = clause;
      return this;
    }

    public @NonNull JdbcTableMetadata<T> build() {
      if (columns.isEmpty()) {
        throw new IllegalStateException("Columns must be provided before building metadata");
      }
      if (auditable) {
        columns.add("created_at");
        columns.add("updated_at");
      }
      if (rowMapper == null) {
        throw new IllegalStateException("RowMapper must be provided");
      }
      String columnsCsv = Objects.requireNonNull(String.join(", ", columns));
      return new JdbcTableMetadata<>(tableName, columnsCsv, Objects.requireNonNull(rowMapper), defaultOrderBy);
    }
  }
}
