package br.com.fastcep.cep.service;

import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.dto.CepHistoricoDTO;
import br.com.fastcep.cep.entity.Cep;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import br.com.fastcep.cep.gateway.RestTemplateService;
import br.com.fastcep.cep.mapper.CepMapper;
import br.com.fastcep.cep.repository.CepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaCepUseCaseTest {

    @Mock
    private CepRepository cepRepository;

    @Mock
    private RestTemplateService restTemplateService;

    @Mock
    private CepMapper cepMapper;

    private ConsultaCepUseCase consultaCepUseCase;

    @BeforeEach
    void setUp() {
        consultaCepUseCase = new ConsultaCepUseCase(cepRepository, restTemplateService, cepMapper);
    }

    @Test
    void deveRealizarConsultaCepCorretamente() throws CepInvalidoException {
        String cep = "01310100";
        String jsonResponse = "{\"cep\":\"01310100\",\"logradouro\":\"Avenida Paulista\",\"bairro\":\"Bela Vista\",\"cidade\":\"São Paulo\",\"estado\":\"SP\"}";
        CepDTO expectedCepDTO = new CepDTO("01310100", "Avenida Paulista", "Bela Vista", "São Paulo", "SP");
        Cep cepEntity = new Cep();

        when(restTemplateService.consultaCepApiExterna(cep)).thenReturn(ResponseEntity.ok(jsonResponse));
        when(cepMapper.stringToCepDto(jsonResponse)).thenReturn(expectedCepDTO);
        when(cepMapper.dtoToEntity(expectedCepDTO)).thenReturn(cepEntity);

        CepDTO result = consultaCepUseCase.consultarCep(cep);

        assertNotNull(result);
        assertEquals(expectedCepDTO, result);
        verify(restTemplateService, times(1)).consultaCepApiExterna(cep);
        verify(cepMapper, times(1)).stringToCepDto(jsonResponse);
        verify(cepMapper, times(1)).dtoToEntity(expectedCepDTO);
        verify(cepRepository, times(1)).save(cepEntity);
    }

    @Test
    void deveLancarExcecaoQuandoCepNaoEncontrado() throws CepInvalidoException {
        String cep = "99999999";
        String jsonResponse = "{}";

        when(restTemplateService.consultaCepApiExterna(cep)).thenReturn(ResponseEntity.ok(jsonResponse));
        when(cepMapper.stringToCepDto(jsonResponse)).thenReturn(null);

        assertThrows(CepInvalidoException.class, () -> consultaCepUseCase.consultarCep(cep));
        verify(restTemplateService, times(1)).consultaCepApiExterna(cep);
        verify(cepMapper, times(1)).stringToCepDto(jsonResponse);
        verifyNoMoreInteractions(cepMapper);
    }

    @Test
    void deveRetornarListaVaziaHistoricoCepVazio() {
        when(cepRepository.findAll()).thenReturn(List.of());

        List<CepHistoricoDTO> result = consultaCepUseCase.consultarHistoricoCep();

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

        CepHistoricoDTO dto1 = new CepHistoricoDTO("01310100", "Avenida Paulista", "Bela Vista", "São Paulo", "SP", "2024-04-27T10:00:00");
        CepHistoricoDTO dto2 = new CepHistoricoDTO("20040020", "Avenida Rio Branco", "Centro", "Rio de Janeiro", "RJ", "2024-04-27T11:00:00");

        List<Cep> listaCeps = Arrays.asList(cep1, cep2);
        when(cepRepository.findAll()).thenReturn(listaCeps);
        when(cepMapper.entityToHistoricoDTO(cep1)).thenReturn(dto1);
        when(cepMapper.entityToHistoricoDTO(cep2)).thenReturn(dto2);

        List<CepHistoricoDTO> result = consultaCepUseCase.consultarHistoricoCep();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("01310100", result.get(0).cep());
        assertEquals("20040020", result.get(1).cep());
        verify(cepRepository, times(1)).findAll();
        verify(cepMapper, times(1)).entityToHistoricoDTO(cep1);
        verify(cepMapper, times(1)).entityToHistoricoDTO(cep2);
    }
}
