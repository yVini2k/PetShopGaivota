package com.petshopgaivota.api.controller;

import com.petshopgaivota.api.entity.Pet;
import com.petshopgaivota.api.service.PetService;
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
        name = "Pets",
        description = "Operações relacionadas aos pets do Pet Shop Gaivota"
)
@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService service;

    public PetController(PetService service) {
        this.service = service;
    }

    @Operation(
            summary = "Buscar pets por nome",
            description = "Retorna os pets cujo nome contém o texto informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<Pet>>> buscarPorNome(
            @RequestParam String nome) {

        List<EntityModel<Pet>> pets = service.buscarPorNome(nome)
                .stream()
                .map(pet -> EntityModel.of(
                        pet,
                        linkTo(methodOn(PetController.class)
                                .buscarPorId(pet.getId())).withSelfRel()
                ))
                .toList();

        CollectionModel<EntityModel<Pet>> resultado =
                CollectionModel.of(pets);

        resultado.add(
                linkTo(methodOn(PetController.class)
                        .buscarPorNome(nome)).withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Listar pets",
            description = "Retorna uma lista paginada de pets cadastrados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pets encontrados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status informado é inválido")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Pet>>> listar(
            Pageable pageable) {

        Page<Pet> pagina = service.listar(pageable);

        List<EntityModel<Pet>> pets = pagina.getContent()
                .stream()
                .map(pet -> EntityModel.of(
                        pet,
                        linkTo(methodOn(PetController.class)
                                .buscarPorId(pet.getId())).withSelfRel()
                ))
                .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pagina.getSize(),
                pagina.getNumber(),
                pagina.getTotalElements()
        );

        PagedModel<EntityModel<Pet>> resultado =
                PagedModel.of(pets, metadata);

        resultado.add(
                linkTo(methodOn(PetController.class)
                        .listar(pageable)).withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar pet por ID",
            description = "Retorna os dados de um pet específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pet encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Pet>> buscarPorId(
            @PathVariable Long id) {

        Pet pet = service.buscarPorId(id);

        if (pet == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Pet> resultado = EntityModel.of(
                pet,
                linkTo(methodOn(PetController.class)
                        .buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(PetController.class)
                        .listar(null)).withRel("pets")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Cadastrar pet",
            description = "Cadastra um novo pet no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pet cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Pet>> salvar(
            @Valid @RequestBody Pet pet) {

        Pet novoPet = service.salvar(pet);

        EntityModel<Pet> resultado = EntityModel.of(
                novoPet,
                linkTo(methodOn(PetController.class)
                        .buscarPorId(novoPet.getId())).withSelfRel(),
                linkTo(methodOn(PetController.class)
                        .listar(null)).withRel("pets")
        );

        return ResponseEntity.status(201).body(resultado);
    }

    @Operation(
            summary = "Atualizar pet",
            description = "Atualiza os dados de um pet existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pet atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Pet>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Pet pet) {

        Pet petExistente = service.buscarPorId(id);

        if (petExistente == null) {
            return ResponseEntity.notFound().build();
        }

        petExistente.setNome(pet.getNome());
        petExistente.setTamanho(pet.getTamanho());
        petExistente.setCliente(pet.getCliente());
        petExistente.setServicos(pet.getServicos());

        Pet atualizado = service.salvar(petExistente);

        EntityModel<Pet> resultado = EntityModel.of(
                atualizado,
                linkTo(methodOn(PetController.class)
                        .buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(PetController.class)
                        .listar(null)).withRel("pets")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Excluir pet",
            description = "Exclui um pet cadastrado pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Pet excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        Pet pet = service.buscarPorId(id);

        if (pet == null) {
            return ResponseEntity.notFound().build();
        }

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
