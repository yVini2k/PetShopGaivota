package com.petshopgaivota.api.controller;

import com.petshopgaivota.api.entity.Funcionario;
import com.petshopgaivota.api.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Funcionários",
        description = "Operações relacionadas aos funcionários do Pet Shop Gaivota"
)
@RestController
@RequestMapping("/api/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @Operation(
            summary = "Listar funcionários",
            description = "Retorna uma lista paginada de funcionários cadastrados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Funcionários encontrados com sucesso"
            ),
            @ApiResponse(responseCode = "400", description = "Status informado é inválido")
    })
    @GetMapping
    public ResponseEntity<Page<Funcionario>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @Operation(
            summary = "Buscar funcionários por nome",
            description = "Retorna os funcionários cujo nome contém o texto informado."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Busca realizada com sucesso"
            )
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Funcionario>> buscarPorNome(
            @RequestParam String nome) {

        return ResponseEntity.ok(service.buscarPorNome(nome));
    }

    @Operation(
            summary = "Buscar funcionário por ID",
            description = "Retorna os dados de um funcionário específico pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Funcionário encontrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable Long id) {

        Funcionario funcionario = service.buscarPorId(id);

        if (funcionario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(funcionario);
    }

    @Operation(
            summary = "Cadastrar funcionário",
            description = "Cadastra um novo funcionário no sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Funcionário cadastrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    @PostMapping
    public ResponseEntity<Funcionario> salvar(
            @Valid @RequestBody Funcionario funcionario) {

        Funcionario novoFuncionario = service.salvar(funcionario);

        return ResponseEntity.status(201).body(novoFuncionario);
    }

    @Operation(
            summary = "Atualizar funcionário",
            description = "Atualiza os dados de um funcionário existente."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Funcionário atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<Funcionario> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Funcionario funcionario) {

        Funcionario funcionarioExistente = service.buscarPorId(id);

        if (funcionarioExistente == null) {
            return ResponseEntity.notFound().build();
        }

        funcionarioExistente.setNome(funcionario.getNome());
        funcionarioExistente.setFuncao(funcionario.getFuncao());

        Funcionario atualizado = service.salvar(funcionarioExistente);

        return ResponseEntity.ok(atualizado);
    }

    @Operation(
            summary = "Excluir funcionário",
            description = "Exclui um funcionário cadastrado pelo seu ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Funcionário excluído com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Funcionário não encontrado"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        Funcionario funcionario = service.buscarPorId(id);

        if (funcionario == null) {
            return ResponseEntity.notFound().build();
        }

        service.excluir(id);

        return ResponseEntity.noContent().build();
    }
}