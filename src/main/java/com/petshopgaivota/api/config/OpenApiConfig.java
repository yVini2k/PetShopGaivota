package com.petshopgaivota.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    // Cria um objeto JSON (mantendo a ordem dos campos) a partir de pares chave, valor.
    private static Map<String, Object> obj(Object... pares) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        for (int i = 0; i < pares.length; i += 2) {
            mapa.put((String) pares[i], pares[i + 1]);
        }
        return mapa;
    }

    // Corpo de exemplo de cada POST (aparece pré-preenchido no Swagger).
    private static final Map<String, Object> EXEMPLOS_POST = Map.of(
            "/api/keys",
            obj("proprietario", "Maria - Site do Pet Shop"),

            "/api/contatos",
            obj("telefone", "11977776666",
                    "endereco", "Rua das Gaivotas, 123 - Centro"),

            "/api/clientes",
            obj("nome", "Carlos Silva",
                    "email", "carlos@email.com",
                    "contato", obj("telefone", "11977776666",
                            "endereco", "Rua das Gaivotas, 123 - Centro")),

            "/api/pets",
            obj("nome", "Rex",
                    "tamanho", "GRANDE",
                    "cliente", obj("id", 1),
                    "servicos", List.of(obj("id", 1))),

            "/api/funcionarios",
            obj("nome", "João Silva",
                    "funcao", "BANHISTA"),

            "/api/servicos",
            obj("tipo", "BANHO",
                    "preco", 50.0),

            "/api/agendamentos",
            obj("data", "2026-12-10",
                    "horario", "14:00:00",
                    "status", "AGENDADO",
                    "pet", obj("id", 1),
                    "funcionario", obj("id", 1),
                    "servico", obj("id", 1))
    );

    // Regras de preenchimento de cada POST (acrescentadas à descrição da operação).
    private static final Map<String, String> NOTAS_POST = Map.of(
            "/api/keys",
            "**Como usar:** não exige `X-API-Key`. Informe o `proprietario` (obrigatório). "
                    + "A chave retornada é exibida apenas uma vez; depois, envie-a no header `X-API-Key`.",

            "/api/contatos",
            "**Como usar:** exige `X-API-Key`. `telefone`: 10 ou 11 dígitos, somente números. "
                    + "`endereco`: de 5 a 200 caracteres. Normalmente o contato é criado junto com o "
                    + "cliente em `POST /api/clientes`; este endpoint faz o cadastro avulso de um contato.",

            "/api/clientes",
            "**Como usar:** exige `X-API-Key`. `nome`: 3 a 100 caracteres. `email`: formato válido. "
                    + "`contato`: obrigatório; informe `telefone` (10 ou 11 dígitos) e `endereco` "
                    + "(5 a 200 caracteres). O contato é criado junto com o cliente; não envie `id`.",

            "/api/pets",
            "**Como usar:** exige `X-API-Key`. `nome`: 2 a 50 caracteres. "
                    + "`tamanho`: `PEQUENO`, `MEDIO` ou `GRANDE`. "
                    + "`cliente`: informe apenas o `id` de um cliente existente. "
                    + "`servicos`: opcional; lista com o `id` de serviços existentes.",

            "/api/funcionarios",
            "**Como usar:** exige `X-API-Key`. `nome`: 3 a 100 caracteres. "
                    + "`funcao`: `BANHISTA` ou `TOSADOR`.",

            "/api/servicos",
            "**Como usar:** exige `X-API-Key`. `tipo`: `BANHO` ou `BANHO_E_TOSA`. "
                    + "`preco`: número obrigatório (ex.: 50.0).",

            "/api/agendamentos",
            "**Como usar:** exige `X-API-Key`. `data`: formato `AAAA-MM-DD`. "
                    + "`horario`: formato `HH:mm:ss`. `status`: `AGENDADO`, `REALIZADO` ou `CANCELADO`. "
                    + "`pet`, `funcionario` e `servico`: informe apenas o `id` de registros existentes. "
                    + "Envie `X-Idempotency-Key` (ex.: um UUID) para evitar agendamentos duplicados em reenvios."
    );

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

                                        + "**Ordem recomendada de cadastro**\n\n"
                                        + "1. `POST /api/keys` — gerar a chave de API (ou usar a chave fixa do servidor).\n"
                                        + "2. `POST /api/clientes` — cadastrar o cliente (o contato é criado junto).\n"
                                        + "3. `POST /api/servicos` e `POST /api/funcionarios` — cadastrar serviços e funcionários.\n"
                                        + "4. `POST /api/pets` — cadastrar o pet, vinculando o cliente.\n"
                                        + "5. `POST /api/agendamentos` — agendar, vinculando pet, funcionário e serviço.\n\n"

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
                                                "Informe a chave original gerada em POST /api/keys "
                                                        + "ou a chave fixa configurada no servidor."
                                        )
                        )
                );
    }

    // Acrescenta o exemplo de corpo e as regras de preenchimento a um POST.
    private static void documentarPost(String caminho, Operation operacao) {
        String nota = NOTAS_POST.get(caminho);
        if (nota != null) {
            String atual = operacao.getDescription() == null ? "" : operacao.getDescription();
            // Só acrescenta uma vez (o customizer pode ser aplicado mais de uma vez).
            if (!atual.contains(nota)) {
                operacao.setDescription(atual.isBlank() ? nota : atual + "\n\n" + nota);
            }
        }

        Object exemplo = EXEMPLOS_POST.get(caminho);
        if (exemplo != null
                && operacao.getRequestBody() != null
                && operacao.getRequestBody().getContent() != null) {
            operacao.getRequestBody().getContent()
                    .values()
                    .forEach(tipo -> tipo.setExample(exemplo));
        }
    }

    // Documenta as respostas geradas pelos filtros de segurança.
    @Bean
    public OpenApiCustomizer errosDeFiltros() {
        return openApi -> openApi.getPaths().forEach(
                (caminho, pathItem) ->
                        pathItem.readOperationsMap().forEach(
                                (metodo, operacao) -> {

                                    // Exemplo de corpo e regras de cada POST.
                                    if (metodo == PathItem.HttpMethod.POST) {
                                        documentarPost(caminho, operacao);
                                    }

                                    // Resposta de Rate Limiting.
                                    operacao.getResponses().addApiResponse("429",
                                            new io.swagger.v3.oas.models.responses.ApiResponse()
                                                    .description("Limite de requisições excedido. Veja o header Retry-After."));
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

    // Esconde, no grupo, o endpoint que pertence a outra versão.
    private static OperationCustomizer ocultar(String... metodos) {
        List<String> ocultos = List.of(metodos);
        return (operation, handlerMethod) ->
                ocultos.contains(handlerMethod.getMethod().getName()) ? null : operation;
    }

    // Versão 1: API completa (padrão).
    @Bean
    public GroupedOpenApi grupoV1() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/**")
                .addOperationCustomizer(ocultar("buscarPorIdV2"))
                .addOpenApiCustomizer(errosDeFiltros())
                .build();
    }

    // Versão 2: API completa, com a busca por ID em formato V2.
    @Bean
    public GroupedOpenApi grupoV2() {
        return GroupedOpenApi.builder()
                .group("v2")
                .pathsToMatch("/api/**")
                .addOperationCustomizer(ocultar("buscarPorIdV1"))
                .addOpenApiCustomizer(errosDeFiltros())
                .build();
    }
}