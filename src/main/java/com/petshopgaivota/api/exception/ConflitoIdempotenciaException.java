package com.petshopgaivota.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflitoIdempotenciaException
        extends RuntimeException {

    public ConflitoIdempotenciaException(String mensagem) {
        super(mensagem);
    }
}