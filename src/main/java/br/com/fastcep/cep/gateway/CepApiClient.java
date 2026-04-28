package br.com.fastcep.cep.gateway;

import org.springframework.http.ResponseEntity;

import br.com.fastcep.cep.exceptions.CepInvalidoException;

public interface CepApiClient {

    ResponseEntity<String> consultaCepApiExterna(String valorCep) throws CepInvalidoException;

}

