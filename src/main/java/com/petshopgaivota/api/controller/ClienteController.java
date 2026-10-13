package com.petshopgaivota.api.controller;

import com.petshopgaivota.api.entity.Cliente;
import com.petshopgaivota.api.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Tag(
        name = "Clientes",
        description = "Operações relacionadas aos clientes do Pet Shop Gaivota"
)
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @Operation(
            summary = "Listar clientes",
            description = "Retorna uma lista paginada de clientes cadastrados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Clientes encontrados com sucesso"
            )
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Cliente>>> listar(
            Pageable pageable) {

        Page<Cliente> pagina = service.listar(pageable);

        List<EntityModel<Cliente>> clientes = pagina.getContent()
                .stream()
                .map(cliente -> EntityModel.of(
                        cliente,
                        linkTo(methodOn(ClienteController.class)
                                .buscarPorId(cliente.getId()))
                                .withSelfRel()
                ))
                .toList();

        PagedModel.PageMetadata metadata =
                new PagedModel.PageMetadata(
                        pagina.getSize(),
                        pagina.getNumber(),
                        pagina.getTotalElements()
                );

        PagedModel<EntityModel<Cliente>> resultado =
                PagedModel.of(clientes, metadata);

        resultado.add(
                linkTo(methodOn(ClienteController.class)
                        .listar(pageable))
                        .withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar clientes por nome",
            description = "Retorna os clientes cujo nome contém o texto informado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca realizada com sucesso"
            ),
            @ApiResponse(responseCode = "400", description = "Status informado é inválido")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<Cliente>>> buscarPorNome(
            @RequestParam String nome) {

        List<EntityModel<Cliente>> clientes =
                service.buscarPorNome(nome)
                        .stream()
                        .map(cliente -> EntityModel.of(
                                cliente,
                                linkTo(methodOn(ClienteController.class)
                                        .buscarPorId(cliente.getId()))
                                        .withSelfRel()
                        ))
                        .toList();

        CollectionModel<EntityModel<Cliente>> resultado =
                CollectionModel.of(clientes);

        resultado.add(
                linkTo(methodOn(ClienteController.class)
                        .buscarPorNome(nome))
                        .withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar cliente por ID",
            description = "Retorna os dados de um cliente específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente não encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> buscarPorId(
            @PathVariable Long id) {

        Cliente cliente = service.buscarPorId(id);

        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Cliente> resultado = EntityModel.of(
                cliente,
                linkTo(methodOn(ClienteController.class)
                        .buscarPorId(id))
                        .withSelfRel(),

                linkTo(methodOn(ClienteController.class)
                        .listar(null))
                        .withRel("clientes")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Cadastrar cliente",
            description = "Cadastra um novo cliente no sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Cliente cadastrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Cliente>> salvar(
            @Valid @RequestBody Cliente cliente) {

        Cliente novoCliente = service.salvar(cliente);

        EntityModel<Cliente> resultado = EntityModel.of(
                novoCliente,
                linkTo(methodOn(ClienteController.class)
                        .buscarPorId(novoCliente.getId()))
                        .withSelfRel(),

                linkTo(methodOn(ClienteController.class)
                        .listar(null))
                        .withRel("clientes")
        );

        return ResponseEntity.status(201).body(resultado);
    }

    @Operation(
            summary = "Atualizar cliente",
            description = "Atualiza os dados de um cliente existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cliente atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente não encontrado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Cliente>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {

        Cliente clienteExistente = service.buscarPorId(id);

        if (clienteExistente == null) {
            return ResponseEntity.notFound().build();
        }

        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setEmail(cliente.getEmail());
        clienteExistente.setContato(cliente.getContato());

        Cliente atualizado = service.salvar(clienteExistente);

        EntityModel<Cliente> resultado = EntityModel.of(
                atualizado,
                linkTo(methodOn(ClienteController.class)
                        .buscarPorId(id))
                        .withSelfRel(),

                linkTo(methodOn(ClienteController.class)
                        .listar(null))
                        .withRel("clientes")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Excluir cliente",
            description = "Exclui um cliente cadastrado pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Cliente excluído com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cliente não encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        Cliente cliente = service.buscarPorId(id);

        if (cliente == null) {
            return ResponseEntity.notFound().build();
        }

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}