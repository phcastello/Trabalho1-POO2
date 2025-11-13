package poo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import poo.model.consultas.ConsultasAvancadasResumo;
import poo.service.ConsultasAvancadasService;

@RestController
@RequestMapping("/api/consultas-avancadas")
public class ConsultasAvancadasController {

  private final ConsultasAvancadasService service;

  public ConsultasAvancadasController(ConsultasAvancadasService service) {
    this.service = service;
  }

  @GetMapping
  public ConsultasAvancadasResumo listar() {
    return service.obterResumo();
  }
}
