package com.petshopgaivota.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class RegistroIdempotencia {

    @Id
    private String chave;

    private Long agendamentoId;

    private Instant criadoEm = Instant.now();

    public String getChave() { return chave; }
    public void setChave(String chave) { this.chave = chave; }
    public Long getAgendamentoId() { return agendamentoId; }
    public void setAgendamentoId(Long agendamentoId) { this.agendamentoId = agendamentoId; }
    public Instant getCriadoEm() { return criadoEm; }
    public void setCriadoEm(Instant criadoEm) { this.criadoEm = criadoEm; }
}