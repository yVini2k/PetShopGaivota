package com.petshopgaivota.api.dto;

import com.petshopgaivota.api.enums.StatusAgendamento;

import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoV2Response(
        Long id,
        LocalDate data,
        LocalTime horario,
        StatusAgendamento status,
        String nomePet,
        String nomeCliente) {
}