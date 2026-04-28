package br.com.fastcep.cep.service;

import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.dto.CepHistoricoDTO;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import br.com.fastcep.cep.gateway.RestTemplateService;
import br.com.fastcep.cep.mapper.CepMapper;
import br.com.fastcep.cep.repository.CepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaCepUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConsultaCepUseCase.class);
    private final CepRepository cepRepository;
    private final RestTemplateService restTemplateService;
    private final CepMapper cepMapper;

    public ConsultaCepUseCase(CepRepository cepRepository, RestTemplateService restTemplateService, CepMapper cepMapper) {
        this.cepRepository = cepRepository;
        this.restTemplateService = restTemplateService;
        this.cepMapper = cepMapper;
    }

    public CepDTO consultarCep(String valorCep) throws CepInvalidoException {
        if (!hasCepValidFormat(valorCep)) {
            log.error("CEP informado está mal formatado: {}", valorCep);
            throw new CepInvalidoException("CEP informado está mal formatado");
        }
        ResponseEntity<String> apiResponse = restTemplateService.consultaCepApiExterna(valorCep);
        CepDTO cepResponse = cepMapper.stringToCepDto(apiResponse.getBody());
        if (cepResponse == null) {
            log.error("CEP informado não encontrado ou inválido: {}", valorCep);
            throw new CepInvalidoException("CEP informado não encontrado ou inválido");
        }
        salvarHistorico(cepResponse);
        return cepResponse;
    }

    public void salvarHistorico(CepDTO cepDTO) {
        cepRepository.save(cepMapper.dtoToEntity(cepDTO));
    }

    public List<CepHistoricoDTO> consultarHistoricoCep() {
        return cepRepository.findAll().stream()
                .map(cepMapper::entityToHistoricoDTO)
                .collect(Collectors.toList());
    }

    private Boolean hasCepValidFormat(String valorCep) {
        return valorCep.matches("^\\d{8}$");
    }
}
