package com.petshopgaivota.api.service;

import com.petshopgaivota.api.entity.Agendamento;
import com.petshopgaivota.api.entity.RegistroIdempotencia;
import com.petshopgaivota.api.repository.RegistroIdempotenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AgendamentoIdempotenteService {

    private final AgendamentoService agendamentoService;
    private final RegistroIdempotenciaRepository registroRepo;

    public AgendamentoIdempotenteService(AgendamentoService agendamentoService,
                                         RegistroIdempotenciaRepository registroRepo) {
        this.agendamentoService = agendamentoService;
        this.registroRepo = registroRepo;
    }

    public record Resultado(Agendamento agendamento, boolean repetido) {}

    @Transactional
    public Resultado salvar(String chave, Agendamento novo) {
        if (chave == null || chave.isBlank()) {
            return new Resultado(agendamentoService.salvar(novo), false);
        }

        Optional<RegistroIdempotencia> existente = registroRepo.findById(chave);
        if (existente.isPresent()) {
            Agendamento anterior = agendamentoService.buscarPorId(existente.get().getAgendamentoId());
            return new Resultado(anterior, true);
        }

        Agendamento salvo = agendamentoService.salvar(novo);

        RegistroIdempotencia registro = new RegistroIdempotencia();
        registro.setChave(chave);
        registro.setAgendamentoId(salvo.getId());
        registroRepo.saveAndFlush(registro); // se duas requisições iguais chegarem juntas, uma falha aqui (409)

        return new Resultado(salvo, false);
    }
}