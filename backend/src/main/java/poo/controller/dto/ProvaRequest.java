package poo.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProvaRequest(
  @NotNull Long departamentoId,
  @NotBlank String titulo,
  @NotNull LocalDate data,
  String descricao
) {}
