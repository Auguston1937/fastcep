package br.com.fastcep.cep.utils;

import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import br.com.fastcep.cep.mapper.CepMapper;
import br.com.fastcep.cep.repository.CepRepository;
import br.com.fastcep.service.utils.RestTemplateService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ConsultaCepFacade {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ConsultaCepFacade.class);
    private final CepRepository cepRepository;
    private final RestTemplateService restTemplateService;
    private final CepMapper cepMapper;
    private final ValidaCepService validaCepService;

    public ConsultaCepFacade(CepRepository cepRepository, RestTemplateService restTemplateService, CepMapper cepMapper, ValidaCepService validaCepService) {
        this.cepRepository = cepRepository;
        this.restTemplateService = restTemplateService;
        this.cepMapper = cepMapper;
        this.validaCepService = validaCepService;
    }

    public CepDTO realizaConsultaCepESalvaHistorico(String valorCep) throws CepInvalidoException {
        if (!validaCepService.hasCepValidFormat(valorCep)) {
            log.error("CEP informado está mal formatado: {}", valorCep);
            throw new CepInvalidoException("CEP informado está mal formatado");
        }
        ResponseEntity<String> apiResponse = restTemplateService.consultaCepApiExterna(valorCep);
        var cepResponse = cepMapper.stringToCepDto(apiResponse.getBody());
        if(cepResponse == null){
            log.error("CEP informado está mal formatado: {}", valorCep);
            throw new CepInvalidoException("CEP informado está mal formatado");
        }
        salvarConsultaHistorico(cepResponse);
        return cepResponse;
    }

    public void salvarConsultaHistorico(CepDTO cepDTO){
        cepRepository.save(cepMapper.dtoToEntity(cepDTO));
    }

}
