package com.petshopgaivota.api.security;

import com.petshopgaivota.api.entity.ApiKey;
import com.petshopgaivota.api.enums.NivelAcesso;
import com.petshopgaivota.api.exception.ErroResponse;
import com.petshopgaivota.api.service.ApiKeyService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@Order(2)
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyService service;
    private final ObjectMapper mapper;

    public ApiKeyFilter(ApiKeyService service,
                        ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {

        String rota = req.getRequestURI();
        String metodo = req.getMethod();

        return !rota.startsWith("/api/")
                || metodo.equals("OPTIONS")
                || (rota.equals("/api/keys")
                && metodo.equals("POST"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain
    ) throws ServletException, IOException {

        String chave = req.getHeader("X-API-Key");

        if (chave == null || chave.isBlank()) {
            responderErro(res, HttpStatus.UNAUTHORIZED,
                    "Chave de API não informada.");
            return;
        }

        Optional<ApiKey> apiKey = service.validar(chave);

        if (apiKey.isEmpty()) {
            responderErro(res, HttpStatus.UNAUTHORIZED,
                    "Chave de API inválida ou revogada.");
            return;
        }

        String rota = req.getRequestURI();

        if ((rota.equals("/api/keys")
                || rota.startsWith("/api/keys/"))
                && apiKey.get().getNivel() != NivelAcesso.ADMIN) {

            responderErro(res, HttpStatus.FORBIDDEN,
                    "Acesso permitido apenas para administradores.");
            return;
        }

        chain.doFilter(req, res);
    }

    private void responderErro(
            HttpServletResponse res,
            HttpStatus status,
            String mensagem
    ) throws IOException {

        res.setStatus(status.value());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");

        mapper.writeValue(
                res.getOutputStream(),
                ErroResponse.of(status, mensagem, List.of())
        );
    }
}