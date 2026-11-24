package com.petshopgaivota.api.exception;

import jakarta.servlet.http.HttpServletRequest;
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

    // Violação de integridade: distingue referência inexistente, exclusão bloqueada e duplicidade.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        String estado = sqlState(ex);
        boolean chaveEstrangeira = "23503".equals(estado) || "23506".equals(estado);
        boolean exclusao = "DELETE".equals(request.getMethod());

        // 400 - Referência a um registro que não existe (cliente, pet, funcionário, serviço...)
        if (chaveEstrangeira && !exclusao) {
            return ResponseEntity.badRequest().body(
                    ErroResponse.of(
                            HttpStatus.BAD_REQUEST,
                            "Referência inválida",
                            List.of("Um dos registros informados (cliente, contato, pet, "
                                    + "funcionário ou serviço) não existe. Cadastre-o antes de usá-lo.")
                    )
            );
        }

        // 409 - Exclusão bloqueada porque o registro está em uso
        if (chaveEstrangeira) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    ErroResponse.of(
                            HttpStatus.CONFLICT,
                            "Registro em uso",
                            List.of("Este registro está vinculado a outros e não pode ser excluído.")
                    )
            );
        }

        // 409 - Duplicidade ou outro conflito de dados
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ErroResponse.of(
                        HttpStatus.CONFLICT,
                        "Conflito de dados ou requisição duplicada",
                        List.of()
                )
        );
    }

    // Procura o código SQLState na cadeia de causas da exceção.
    private static String sqlState(Throwable ex) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof java.sql.SQLException sql && sql.getSQLState() != null) {
                return sql.getSQLState();
            }
        }
        return null;
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