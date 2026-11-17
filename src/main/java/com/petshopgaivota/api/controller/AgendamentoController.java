package com.petshopgaivota.api.controller;

import com.petshopgaivota.api.dto.AgendamentoV2Response;
import com.petshopgaivota.api.entity.Agendamento;
import com.petshopgaivota.api.enums.StatusAgendamento;
import com.petshopgaivota.api.exception.ErroResponse;
import com.petshopgaivota.api.service.AgendamentoIdempotenteService;
import com.petshopgaivota.api.service.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final AgendamentoIdempotenteService idempotenteService;

    public AgendamentoController(AgendamentoService service,
                                 AgendamentoIdempotenteService idempotenteService) {
        this.service = service;
        this.idempotenteService = idempotenteService;
    }

    private EntityModel<Agendamento> toModel(Agendamento a) {
        return EntityModel.of(
                a,
                linkTo(methodOn(AgendamentoController.class).buscarPorIdV1(a.getId())).withSelfRel(),
                linkTo(methodOn(AgendamentoController.class).listar(null)).withRel("agendamentos")
        );
    }

    @Operation(
            summary = "Listar agendamentos",
            description = "Retorna uma lista paginada de agendamentos cadastrados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamentos encontrados com sucesso")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Agendamento>>> listar(Pageable pageable) {

        Page<Agendamento> pagina = service.listar(pageable);

        List<EntityModel<Agendamento>> agendamentos = pagina.getContent()
                .stream()
                .map(agendamento -> EntityModel.of(
                        agendamento,
                        linkTo(methodOn(AgendamentoController.class)
                                .buscarPorIdV1(agendamento.getId())).withSelfRel()
                ))
                .toList();

        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(
                pagina.getSize(),
                pagina.getNumber(),
                pagina.getTotalElements()
        );

        PagedModel<EntityModel<Agendamento>> resultado = PagedModel.of(agendamentos, metadata);

        resultado.add(linkTo(methodOn(AgendamentoController.class).listar(pageable)).withSelfRel());

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar agendamentos por status",
            description = "Retorna os agendamentos de acordo com o status informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status informado é inválido",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<Agendamento>>> buscarPorStatus(
            @RequestParam StatusAgendamento status) {

        List<EntityModel<Agendamento>> agendamentos = service.buscarPorStatus(status)
                .stream()
                .map(agendamento -> EntityModel.of(
                        agendamento,
                        linkTo(methodOn(AgendamentoController.class)
                                .buscarPorIdV1(agendamento.getId())).withSelfRel()
                ))
                .toList();

        CollectionModel<EntityModel<Agendamento>> resultado = CollectionModel.of(agendamentos);

        resultado.add(linkTo(methodOn(AgendamentoController.class)
                .buscarPorStatus(status)).withSelfRel());

        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Buscar agendamento por ID (V1)",
            description = "Retorna o agendamento completo. Envie o header X-API-Version: 1."
    )

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content)
    })
    @GetMapping(value = "/{id}", headers = "X-API-Version=1")
    public ResponseEntity<EntityModel<Agendamento>> buscarPorIdV1(@PathVariable Long id) {

        Agendamento agendamento = service.buscarPorId(id);

        if (agendamento == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(toModel(agendamento));
    }

    @Operation(
            summary = "Buscar agendamento por ID (V2)",
            description = "Retorna uma versão resumida, com os nomes do pet e do cliente. "
                    + "Envie o header X-API-Version: 2."
    )

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content)
    })
    @GetMapping(value = "/{id}", headers = "X-API-Version=2")
    public ResponseEntity<AgendamentoV2Response> buscarPorIdV2(@PathVariable Long id) {

        Agendamento a = service.buscarPorId(id);

        if (a == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new AgendamentoV2Response(
                a.getId(),
                a.getData(),
                a.getHorario(),
                a.getStatus(),
                a.getPet().getNome(),
                a.getPet().getCliente().getNome()
        ));
    }

    @Operation(
            summary = "Cadastrar agendamento",
            description = "Cadastra um novo agendamento. Envie X-Idempotency-Key para evitar duplicidade."
    )
    @SecurityRequirement(name = "apiKey")
    @Parameter(in = ParameterIn.HEADER, name = "X-Idempotency-Key", required = false,
            description = "Chave única da operação (ex.: um UUID)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agendamento cadastrado com sucesso"),
            @ApiResponse(responseCode = "200",
                    description = "Requisição repetida: retorna o agendamento já criado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflito de idempotência",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EntityModel<Agendamento>> salvar(
            @RequestHeader(value = "X-Idempotency-Key", required = false) String chave,
            @Valid @RequestBody Agendamento agendamento) {

        AgendamentoIdempotenteService.Resultado r = idempotenteService.salvar(chave, agendamento);
        EntityModel<Agendamento> modelo = toModel(r.agendamento());

        if (r.repetido()) {
            return ResponseEntity.ok()
                    .header("Idempotent-Replayed", "true")
                    .body(modelo);
        }

        return ResponseEntity.status(201).body(modelo);
    }

    @Operation(
            summary = "Atualizar agendamento",
            description = "Atualiza os dados de um agendamento existente."
    )
    @SecurityRequirement(name = "apiKey")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agendamento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content)
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

        return ResponseEntity.ok(toModel(atualizado));
    }

    @Operation(
            summary = "Excluir agendamento",
            description = "Exclui um agendamento cadastrado pelo seu ID."
    )
    @SecurityRequirement(name = "apiKey")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agendamento excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content)
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