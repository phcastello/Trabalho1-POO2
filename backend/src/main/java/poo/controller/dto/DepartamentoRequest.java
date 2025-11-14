package poo.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartamentoRequest(
  @NotBlank String nome,
  @Size(min = 1, max = 10) String sigla
) {}
