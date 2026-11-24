package com.petshopgaivota.api.controller;

import com.petshopgaivota.api.dto.ClienteV2Request;
import com.petshopgaivota.api.dto.ClienteV2Response;
import com.petshopgaivota.api.entity.Cliente;
import com.petshopgaivota.api.entity.Contato;
import com.petshopgaivota.api.service.ClienteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
        value = "/api/clientes",
        headers = "X-API-Version=2"
)
@Tag(
        name = "Clientes V2",
        description = "Gerenciamento de clientes - Versão 2"
)
public class ClienteV2Controller {

    private final ClienteService service;

    public ClienteV2Controller(ClienteService service) {
        this.service = service;
    }

    // Converter entidade para DTO
    private ClienteV2Response converter(Cliente cliente) {

        Contato contato = cliente.getContato();

        return new ClienteV2Response(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                contato != null ? contato.getTelefone() : null,
                contato != null ? contato.getEndereco() : null
        );
    }

    // Preencher dados do cliente
    private void preencher(
            Cliente cliente,
            ClienteV2Request dto) {

        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());

        Contato contato = cliente.getContato();

        if (contato == null) {
            contato = new Contato();
        }

        contato.setTelefone(dto.telefone());
        contato.setEndereco(dto.endereco());

        cliente.setContato(contato);
    }

    // LISTAR
    @Operation(summary = "Listar clientes V2")
    @GetMapping
    public ResponseEntity<Page<ClienteV2Response>> listarV2(
            Pageable pageable) {

        return ResponseEntity.ok(
                service.listar(pageable)
                        .map(this::converter)
        );
    }

    // BUSCAR POR ID
    @Operation(summary = "Buscar cliente por ID V2")
    @GetMapping("/{id}")
    public ResponseEntity<ClienteV2Response> buscarPorIdV2(
            @PathVariable Long id) {

        Cliente cliente = service.buscarPorId(id);

        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(converter(cliente));
    }

    // BUSCAR POR NOME
    @Operation(summary = "Buscar clientes por nome V2")
    @GetMapping("/buscar")
    public ResponseEntity<List<ClienteV2Response>> buscarPorNomeV2(
            @RequestParam String nome) {

        return ResponseEntity.ok(
                service.buscarPorNome(nome)
                        .stream()
                        .map(this::converter)
                        .toList()
        );
    }

    // CADASTRAR
    @Operation(summary = "Cadastrar cliente V2")
    @PostMapping
    public ResponseEntity<ClienteV2Response> salvarV2(
            @Valid @RequestBody ClienteV2Request dto) {

        Cliente cliente = new Cliente();

        preencher(cliente, dto);

        Cliente salvo = service.salvar(cliente);

        return ResponseEntity.status(201)
                .body(converter(salvo));
    }

    // ATUALIZAR
    @Operation(summary = "Atualizar cliente V2")
    @PutMapping("/{id}")
    public ResponseEntity<ClienteV2Response> atualizarV2(
            @PathVariable Long id,
            @Valid @RequestBody ClienteV2Request dto) {

        Cliente cliente = service.buscarPorId(id);

        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        preencher(cliente, dto);

        Cliente atualizado = service.salvar(cliente);

        return ResponseEntity.ok(converter(atualizado));
    }

    // EXCLUIR
    @Operation(summary = "Excluir cliente V2")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirV2(
            @PathVariable Long id) {

        Cliente cliente = service.buscarPorId(id);

        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}