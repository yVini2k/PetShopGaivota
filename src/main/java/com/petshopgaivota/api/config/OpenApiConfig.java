
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
                        .description(
                                "O Pet Shop Gaivota é especializado em serviços de banho e tosa, "
                                        + "oferecendo cuidado, higiene e bem-estar para os animais de estimação.\n\n"

                                        + "Sistema desenvolvido para gerenciar clientes, contatos, pets, "
                                        + "funcionários, serviços e agendamentos do Pet Shop Gaivota.\n\n"

                                        + "**Informações gerais**\n\n"
                                        + "- Codificação: UTF-8.\n"
                                        + "- Idioma: Português do Brasil (pt-BR).\n"
                                        + "- Cliente: responsável pelo animal.\n"
                                        + "- Contato: telefone e endereço do cliente.\n"
                                        + "- Pet: animal cadastrado no sistema.\n"
                                        + "- Funcionário: profissional responsável pelo atendimento.\n"
                                        + "- Serviço: modalidade de banho e tosa.\n"
                                        + "- Agendamento: atendimento programado.\n\n"

                                        + "**Recursos avançados**\n\n"
                                        + "- **API Key:** operações de escrita exigem `X-API-Key`. "
                                        + "A geração de chaves em `POST /api/keys` é pública; "
                                        + "as demais operações de gerenciamento de chaves são protegidas.\n"
                                        + "- **Idempotência:** utilize `X-Idempotency-Key` "
                                        + "no cadastro de agendamentos para evitar duplicidade.\n"
                                        + "- **Versionamento:** a busca de agendamentos por ID "
                                        + "aceita `X-API-Version` com os valores 1 e 2.\n"
                                        + "- **Rate Limiting:** requisições acima do limite "
                                        + "recebem HTTP 429 e o cabeçalho `Retry-After`.\n"
                                        + "- **Validação:** dados inválidos recebem HTTP 400 "
                                        + "com mensagens explicativas.\n"
                                        + "- **HATEOAS:** os recursos disponibilizam links "
                                        + "para navegação entre endpoints."
                        )
                        .contact(new Contact()
                                .name("Suporte Pet Shop Gaivota")
                                .email("petshogaivota@gmail.com")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                )
                .components(new Components()
                        .addSecuritySchemes("apiKey",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-Key")
                                        .description(
                                                "Informe a chave original gerada "
                                                        + "em POST /api/keys."
                                        )
                        )
                );
    }

    // Documenta as respostas geradas pelos filtros de segurança.
    @Bean
    public OpenApiCustomizer errosDeFiltros() {
        return openApi -> openApi.getPaths().forEach(
                (caminho, pathItem) ->
                        pathItem.readOperationsMap().forEach(
                                (metodo, operacao) -> {

                                    // Resposta de Rate Limiting.
                                    operacao.getResponses().addApiResponse(
                                            "429",
                                            new io.swagger.v3.oas.models.responses.ApiResponse()
                                                    .description(
                                                            "Limite de requisições excedido. "
                                                                    + "Consulte o cabeçalho Retry-After."
                                                    )
                                    );

                                    // GET público, exceto gerenciamento de chaves.
                                    boolean leituraPublica =
                                            metodo == PathItem.HttpMethod.GET
                                                    && !caminho.startsWith("/api/keys");

                                    // Geração de API Key não exige autenticação.
                                    boolean geracaoPublica =
                                            metodo == PathItem.HttpMethod.POST
                                                    && caminho.equals("/api/keys");

                                    boolean operacaoProtegida =
                                            !leituraPublica
                                                    && !geracaoPublica
                                                    && metodo != PathItem.HttpMethod.OPTIONS;

                                    if (operacaoProtegida) {

                                        // Documenta a autenticação no Swagger.
                                        operacao.addSecurityItem(
                                                new io.swagger.v3.oas.models.security.SecurityRequirement()
                                                        .addList("apiKey")
                                        );

                                        // Resposta para chave ausente ou inválida.
                                        operacao.getResponses().addApiResponse(
                                                "401",
                                                new io.swagger.v3.oas.models.responses.ApiResponse()
                                                        .description(
                                                                "API Key ausente, inválida ou revogada."
                                                        )
                                        );
                                    }
                                }
                        )
        );
    }

    // Documentação geral da API.
    @Bean
    public GroupedOpenApi grupoGeral() {
        return GroupedOpenApi.builder()
                .group("geral")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(errosDeFiltros())
                .build();
    }

    // Documentação dos endpoints da versão 1.
    @Bean
    public GroupedOpenApi grupoV1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/**")
                .headersToMatch("X-API-Version=1")
                .addOpenApiCustomizer(errosDeFiltros())
                .build();
    }

    // Documentação dos endpoints da versão 2.
    @Bean
    public GroupedOpenApi grupoV2() {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/api/**")
                .headersToMatch("X-API-Version=2")
                .addOpenApiCustomizer(errosDeFiltros())
                .build();
    }
}
