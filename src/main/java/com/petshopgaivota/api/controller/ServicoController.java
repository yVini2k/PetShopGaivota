package com.petshopgaivota.api.controller;

import com.petshopgaivota.api.entity.Servico;
import com.petshopgaivota.api.enums.TipoServico;
import com.petshopgaivota.api.service.ServicoService;
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
        name = "Serviços",
        description = "Operações relacionadas aos serviços do Pet Shop Gaivota"
)
@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService service;

    public ServicoController(ServicoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Listar serviços",
            description = "Retorna uma lista paginada de serviços cadastrados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviços encontrados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status informado é inválido")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Servico>>> listar(
            Pageable pageable) {

        Page<Servico> pagina = service.listar(pageable);

        List<EntityModel<Servico>> servicos = pagina.getContent()
                .stream()
                .map(servico -> EntityModel.of(
                        servico,
                        linkTo(methodOn(ServicoController.class)
                                .buscarPorId(servico.getId())).withSelfRel()
                ))
                .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pagina.getSize(),
                pagina.getNumber(),
                pagina.getTotalElements()
        );

        PagedModel<EntityModel<Servico>> resultado =
                PagedModel.of(servicos, metadata);

        resultado.add(
                linkTo(methodOn(ServicoController.class)
                        .listar(pageable)).withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar serviços por tipo",
            description = "Retorna os serviços de acordo com o tipo informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Tipo de serviço inválido")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<Servico>>> buscarPorTipo(
            @RequestParam TipoServico tipo) {

        List<EntityModel<Servico>> servicos =
                service.buscarPorTipo(tipo)
                        .stream()
                        .map(servico -> EntityModel.of(
                                servico,
                                linkTo(methodOn(ServicoController.class)
                                        .buscarPorId(servico.getId())).withSelfRel()
                        ))
                        .toList();

        CollectionModel<EntityModel<Servico>> resultado =
                CollectionModel.of(servicos);

        resultado.add(
                linkTo(methodOn(ServicoController.class)
                        .buscarPorTipo(tipo)).withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar serviço por ID",
            description = "Retorna os dados de um serviço específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Servico>> buscarPorId(
            @PathVariable Long id) {

        Servico servico = service.buscarPorId(id);

        if (servico == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Servico> resultado = EntityModel.of(
                servico,
                linkTo(methodOn(ServicoController.class)
                        .buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(ServicoController.class)
                        .listar(null)).withRel("servicos")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Cadastrar serviço",
            description = "Cadastra um novo serviço no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Serviço cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Servico>> salvar(
            @Valid @RequestBody Servico servico) {

        Servico novoServico = service.salvar(servico);

        EntityModel<Servico> resultado = EntityModel.of(
                novoServico,
                linkTo(methodOn(ServicoController.class)
                        .buscarPorId(novoServico.getId())).withSelfRel(),
                linkTo(methodOn(ServicoController.class)
                        .listar(null)).withRel("servicos")
        );

        return ResponseEntity.status(201).body(resultado);
    }

    @Operation(
            summary = "Atualizar serviço",
            description = "Atualiza os dados de um serviço existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Servico>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Servico servico) {

        Servico servicoExistente = service.buscarPorId(id);

        if (servicoExistente == null) {
            return ResponseEntity.notFound().build();
        }

        servicoExistente.setTipo(servico.getTipo());
        servicoExistente.setPreco(servico.getPreco());

        Servico atualizado = service.salvar(servicoExistente);

        EntityModel<Servico> resultado = EntityModel.of(
                atualizado,
                linkTo(methodOn(ServicoController.class)
                        .buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(ServicoController.class)
                        .listar(null)).withRel("servicos")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Excluir serviço",
            description = "Exclui um serviço cadastrado pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Serviço excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        Servico servico = service.buscarPorId(id);

        if (servico == null) {
            return ResponseEntity.notFound().build();
        }

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}