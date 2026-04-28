package br.com.fastcep.cep.dto;

public record CepHistoricoDTO(
        String cep,
        String logradouro,
        String bairro,
        String cidade,
        String estado,
        String dataHora
) {
}
