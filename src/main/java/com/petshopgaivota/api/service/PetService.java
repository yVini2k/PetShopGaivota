package com.petshopgaivota.api.service;

import com.petshopgaivota.api.entity.Pet;
import com.petshopgaivota.api.repository.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    private final PetRepository repository;

    public PetService(PetRepository repository) {
        this.repository = repository;
    }

    public Page<Pet> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Pet buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Pet salvar(Pet pet) {
        return repository.save(pet);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public List<Pet> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }
}