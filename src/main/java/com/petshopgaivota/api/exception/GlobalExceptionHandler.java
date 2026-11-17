
package com.petshopgaivota.api.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice(basePackages = "com.petshopgaivota.api.controller")
public class GlobalExceptionHandler {

    // 400 - Erros de Bean Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(
            MethodArgumentNotValidException ex) {

        List<String> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(
                ErroResponse.of(
                        HttpStatus.BAD_REQUEST,
                        "Dados inválidos",
                        detalhes
                )
        );
    }

    // 400 - Requisições inválidas
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class
    })
    public ResponseEntity<ErroResponse> requisicaoInvalida(Exception ex) {

        return ResponseEntity.badRequest().body(
                ErroResponse.of(
                        HttpStatus.BAD_REQUEST,
                        "Requisição inválida",
                        List.of("Verifique os parâmetros e o corpo da requisição.")
                )
        );
    }

    // 404 - Recurso não encontrado (sem JSON)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> recursoNaoEncontrado(
            NoResourceFoundException ex) {

        return ResponseEntity.notFound().build();
    }

    // Preserva os status HTTP definidos pela aplicação
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> erroComStatus(
            ResponseStatusException ex) {

        HttpStatus status = HttpStatus.resolve(
                ex.getStatusCode().value()
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        // 404 - Retorna apenas o status, sem JSON
        if (status == HttpStatus.NOT_FOUND) {
            return ResponseEntity.notFound().build();
        }

        String mensagem = ex.getReason() != null
                ? ex.getReason()
                : "Erro na requisição";

        return ResponseEntity.status(status).body(
                ErroResponse.of(
                        status,
                        mensagem,
                        List.of()
                )
        );
    }

    // 409 - Conflito de integridade no banco
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> conflito(
            DataIntegrityViolationException ex) {

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErroResponse.of(
                        HttpStatus.CONFLICT,
                        "Conflito de dados ou requisição duplicada",
                        List.of()
                )
        );
    }

    // 409 - Conflito de idempotência
    @ExceptionHandler(ConflitoIdempotenciaException.class)
    public ResponseEntity<ErroResponse> conflitoIdempotencia(
            ConflitoIdempotenciaException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErroResponse.of(
                        HttpStatus.CONFLICT,
                        "Conflito de idempotência",
                        List.of(ex.getMessage())
                )
        );
    }

    // 500 - Erros inesperados
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> generico(Exception ex) {

        ex.printStackTrace();

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
        ).body(
                ErroResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro interno do servidor",
                        List.of()
                )
        );
    }
}
