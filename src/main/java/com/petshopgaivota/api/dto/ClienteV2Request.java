package com.petshopgaivota.api.dto;

import jakarta.validation.constraints.*;

public record ClienteV2Request(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100)
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "O telefone é obrigatório")
        @Pattern(regexp = "\\d{10,11}",
                message = "O telefone deve ter 10 ou 11 dígitos")
        String telefone,

        @NotBlank(message = "O endereço é obrigatório")
        @Size(min = 5, max = 200)
        String endereco

) {
}