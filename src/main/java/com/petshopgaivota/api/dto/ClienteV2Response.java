package com.petshopgaivota.api.dto;

public record ClienteV2Response(
        Long id,
        String nome,
        String email,
        String telefone,
        String endereco
) {
}