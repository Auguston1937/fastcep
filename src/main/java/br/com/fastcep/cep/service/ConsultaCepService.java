package br.com.fastcep.cep.service;

import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.entity.Cep;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import br.com.fastcep.cep.repository.CepRepository;
import br.com.fastcep.cep.utils.ConsultaCepFacade;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsultaCepService {

    private final ConsultaCepFacade consultaCepFacade;
    private final CepRepository cepRepository;

    public ConsultaCepService(ConsultaCepFacade consultaCepFacade, CepRepository cepRepository) {
        this.consultaCepFacade = consultaCepFacade;
        this.cepRepository = cepRepository;
    }

    public CepDTO consultaCep(String valorCep) throws CepInvalidoException {
        return consultaCepFacade.realizaConsultaCepESalvaHistorico(valorCep);
    }

    public List<Cep> consultaHistoricoCep() {
        return new ArrayList<>(cepRepository.findAll());
    }
}
