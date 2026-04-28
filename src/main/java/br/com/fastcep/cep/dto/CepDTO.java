package br.com.fastcep.cep.dto;

public record CepDTO(
        String cep,
        String logradouro,
        String bairro,
        String cidade,
        String estado
) {
}
