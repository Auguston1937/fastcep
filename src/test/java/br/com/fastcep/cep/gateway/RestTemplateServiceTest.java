package br.com.fastcep.cep.gateway;

import br.com.fastcep.cep.exceptions.CepInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RestTemplateServiceTest {

    @Mock
    private RestTemplate restTemplate;
    private final String wiremockUrl = "http://localhost:8080";
    private RestTemplateService restTemplateService;

    @BeforeEach
    void setUp() {
        restTemplateService = new RestTemplateService(wiremockUrl, restTemplate);
    }

    @Test
    void deveRetornarCorretamenteJsonParaCepValido() throws CepInvalidoException {
        String cep = "01310100";
        String json = "{\"cep\":\"01310100\",\"logradouro\":\"Avenida Paulista\",\"bairro\":\"Bela Vista\",\"cidade\":\"São Paulo\",\"estado\":\"SP\"}";

        Mockito.when(restTemplate.getForEntity(wiremockUrl.concat("/consulta_cep/").concat(cep), String.class)).thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        ResponseEntity<String> response = restTemplateService.consultaCepApiExterna(cep);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(json, response.getBody());
    }

}

