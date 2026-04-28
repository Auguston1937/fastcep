package br.com.fastcep.cep.resource;

import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.dto.CepHistoricoDTO;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import br.com.fastcep.cep.service.ConsultaCepUseCase;
import org.slf4j.Logger;
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

    private static final Logger log = LoggerFactory.getLogger(ConsultaCepResource.class);
    private final ConsultaCepUseCase consultaCepUseCase;

    public ConsultaCepResource(ConsultaCepUseCase consultaCepUseCase) {
        this.consultaCepUseCase = consultaCepUseCase;
    }

    @GetMapping("/{valorCep}")
    public ResponseEntity<CepDTO> consultaCep(@PathVariable String valorCep) {
        log.info("Iniciando consulta de CEP: {}", valorCep);
        try {
            CepDTO result = consultaCepUseCase.consultarCep(valorCep);
            log.info("Consulta de CEP realizada com sucesso: {}", valorCep);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (CepInvalidoException e) {
            log.error("Erro ao consultar CEP {}: {}", valorCep, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao consultar CEP {}: {}", valorCep, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        }
    }

    @GetMapping
    public ResponseEntity<List<CepHistoricoDTO>> consultaHistoricoCep() {
        log.info("Iniciando consulta do histórico de CEPs");
        try {
            List<CepHistoricoDTO> result = consultaCepUseCase.consultarHistoricoCep();
            log.info("Consulta do histórico de CEPs realizada com sucesso, retornando {} registros", result.size());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Erro ao consultar histórico de CEPs: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
        }
    }

}
