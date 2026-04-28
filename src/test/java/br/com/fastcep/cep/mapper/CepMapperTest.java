package br.com.fastcep.cep.mapper;

import br.com.fastcep.cep.entity.Cep;
import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CepMapperTest {

    private CepMapper cepMapper;

    @BeforeEach
    void setUp() {
        cepMapper = new CepMapper();
    }

    @Test
    void deveConverterJsonValidoParaCepDto() throws CepInvalidoException {
        String json = "{\"cep\":\"01310100\",\"logradouro\":\"Avenida Paulista\",\"bairro\":\"Bela Vista\",\"cidade\":\"São Paulo\",\"estado\":\"SP\"}";

        CepDTO cepDto = cepMapper.stringToCepDto(json);

        assertNotNull(cepDto);
        assertEquals("01310100", cepDto.cep());
        assertEquals("Avenida Paulista", cepDto.logradouro());
        assertEquals("Bela Vista", cepDto.bairro());
        assertEquals("São Paulo", cepDto.cidade());
        assertEquals("SP", cepDto.estado());
    }

    @Test
    void deveLancarErroParaConversaoJsonInvalido() {
        String json = "{json invalido}";

        assertThrows(CepInvalidoException.class, () -> cepMapper.stringToCepDto(json));
    }

    @Test
    void deveConverterCepEntityParaCepDtoCorretamente() {
        Cep cep = Cep.builder()
                .id(1L)
                .cep("01310100")
                .logradouro("Avenida Paulista")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .estado("SP")
                .dataHora(LocalDateTime.now().toString())
                .build();

        CepDTO result = cepMapper.domainToDTO(cep);

        assertEquals("01310100", result.cep());
        assertEquals("Avenida Paulista", result.logradouro());
        assertEquals("Bela Vista", result.bairro());
        assertEquals("São Paulo", result.cidade());
        assertEquals("SP", result.estado());
    }

    @Test
    void deveConverterCepDtoParaCepEntityCorretamente() {
        CepDTO cepDTO = new CepDTO(
                "01310100",
                "Avenida Paulista",
                "Bela Vista",
                "São Paulo",
                "SP"
        );

        Cep result = cepMapper.dtoToEntity(cepDTO);

        assertNotNull(result);
        assertEquals("01310100", result.getCep());
        assertEquals("Avenida Paulista", result.getLogradouro());
        assertEquals("Bela Vista", result.getBairro());
        assertEquals("São Paulo", result.getCidade());
        assertEquals("SP", result.getEstado());
        assertNotNull(result.getDataHora());
    }

}

