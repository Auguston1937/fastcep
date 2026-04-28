package br.com.fastcep.cep.mapper;

import br.com.fastcep.cep.entity.Cep;
import br.com.fastcep.cep.dto.CepDTO;
import br.com.fastcep.cep.exceptions.CepInvalidoException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CepMapper {

    private static final Logger log = LoggerFactory.getLogger(CepMapper.class);

    public CepDTO stringToCepDto(String jsonResponse) throws CepInvalidoException {
        try {
            Gson gson = new Gson();
            return gson.fromJson(jsonResponse, CepDTO.class);
        } catch (JsonSyntaxException e) {
            log.error("Erro ao converter resposta da API externa para CepDTO: {}", e.getMessage());
            throw new CepInvalidoException(e.getMessage());
        }
    }

    public CepDTO domainToDTO(Cep entity) {
        return new CepDTO(
                entity.getCep(),
                entity.getLogradouro(),
                entity.getBairro(),
                entity.getCidade(),
                entity.getEstado()
        );
    }

    public Cep dtoToEntity(CepDTO cepDTO) {
        Cep cep = new Cep();
        cep.setCep(cepDTO.cep());
        cep.setLogradouro(cepDTO.logradouro());
        cep.setBairro(cepDTO.bairro());
        cep.setCidade(cepDTO.cidade());
        cep.setEstado(cepDTO.estado());
        cep.setDataHora(LocalDateTime.now().toString());
        return cep;
    }

}


