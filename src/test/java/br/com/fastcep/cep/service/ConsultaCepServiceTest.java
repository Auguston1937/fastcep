package br.com.fastcep.cep.service;

import br.com.fastcep.cep.entity.Cep;
import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import br.com.fastcep.cep.repository.CepRepository;
import br.com.fastcep.cep.utils.ConsultaCepFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaCepServiceTest {

    @Mock
    private ConsultaCepFacade consultaCepFacade;

    @Mock
    private CepRepository cepRepository;

    private ConsultaCepService consultaCepService;

    @BeforeEach
    void setUp() {
        consultaCepService = new ConsultaCepService(consultaCepFacade, cepRepository);
    }

    @Test
    void deveRealizarConsultaCepCorretamente() throws CepInvalidoException {
        String cep = "01310100";
        CepDTO expectedCepDTO = new CepDTO(
                "01310100",
                "Avenida Paulista",
                "Bela Vista",
                "São Paulo",
                "SP"
        );

        when(consultaCepFacade.realizaConsultaCepESalvaHistorico(cep))
                .thenReturn(expectedCepDTO);

        CepDTO result = consultaCepService.consultaCep(cep);

        assertNotNull(result);
        verify(consultaCepFacade, times(1)).realizaConsultaCepESalvaHistorico(cep);
    }

    @Test
    void deveLancarExcecaoQuandoConsultarCepParaValorInvalido() throws CepInvalidoException {
        String cepInvalido = "0";

        when(consultaCepFacade.realizaConsultaCepESalvaHistorico(cepInvalido))
                .thenThrow(new CepInvalidoException("CEP inválido"));

        assertThrows(CepInvalidoException.class, () -> consultaCepService.consultaCep(cepInvalido));
        verify(consultaCepFacade, times(1)).realizaConsultaCepESalvaHistorico(cepInvalido);
    }

    @Test
    void deveRetornarListaVaziaHistoricoCepVazio() {
        when(cepRepository.findAll()).thenReturn(new ArrayList<>());

        List<Cep> result = consultaCepService.consultaHistoricoCep();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(cepRepository, times(1)).findAll();
    }

    @Test
    void deveRetornarListaPreenchidaComHistoricoRegistrado() {
        Cep cep1 = Cep.builder()
                .id(1L)
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .estado("SP")
                .dataHora(LocalDateTime.now().toString())
                .build();

        Cep cep2 = Cep.builder()
                .id(2L)
                .cep("20040020")
                .logradouro("Avenida Rio Branco")
                .bairro("Centro")
                .cidade("Rio de Janeiro")
                .estado("RJ")
                .dataHora(LocalDateTime.now().toString())
                .build();

        List<Cep> listaCeps = Arrays.asList(cep1, cep2);
        when(cepRepository.findAll()).thenReturn(listaCeps);

        List<Cep> result = consultaCepService.consultaHistoricoCep();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("01310100", result.get(0).getCep());
        assertEquals("20040020", result.get(1).getCep());
        verify(cepRepository, times(1)).findAll();
    }

}

