package poo.controller.mapper;

import poo.controller.dto.AlunoRequest;
import poo.model.Aluno;

public final class AlunoMapper {

  private AlunoMapper() {}

  public static Aluno toEntity(AlunoRequest dto) {
    Aluno aluno = new Aluno();
    aluno.setRa(MapperUtils.trimToNull(dto.ra()));
    aluno.setNome(MapperUtils.trimToNull(dto.nome()));
    aluno.setEmail(MapperUtils.normalizeEmail(dto.email()));
    aluno.setDepartamentoId(dto.departamentoId());
    aluno.setDataNascimento(dto.dataNascimento());
    return aluno;
  }
}
