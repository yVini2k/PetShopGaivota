package com.petshopgaivota.api.repository;

import com.petshopgaivota.api.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByNomeContainingIgnoreCase(String nome);
}