package br.com.fastcep.cep.utils;

import org.springframework.stereotype.Service;

@Service
public class ValidaCepService {

    public Boolean hasCepValidFormat(String valorCep) {
        return valorCep.matches("^\\d{8}$");
    }
}
