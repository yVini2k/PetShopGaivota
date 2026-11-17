package com.petshopgaivota.api.repository;

import com.petshopgaivota.api.entity.RegistroIdempotencia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroIdempotenciaRepository extends JpaRepository<RegistroIdempotencia, String> {
}