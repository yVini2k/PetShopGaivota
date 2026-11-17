package com.petshopgaivota.api.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public record ErroResponse(int status, String erro, String mensagem,
                           List<String> detalhes, Instant timestamp) {

    public static ErroResponse of(HttpStatus s, String msg, List<String> det) {
        return new ErroResponse(s.value(), s.getReasonPhrase(), msg, det, Instant.now());
    }
}