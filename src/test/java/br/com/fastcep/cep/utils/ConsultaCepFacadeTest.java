package br.com.fastcep.cep.utils;

import br.com.fastcep.cep.entity.Cep;
import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import br.com.fastcep.cep.mapper.CepMapper;
import br.com.fastcep.cep.repository.CepRepository;
import br.com.fastcep.service.utils.RestTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaCepFacadeTest {

    @Mock
    private CepRepository cepRepository;

    @Mock
    private RestTemplateService restTemplateService;

    @Mock
    private CepMapper cepMapper;

    @Mock
    private ValidaCepService validaCepService;

    private ConsultaCepFacade consultaCepFacade;

    @BeforeEach
    void setUp() {
        consultaCepFacade = new ConsultaCepFacade(
                cepRepository,
                restTemplateService,
                cepMapper,
                validaCepService
        );
    }

    @Test
    void deveRealizarConsultaCepESalvarHistoricoComSucesso() throws CepInvalidoException {
        String cep = "01310100";
        String json = "{\"cep\":\"01310100\",\"logradouro\":\"Avenida Paulista\",\"bairro\":\"Bela Vista\",\"cidade\":\"São Paulo\",\"estado\":\"SP\"}";
        CepDTO cepDTO = new CepDTO("01310100", "Avenida Paulista", "Bela Vista", "São Paulo", "SP");
        Cep cepEntity = Cep.builder()
                .id(1L)
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        when(validaCepService.hasCepValidFormat(cep)).thenReturn(true);
        when(restTemplateService.consultaCepApiExterna(cep))
                .thenReturn(new ResponseEntity<>(json, HttpStatus.OK));
        when(cepMapper.stringToCepDto(json)).thenReturn(cepDTO);
        when(cepMapper.dtoToEntity(cepDTO)).thenReturn(cepEntity);

        CepDTO result = consultaCepFacade.realizaConsultaCepESalvaHistorico(cep);

        assertNotNull(result);
        assertEquals(cepDTO, result);
        verify(validaCepService, times(1)).hasCepValidFormat(cep);
        verify(restTemplateService, times(1)).consultaCepApiExterna(cep);
        verify(cepMapper, times(1)).stringToCepDto(json);
        verify(cepRepository, times(1)).save(any(Cep.class));
    }

    @Test
    void deveLancarExcecaoParaCepInvalido() throws CepInvalidoException {
        String cep = "1234567"; // CEP com formato inválido

        when(validaCepService.hasCepValidFormat(cep)).thenReturn(false);

        assertThrows(CepInvalidoException.class, () ->
                consultaCepFacade.realizaConsultaCepESalvaHistorico(cep)
        );
        verify(validaCepService, times(1)).hasCepValidFormat(cep);
        verify(restTemplateService, never()).consultaCepApiExterna(anyString());
        verify(cepRepository, never()).save(any());
    }

    @Test
    void testRealizaConsultaCepInvalidJsonResponse() throws CepInvalidoException {
        String cep = "01310100";
        String json = "{json invalido}";

        when(validaCepService.hasCepValidFormat(cep)).thenReturn(true);
        when(restTemplateService.consultaCepApiExterna(cep))
                .thenReturn(new ResponseEntity<>(json, HttpStatus.OK));
        when(cepMapper.stringToCepDto(json))
                .thenThrow(new CepInvalidoException("CEP informado está mal formatado"));

        assertThrows(CepInvalidoException.class, () ->
                consultaCepFacade.realizaConsultaCepESalvaHistorico(cep)
        );
        verify(cepRepository, never()).save(any());
    }

    @Test
    void deveSalveHistoricoConsultaCorretamente() {
        CepDTO cepDTO = new CepDTO("01310100", "Avenida Paulista", "Bela Vista", "São Paulo", "SP");
        Cep cepEntity = Cep.builder()
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .estado("SP")
                .build();

        when(cepMapper.dtoToEntity(cepDTO)).thenReturn(cepEntity);

        consultaCepFacade.salvarConsultaHistorico(cepDTO);

        verify(cepMapper, times(1)).dtoToEntity(cepDTO);
        verify(cepRepository, times(1)).save(cepEntity);
    }

    @Test
    void deveLancarExcecaoParaCepNaoEncontradoPorApiExterna() throws CepInvalidoException {
        String cep = "99999999";

        when(validaCepService.hasCepValidFormat(cep)).thenReturn(true);
        when(restTemplateService.consultaCepApiExterna(cep))
                .thenThrow(new CepInvalidoException("CEP não encontrado"));

        assertThrows(CepInvalidoException.class, () ->
                consultaCepFacade.realizaConsultaCepESalvaHistorico(cep)
        );
        verify(cepRepository, never()).save(any());
    }

}





