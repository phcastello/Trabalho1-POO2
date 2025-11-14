package poo.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

public record AlunoRequest(
  @NotBlank String ra,
  @NotBlank String nome,
  @Email String email,
  @NotNull Long departamentoId,
  @PastOrPresent LocalDate dataNascimento
) {}
