package com.petshopgaivota.api.controller;

import com.petshopgaivota.api.entity.Agendamento;
import com.petshopgaivota.api.enums.StatusAgendamento;
import com.petshopgaivota.api.service.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.CollectionModel;
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
        name = "Agendamentos",
        description = "Operações relacionadas aos agendamentos do Pet Shop Gaivota"
)
@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @Operation(
            summary = "Listar agendamentos",
            description = "Retorna uma lista paginada de agendamentos cadastrados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamentos encontrados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status informado é inválido")
    })

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Agendamento>>> listar(
            Pageable pageable) {

        Page<Agendamento> pagina = service.listar(pageable);

        List<EntityModel<Agendamento>> agendamentos = pagina.getContent()
                .stream()
                .map(agendamento -> EntityModel.of(
                        agendamento,
                        linkTo(methodOn(AgendamentoController.class)
                                .buscarPorId(agendamento.getId())).withSelfRel()
                ))
                .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pagina.getSize(),
                pagina.getNumber(),
                pagina.getTotalElements()
        );

        PagedModel<EntityModel<Agendamento>> resultado =
                PagedModel.of(agendamentos, metadata);

        resultado.add(
                linkTo(methodOn(AgendamentoController.class)
                        .listar(pageable)).withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar agendamentos por status",
            description = "Retorna os agendamentos de acordo com o status informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status informado é inválido")
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<Agendamento>>> buscarPorStatus(
            @RequestParam StatusAgendamento status) {

        List<EntityModel<Agendamento>> agendamentos =
                service.buscarPorStatus(status)
                        .stream()
                        .map(agendamento -> EntityModel.of(
                                agendamento,
                                linkTo(methodOn(AgendamentoController.class)
                                        .buscarPorId(agendamento.getId())).withSelfRel()
                        ))
                        .toList();

        CollectionModel<EntityModel<Agendamento>> resultado =
                CollectionModel.of(agendamentos);

        resultado.add(
                linkTo(methodOn(AgendamentoController.class)
                        .buscarPorStatus(status)).withSelfRel()
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar agendamento por ID",
            description = "Retorna os dados de um agendamento específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Agendamento>> buscarPorId(
            @PathVariable Long id) {

        Agendamento agendamento = service.buscarPorId(id);

        if (agendamento == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Agendamento> resultado = EntityModel.of(
                agendamento,
                linkTo(methodOn(AgendamentoController.class)
                        .buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(AgendamentoController.class)
                        .listar(null)).withRel("agendamentos")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Cadastrar agendamento",
            description = "Cadastra um novo agendamento no sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agendamento cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Agendamento>> salvar(
            @Valid @RequestBody Agendamento agendamento) {

        Agendamento novoAgendamento = service.salvar(agendamento);

        EntityModel<Agendamento> resultado = EntityModel.of(
                novoAgendamento,
                linkTo(methodOn(AgendamentoController.class)
                        .buscarPorId(novoAgendamento.getId())).withSelfRel(),
                linkTo(methodOn(AgendamentoController.class)
                        .listar(null)).withRel("agendamentos")
        );

        return ResponseEntity.status(201).body(resultado);
    }

    @Operation(
            summary = "Atualizar agendamento",
            description = "Atualiza os dados de um agendamento existente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Agendamento>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Agendamento agendamento) {

        Agendamento agendamentoExistente = service.buscarPorId(id);

        if (agendamentoExistente == null) {
            return ResponseEntity.notFound().build();
        }

        agendamentoExistente.setData(agendamento.getData());
        agendamentoExistente.setHorario(agendamento.getHorario());
        agendamentoExistente.setStatus(agendamento.getStatus());
        agendamentoExistente.setPet(agendamento.getPet());
        agendamentoExistente.setFuncionario(agendamento.getFuncionario());
        agendamentoExistente.setServico(agendamento.getServico());

        Agendamento atualizado = service.salvar(agendamentoExistente);

        EntityModel<Agendamento> resultado = EntityModel.of(
                atualizado,
                linkTo(methodOn(AgendamentoController.class)
                        .buscarPorId(id)).withSelfRel(),
                linkTo(methodOn(AgendamentoController.class)
                        .listar(null)).withRel("agendamentos")
        );

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Excluir agendamento",
            description = "Exclui um agendamento cadastrado pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agendamento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        Agendamento agendamento = service.buscarPorId(id);

        if (agendamento == null) {
            return ResponseEntity.notFound().build();
        }

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}