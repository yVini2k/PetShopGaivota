package com.petshopgaivota.api.repository;

import com.petshopgaivota.api.entity.Agendamento;
import com.petshopgaivota.api.enums.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByStatus(StatusAgendamento status);
}