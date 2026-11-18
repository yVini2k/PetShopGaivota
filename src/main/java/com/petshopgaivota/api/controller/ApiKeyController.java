package com.petshopgaivota.api.controller;

import com.petshopgaivota.api.enums.NivelAcesso;
import com.petshopgaivota.api.exception.ErroResponse;
import com.petshopgaivota.api.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Tag(name = "API Keys", description = "Geração e gerenciamento de chaves de API")
@RestController
@RequestMapping("/api/keys")
public class ApiKeyController {

    private final ApiKeyService service;

    public ApiKeyController(ApiKeyService service) {
        this.service = service;
    }

    public record NovaChaveRequest(@NotBlank(message = "O proprietário é obrigatório")
                                   String proprietario) {}

    public record NovaChaveResponse(String chave, String aviso) {}

    public record ChaveResumo(Long id, String proprietario, boolean ativa, Instant criadaEm) {}

    @Operation(summary = "Gerar chave de API",
            description = "Gera uma nova chave. Ela é exibida apenas nesta resposta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chave gerada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    // Esqueleto do corpo da requisição (Swagger): substitua os valores depois
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Esqueleto",
                            value = """
                            {
                              "proprietario": "Nome do proprietario"
                            }
                            """
                    )
            )
    )
    @PostMapping
    public ResponseEntity<NovaChaveResponse> gerar(@Valid @RequestBody NovaChaveRequest req) {
        String chave = service.gerar(req.proprietario(), NivelAcesso.USER);
        return ResponseEntity.status(201).body(new NovaChaveResponse(chave,
                "Guarde esta chave: ela não será exibida novamente."));
    }

    @Operation(summary = "Listar chaves", description = "Lista as chaves cadastradas (sem o valor da chave).")
    @SecurityRequirement(name = "apiKey")
    @GetMapping
    public ResponseEntity<List<ChaveResumo>> listar() {
        List<ChaveResumo> lista = service.listar().stream()
                .map(k -> new ChaveResumo(k.getId(), k.getProprietario(), k.isAtiva(), k.getCriadaEm()))
                .toList();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Revogar chave", description = "Desativa uma chave de API pelo ID.")
    @SecurityRequirement(name = "apiKey")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Chave revogada"),
            @ApiResponse(responseCode = "404", description = "Chave não encontrada", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revogar(@PathVariable Long id) {
        if (!service.revogar(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}