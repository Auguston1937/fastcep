package br.com.fastcep.cep.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidaCepServiceTest {

    private ValidaCepService validaCepService;

    @BeforeEach
    void setUp() {
        validaCepService = new ValidaCepService();
    }

    @Test
    void deveRetornarTrueParaCepCom8Digitos() {
        String cep = "12345678";

        Boolean result = validaCepService.hasCepValidFormat(cep);

        assertTrue(result);
    }

    @Test
    void deveRetornarFalseParaCepMenorQue8Digitos() {
        String cep = "1234567";

        Boolean result = validaCepService.hasCepValidFormat(cep);

        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCepMaiorQue8Digitos() {
        String cep = "123456789";

        Boolean result = validaCepService.hasCepValidFormat(cep);

        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCepComLetras() {
        String cep = "1234567a";

        Boolean result = validaCepService.hasCepValidFormat(cep);

        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCepComCaracteresEspeciais() {
        String cep = "12345-678";

        Boolean result = validaCepService.hasCepValidFormat(cep);

        assertFalse(result);
    }

    @Test
    void deveRetornarFalseParaCepVazio() {
        String cep = "";

        Boolean result = validaCepService.hasCepValidFormat(cep);

        assertFalse(result);
    }


    @Test
    void deveRetornarFalseParaCepComEspaco() {
        String cep = "12345 78";

        Boolean result = validaCepService.hasCepValidFormat(cep);

        assertFalse(result);
    }
}

