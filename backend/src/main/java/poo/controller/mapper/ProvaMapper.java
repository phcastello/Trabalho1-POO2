package poo.controller.mapper;

import poo.controller.dto.ProvaRequest;
import poo.model.Prova;

public final class ProvaMapper {

  private ProvaMapper() {}

  public static Prova toEntity(ProvaRequest dto) {
    Prova prova = new Prova();
    prova.setDepartamentoId(dto.departamentoId());
    prova.setTitulo(MapperUtils.trimToNull(dto.titulo()));
    prova.setData(dto.data());
    prova.setDescricao(MapperUtils.trimToNull(dto.descricao()));
    return prova;
  }
}
