package br.com.fastcep.cep.resource;

import br.com.fastcep.cep.entity.Cep;
import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import br.com.fastcep.cep.service.ConsultaCepService;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/consulta_cep")
public class ConsultaCepResource {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConsultaCepResource.class);
    private final ConsultaCepService consultaCepService;

    public ConsultaCepResource(ConsultaCepService consultaCepService) {
        this.consultaCepService = consultaCepService;
    }

    @GetMapping("/{valorCep}")
    public ResponseEntity<CepDTO> consultaCep(@PathVariable String valorCep) {
        try {
            return new ResponseEntity<>(consultaCepService.consultaCep(valorCep), HttpStatus.OK);
        } catch (CepInvalidoException e) {
            log.error("Erro ao consultar CEP: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Cep>> consultaHistoricoCep() {
        return new ResponseEntity<>(consultaCepService.consultaHistoricoCep(), HttpStatus.OK);
    }

}
