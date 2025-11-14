package poo.controller.mapper;

import poo.controller.dto.NotaCreateRequest;
import poo.controller.dto.NotaUpdateRequest;
import poo.model.Nota;

public final class NotaMapper {

  private NotaMapper() {}

  public static Nota toEntity(NotaCreateRequest dto) {
    Nota nota = new Nota();
    nota.setAlunoId(dto.alunoId());
    nota.setProvaId(dto.provaId());
    nota.setValor(dto.valor());
    nota.setObservacao(MapperUtils.trimToNull(dto.observacao()));
    return nota;
  }

  public static Nota toEntity(Long alunoId, Long provaId, NotaUpdateRequest dto) {
    Nota nota = new Nota();
    nota.setAlunoId(alunoId);
    nota.setProvaId(provaId);
    nota.setValor(dto.valor());
    nota.setObservacao(MapperUtils.trimToNull(dto.observacao()));
    return nota;
  }
}
