package com.petshopgaivota.api.controller;

import com.petshopgaivota.api.entity.Contato;
import com.petshopgaivota.api.service.ContatoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(
        name = "Contatos",
        description = "Operações relacionadas aos contatos do Pet Shop Gaivota"
)
@RestController
@RequestMapping("/api/contatos")
public class ContatoController {

    private final ContatoService service;

    public ContatoController(ContatoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Listar contatos",
            description = "Retorna uma lista paginada de contatos cadastrados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contatos encontrados com sucesso"
            )
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Contato>>> listar(
            Pageable pageable) {

        Page<Contato> pagina = service.listar(pageable);

        List<EntityModel<Contato>> contatos = pagina.getContent()
                .stream()
                .map(contato -> EntityModel.of(
                        contato,
                        linkTo(methodOn(ContatoController.class)
                                .buscarPorId(contato.getId()))
                                .withSelfRel()
                ))
                .toList();

        PagedModel.PageMetadata metadata =
                new PagedModel.PageMetadata(
                        pagina.getSize(),
                        pagina.getNumber(),
                        pagina.getTotalElements()
                );

        PagedModel<EntityModel<Contato>> resultado =
                PagedModel.of(contatos, metadata);

        resultado.add(
                linkTo(methodOn(ContatoController.class)
                        .listar(pageable))
                        .withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar contatos por endereço",
            description = "Retorna os contatos cujo endereço contém o texto informado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca realizada com sucesso"
            ),
            @ApiResponse(responseCode = "400", description = "Status informado é inválido")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<Contato>>> buscarPorEndereco(
            @RequestParam String endereco) {

        List<EntityModel<Contato>> contatos =
                service.buscarPorEndereco(endereco)
                        .stream()
                        .map(contato -> EntityModel.of(
                                contato,
                                linkTo(methodOn(ContatoController.class)
                                        .buscarPorId(contato.getId()))
                                        .withSelfRel()
                        ))
                        .toList();

        CollectionModel<EntityModel<Contato>> resultado =
                CollectionModel.of(contatos);

        resultado.add(
                linkTo(methodOn(ContatoController.class)
                        .buscarPorEndereco(endereco))
                        .withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar contato por ID",
            description = "Retorna os dados de um contato específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contato encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contato não encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Contato>> buscarPorId(
            @PathVariable Long id) {

        Contato contato = service.buscarPorId(id);

        if (contato == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Contato> resultado = EntityModel.of(
                contato,
                linkTo(methodOn(ContatoController.class)
                        .buscarPorId(id))
                        .withSelfRel(),

                linkTo(methodOn(ContatoController.class)
                        .listar(null))
                        .withRel("contatos")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Cadastrar contato",
            description = "Cadastra um novo contato no sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Contato cadastrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    @PostMapping
    public ResponseEntity<EntityModel<Contato>> salvar(
            @Valid @RequestBody Contato contato) {

        Contato novoContato = service.salvar(contato);

        EntityModel<Contato> resultado = EntityModel.of(
                novoContato,
                linkTo(methodOn(ContatoController.class)
                        .buscarPorId(novoContato.getId()))
                        .withSelfRel(),

                linkTo(methodOn(ContatoController.class)
                        .listar(null))
                        .withRel("contatos")
        );

        return ResponseEntity.status(201).body(resultado);
    }

    @Operation(
            summary = "Atualizar contato",
            description = "Atualiza os dados de um contato existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Contato atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contato não encontrado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Contato>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Contato contato) {

        Contato contatoExistente = service.buscarPorId(id);

        if (contatoExistente == null) {
            return ResponseEntity.notFound().build();
        }

        contatoExistente.setTelefone(contato.getTelefone());
        contatoExistente.setEndereco(contato.getEndereco());

        Contato atualizado = service.salvar(contatoExistente);

        EntityModel<Contato> resultado = EntityModel.of(
                atualizado,
                linkTo(methodOn(ContatoController.class)
                        .buscarPorId(id))
                        .withSelfRel(),

                linkTo(methodOn(ContatoController.class)
                        .listar(null))
                        .withRel("contatos")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Excluir contato",
            description = "Exclui um contato cadastrado pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Contato excluído com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Contato não encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        Contato contato = service.buscarPorId(id);

        if (contato == null) {
            return ResponseEntity.notFound().build();
        }

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}