package com.petshopgaivota.api.security;

import com.petshopgaivota.api.entity.ApiKey;
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

    public ApiKeyFilter(ApiKeyService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String path = req.getRequestURI();
        String metodo = req.getMethod();
        boolean ehChaves = path.startsWith("/api/keys");

        return !path.startsWith("/api/")                               // swagger, h2 etc.
                || metodo.equals("OPTIONS")                            // preflight do CORS
                || (metodo.equals("GET") && !ehChaves)                 // leitura pública
                || (path.equals("/api/keys") && metodo.equals("POST")); // gerar chave
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String chave = req.getHeader("X-API-Key");
        Optional<ApiKey> apiKey = (chave == null || chave.isBlank())
                ? Optional.empty()
                : service.validar(chave);

        if (apiKey.isEmpty()) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            mapper.writeValue(res.getOutputStream(), ErroResponse.of(
                    HttpStatus.UNAUTHORIZED, "API Key ausente ou inválida", List.of()));
            return;
        }

        req.setAttribute("apiKeyId", apiKey.get().getId());
        req.setAttribute("apiKeyNivel", apiKey.get().getNivel());
        chain.doFilter(req, res);
    }
}