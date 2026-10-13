package com.petshopgaivota.api.service;

import com.petshopgaivota.api.entity.Servico;
import com.petshopgaivota.api.enums.TipoServico;
import com.petshopgaivota.api.repository.ServicoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository repository;

    public ServicoService(ServicoRepository repository) {
        this.repository = repository;
    }

    public Page<Servico> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Servico buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Servico salvar(Servico servico) {
        return repository.save(servico);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public List<Servico> buscarPorTipo(TipoServico tipo) {
        return repository.findByTipo(tipo);
    }
}