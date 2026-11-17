package com.petshopgaivota.api.security;

import com.petshopgaivota.api.exception.ErroResponse;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LIMITE = 60;           // requisições por janela
    private static final long JANELA_MS = 60_000;   // 1 minuto

    private record Janela(long inicio, AtomicInteger contagem) {}

    private final ConcurrentHashMap<String, Janela> janelas = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;

    public RateLimitFilter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        return !req.getRequestURI().startsWith("/api/") || req.getMethod().equals("OPTIONS");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String cliente = Optional.ofNullable(req.getHeader("X-API-Key"))
                .orElse(req.getRemoteAddr());
        long agora = System.currentTimeMillis();

        Janela j = janelas.compute(cliente, (k, atual) ->
                (atual == null || agora - atual.inicio() >= JANELA_MS)
                        ? new Janela(agora, new AtomicInteger(0))
                        : atual);

        int usadas = j.contagem().incrementAndGet();
        res.setHeader("X-RateLimit-Limit", String.valueOf(LIMITE));
        res.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, LIMITE - usadas)));

        if (usadas > LIMITE) {
            long segundos = Math.max(1, (j.inicio() + JANELA_MS - agora) / 1000);
            res.setHeader("Retry-After", String.valueOf(segundos));
            res.setStatus(429);
            res.setContentType("application/json");
            mapper.writeValue(res.getOutputStream(), ErroResponse.of(
                    HttpStatus.TOO_MANY_REQUESTS, "Limite de requisições excedido", List.of()));
            return;
        }
        chain.doFilter(req, res);
    }
}