package com.petshopgaivota.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pet Shop Gaivota")
                        .version("2.0.0")

                        // Descrição geral
                        .description(
                                "O Pet Shop Gaivota é especializado em serviços de banho e tosa, " +
                                        "oferecendo cuidado, higiene e bem-estar para cães e outros animais de estimação.\n\n" +

                                        "Sistema desenvolvido para gerenciar clientes, pets, funcionários, " +
                                        "serviços e agendamentos do Pet Shop Gaivota.\n\n" +

                                        "Codificação: UTF-8. Idioma: Português do Brasil (pt-BR).\n\n" +

                                        "- Cliente: responsável pelo animal.\n" +
                                        "- Pet: animal cadastrado no sistema.\n" +
                                        "- Funcionário: profissional responsável pelo atendimento.\n" +
                                        "- Serviço: modalidade de banho e tosa oferecida pela loja.\n" +
                                        "- Agendamento: registro de um atendimento programado.\n\n" +

                                        "**Recursos avançados**\n\n" +
                                        "- Autenticação: operações de escrita exigem o header `X-API-Key`. " +
                                        "Gere uma chave em `POST /api/keys`.\n" +
                                        "- Idempotência: envie `X-Idempotency-Key` no POST de agendamentos " +
                                        "para evitar duplicidade.\n" +
                                        "- Versionamento: o endpoint de busca por ID aceita `X-API-Version` " +
                                        "com os valores 1 ou 2.\n" +
                                        "- Rate limit: excedido o limite, a API responde 429 com o header `Retry-After`.\n"
                        )

                        // Termos de serviço
                        .termsOfService("https://petshopgaivota.com.br/termos")

                        // Contato
                        .contact(new Contact()
                                .name("Suporte Pet Shop Gaivota")
                                .email("petshogaivota@gmail.com")
                        )

                        // Licença MIT
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                // Esquema de segurança usado pelo @SecurityRequirement(name = "apiKey")
                .components(new Components()
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")
                                .description("Chave de API gerada em POST /api/keys")
                        )
                );
    }

    // 401 e 429 vêm dos filtros, então o springdoc não os detecta sozinho
    @Bean
    public OpenApiCustomizer errosDeFiltros() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperationsMap().forEach((metodo, op) -> {
                    op.getResponses().addApiResponse("429",
                            new io.swagger.v3.oas.models.responses.ApiResponse()
                                    .description("Limite de requisições excedido (veja o header Retry-After)"));
                    if (metodo != PathItem.HttpMethod.GET) {
                        op.getResponses().addApiResponse("401",
                                new io.swagger.v3.oas.models.responses.ApiResponse()
                                        .description("API Key ausente ou inválida"));
                    }
                }));
    }

    @Bean
    public GroupedOpenApi grupoGeral() {
        return GroupedOpenApi.builder().group("geral")
                .pathsToMatch("/api/**").build();
    }

    @Bean
    public GroupedOpenApi grupoV1() {
        return GroupedOpenApi.builder().group("v1")
                .pathsToMatch("/api/**").headersToMatch("X-API-Version=1").build();
    }

    @Bean
    public GroupedOpenApi grupoV2() {
        return GroupedOpenApi.builder().group("v2")
                .pathsToMatch("/api/**").headersToMatch("X-API-Version=2").build();
    }
}