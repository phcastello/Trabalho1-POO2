package poo.controller.mapper;

import java.util.Locale;

final class MapperUtils {
  private MapperUtils() {}

  static String trimToNull(String value) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
  }

  static String normalizeEmail(String email) {
    return trimToNull(email);
  }

  static String uppercaseOrNull(String value) {
    String trimmed = trimToNull(value);
    return trimmed == null ? null : trimmed.toUpperCase(Locale.ROOT);
  }
}
