package com.petshopgaivota.api.repository;

import com.petshopgaivota.api.entity.Contato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContatoRepository extends JpaRepository<Contato, Long> {

    List<Contato> findByEnderecoContainingIgnoreCase(String endereco);
}