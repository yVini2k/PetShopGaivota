package com.petshopgaivota.api.repository;

import com.petshopgaivota.api.entity.Servico;
import com.petshopgaivota.api.enums.TipoServico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByTipo(TipoServico tipo);
}