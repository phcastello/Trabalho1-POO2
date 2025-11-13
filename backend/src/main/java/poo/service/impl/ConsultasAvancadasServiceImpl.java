package poo.service.impl;

import org.springframework.stereotype.Service;
import poo.dao.ConsultasAvancadasDao;
import poo.model.consultas.ConsultasAvancadasResumo;
import poo.service.ConsultasAvancadasService;

@Service
public class ConsultasAvancadasServiceImpl implements ConsultasAvancadasService {

  private final ConsultasAvancadasDao dao;

  public ConsultasAvancadasServiceImpl(ConsultasAvancadasDao dao) {
    this.dao = dao;
  }

  @Override
  public ConsultasAvancadasResumo obterResumo() {
    ConsultasAvancadasResumo resumo = new ConsultasAvancadasResumo();
    resumo.setRankingDepartamentos(dao.listarRankingDepartamentos());
    resumo.setAlunosModalidades(dao.listarAlunosModalidades());
    resumo.setCoberturaNotas(dao.listarCoberturaNotas());
    return resumo;
  }
}
