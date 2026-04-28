package br.com.fastcep.cep.gateway;

import br.com.fastcep.cep.exceptions.CepInvalidoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTemplateService implements CepApiClient {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateService.class);
    private final String wiremockUrl;
    private final RestTemplate restTemplate;


    public RestTemplateService(@Value("${spring.application.wiremock.url}") String wiremockUrl,
                               RestTemplate restTemplate) {
        this.wiremockUrl = wiremockUrl;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> consultaCepApiExterna(String valorCep) throws CepInvalidoException {
        try {
            return restTemplate.getForEntity(wiremockUrl.concat("/consulta_cep/").concat(valorCep), String.class);
        } catch (HttpClientErrorException e) {
            log.error("Erro ao consultar API externa para o CEP {}: {}", valorCep, e.getMessage());
            throw new CepInvalidoException("CEP não encontrado.");
        }
    }

}

