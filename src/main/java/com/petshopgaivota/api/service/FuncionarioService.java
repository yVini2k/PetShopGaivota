package com.petshopgaivota.api.service;

import com.petshopgaivota.api.entity.Funcionario;
import com.petshopgaivota.api.repository.FuncionarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FuncionarioService {

    private final FuncionarioRepository repository;

    public FuncionarioService(FuncionarioRepository repository) {
        this.repository = repository;
    }

    public Page<Funcionario> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Funcionario buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Funcionario salvar(Funcionario funcionario) {
        return repository.save(funcionario);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public List<Funcionario> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }
}