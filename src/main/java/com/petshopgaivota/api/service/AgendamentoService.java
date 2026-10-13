package com.petshopgaivota.api.service;

import com.petshopgaivota.api.entity.Agendamento;
import com.petshopgaivota.api.enums.StatusAgendamento;
import com.petshopgaivota.api.repository.AgendamentoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;

    public AgendamentoService(AgendamentoRepository repository) {
        this.repository = repository;
    }

    public Page<Agendamento> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Agendamento buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Agendamento salvar(Agendamento agendamento) {
        return repository.save(agendamento);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public List<Agendamento> buscarPorStatus(StatusAgendamento status) {
        return repository.findByStatus(status);
    }
}