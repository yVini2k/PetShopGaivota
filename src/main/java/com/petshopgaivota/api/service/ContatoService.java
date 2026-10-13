package com.petshopgaivota.api.service;

import com.petshopgaivota.api.entity.Contato;
import com.petshopgaivota.api.repository.ContatoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContatoService {

    private final ContatoRepository repository;

    public ContatoService(ContatoRepository repository) {
        this.repository = repository;
    }

    public Page<Contato> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Contato buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Contato salvar(Contato contato) {
        return repository.save(contato);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public List<Contato> buscarPorEndereco(String endereco) {
        return repository.findByEnderecoContainingIgnoreCase(endereco);
    }
}