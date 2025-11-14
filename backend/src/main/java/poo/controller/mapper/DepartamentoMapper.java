package poo.controller.mapper;

import poo.controller.dto.DepartamentoRequest;
import poo.model.Departamento;

public final class DepartamentoMapper {

  private DepartamentoMapper() {}

  public static Departamento toEntity(DepartamentoRequest dto) {
    Departamento departamento = new Departamento();
    departamento.setNome(MapperUtils.trimToNull(dto.nome()));
    departamento.setSigla(MapperUtils.uppercaseOrNull(dto.sigla()));
    return departamento;
  }
}
