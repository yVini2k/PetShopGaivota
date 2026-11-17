package com.petshopgaivota.api.security;

import com.petshopgaivota.api.service.ApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Registra a chave fixa definida em API_KEY_FIXA a cada inicialização.
 * Assim ela continua válida mesmo que o banco seja recriado (ex.: novo deploy no Render).
 * A geração de chaves via POST /api/keys continua funcionando normalmente.
 */
@Component
public class ApiKeyInicializador implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyInicializador.class);
    private static final int TAMANHO_MINIMO = 32;

    private final ApiKeyService service;
    private final String chaveFixa;

    public ApiKeyInicializador(ApiKeyService service,
                               @Value("${app.api-key.fixa:}") String chaveFixa) {
        this.service = service;
        this.chaveFixa = chaveFixa == null ? "" : chaveFixa.trim();
    }

    @Override
    public void run(ApplicationArguments args) {
        if (chaveFixa.isEmpty()) {
            log.info("API_KEY_FIXA não definida: apenas chaves geradas via /api/keys serão aceitas.");
            return;
        }
        if (chaveFixa.length() < TAMANHO_MINIMO) {
            throw new IllegalStateException(
                    "API_KEY_FIXA deve ter pelo menos " + TAMANHO_MINIMO + " caracteres.");
        }
        service.garantirChaveFixa(chaveFixa, "chave-fixa");
        log.info("Chave fixa registrada (ADMIN).");
    }
}