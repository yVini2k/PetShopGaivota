
package com.petshopgaivota.api.service;

import com.petshopgaivota.api.entity.Agendamento;
import com.petshopgaivota.api.entity.RegistroIdempotencia;
import com.petshopgaivota.api.repository.RegistroIdempotenciaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.petshopgaivota.api.exception.ConflitoIdempotenciaException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class AgendamentoIdempotenteService {

    private final AgendamentoService agendamentoService;
    private final RegistroIdempotenciaRepository registroRepo;

    public AgendamentoIdempotenteService(
            AgendamentoService agendamentoService,
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

        String hash = calcularHash(novo);

        Optional<RegistroIdempotencia> existente =
                registroRepo.findById(chave);

        if (existente.isPresent()) {

            RegistroIdempotencia registro = existente.get();

            if (!hash.equals(registro.getHashRequisicao())) {
                throw new ConflitoIdempotenciaException(
                        "A chave de idempotência já foi utilizada com dados diferentes.");
            }

            Agendamento anterior = agendamentoService.buscarPorId(
                    registro.getAgendamentoId());

            return new Resultado(anterior, true);
        }

        Agendamento salvo = agendamentoService.salvar(novo);

        RegistroIdempotencia registro = new RegistroIdempotencia();

        registro.setChave(chave);
        registro.setAgendamentoId(salvo.getId());
        registro.setHashRequisicao(hash);

        registroRepo.saveAndFlush(registro);

        return new Resultado(salvo, false);
    }

    private String calcularHash(Agendamento agendamento) {

        String dados = String.join("|",
                String.valueOf(agendamento.getData()),
                String.valueOf(agendamento.getHorario()),
                String.valueOf(agendamento.getStatus()),
                String.valueOf(agendamento.getPet() == null
                        ? null : agendamento.getPet().getId()),
                String.valueOf(agendamento.getFuncionario() == null
                        ? null : agendamento.getFuncionario().getId()),
                String.valueOf(agendamento.getServico() == null
                        ? null : agendamento.getServico().getId())
        );

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    dados.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "Erro ao calcular hash da requisição.", e);
        }
    }
}
