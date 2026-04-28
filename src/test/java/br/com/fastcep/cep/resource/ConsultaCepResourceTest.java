package br.com.fastcep.cep.resource;

import br.com.fastcep.cep.entity.Cep;
import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import br.com.fastcep.cep.service.ConsultaCepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultaCepResource.class)
class ConsultaCepResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultaCepService consultaCepService;

    private CepDTO validCepDTO;

    @BeforeEach
    void setUp() {
        validCepDTO = new CepDTO(
                "01310100",
                "Avenida Paulista",
                "Bela Vista",
                "São Paulo",
                "SP"
        );
    }

    @Test
    void deveRetornar200OKComCepValido() throws Exception {
        String cep = "01310100";
        when(consultaCepService.consultaCep(cep)).thenReturn(validCepDTO);

        mockMvc.perform(get("/consulta_cep/{valorCep}", cep)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep", is("01310100")))
                .andExpect(jsonPath("$.logradouro", is("Avenida Paulista")))
                .andExpect(jsonPath("$.bairro", is("Bela Vista")))
                .andExpect(jsonPath("$.cidade", is("São Paulo")))
                .andExpect(jsonPath("$.estado", is("SP")));

        verify(consultaCepService, times(1)).consultaCep(cep);
    }

    @Test
    void deveRetornarExcecaoParaCepInvalido() throws Exception {
        String cep = "0";
        when(consultaCepService.consultaCep(cep))
                .thenThrow(new CepInvalidoException("CEP inválido"));

        mockMvc.perform(get("/consulta_cep/{valorCep}", cep)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(consultaCepService, times(1)).consultaCep(cep);
    }

    @Test
    void deveRetornaExcecaoParaCepNaoEncontrado() throws Exception {
        String cep = "99999999";
        when(consultaCepService.consultaCep(cep))
                .thenThrow(new CepInvalidoException("CEP não encontrado"));

        mockMvc.perform(get("/consulta_cep/{valorCep}", cep)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testConsultaHistoricoCepWithData() throws Exception {
        Cep cep1 = Cep.builder()
                .id(1L)
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .estado("SP")
                .dataHora("2024-04-27T10:00:00")
                .build();

        Cep cep2 = Cep.builder()
                .id(2L)
                .cep("20040020")
                .logradouro("Avenida Rio Branco")
                .bairro("Centro")
                .cidade("Rio de Janeiro")
                .estado("RJ")
                .dataHora("2024-04-27T11:00:00")
                .build();

        List<Cep> historicoCeps = Arrays.asList(cep1, cep2);
        when(consultaCepService.consultaHistoricoCep()).thenReturn(historicoCeps);

        mockMvc.perform(get("/consulta_cep")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].cep", is("01310100")))
                .andExpect(jsonPath("$[0].logradouro", is("Avenida Paulista")))
                .andExpect(jsonPath("$[1].cep", is("20040020")))
                .andExpect(jsonPath("$[1].logradouro", is("Avenida Rio Branco")));

        verify(consultaCepService, times(1)).consultaHistoricoCep();
    }

}


