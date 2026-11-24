
package com.petshopgaivota.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.responses.ApiResponse;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    // ==================================================
    // CRIAÇÃO DOS OBJETOS JSON
    // ==================================================

    private static Map<String, Object> obj(Object... pares) {

        Map<String, Object> mapa = new LinkedHashMap<>();

        for (int i = 0; i < pares.length; i += 2) {
            mapa.put((String) pares[i], pares[i + 1]);
        }

        return mapa;
    }

    // ==================================================
    // ESQUELETOS JSON DA V1
    // ==================================================

    private static final Map<String, Object> EXEMPLOS_POST = Map.of(

            "/api/keys",
            obj(
                    "proprietario", "Maria - Site do Pet Shop"
            ),

            "/api/contatos",
            obj(
                    "telefone", "11977776666",
                    "endereco", "Rua das Gaivotas, 123 - Centro"
            ),

            "/api/clientes",
            obj(
                    "nome", "Carlos Silva",
                    "email", "carlos@email.com",
                    "contato", obj(
                            "telefone", "11977776666",
                            "endereco", "Rua das Gaivotas, 123 - Centro"
                    )
            ),

            "/api/pets",
            obj(
                    "nome", "Rex",
                    "tamanho", "GRANDE",
                    "cliente", obj("id", 1),
                    "servicos", List.of(obj("id", 1))
            ),

            "/api/funcionarios",
            obj(
                    "nome", "João Silva",
                    "funcao", "BANHISTA"
            ),

            "/api/servicos",
            obj(
                    "tipo", "BANHO",
                    "preco", 50.0
            ),

            "/api/agendamentos",
            obj(
                    "data", "2026-12-10",
                    "horario", "14:00:00",
                    "status", "AGENDADO",
                    "pet", obj("id", 1),
                    "funcionario", obj("id", 1),
                    "servico", obj("id", 1)
            )
    );

    // ==================================================
    // ESQUELETO EXCLUSIVO DA V2
    // ==================================================

    private static final Map<String, Object> EXEMPLO_CLIENTE_V2 =
            obj(
                    "nome", "Carlos Silva",
                    "email", "carlos@email.com",
                    "telefone", "11977776666",
                    "endereco", "Rua das Gaivotas, 123 - Centro"
            );

    // ==================================================
    // REGRAS DE PREENCHIMENTO DA V1
    // ==================================================

    private static final Map<String, String> NOTAS_POST = Map.of(

            "/api/keys",
            "**Como usar:** não exige `X-API-Key`. "
                    + "Informe o `proprietario` (obrigatório). "
                    + "A chave retornada é exibida apenas uma vez; "
                    + "depois, envie-a no header `X-API-Key`.",

            "/api/contatos",
            "**Como usar:** exige `X-API-Key`. "
                    + "`telefone`: 10 ou 11 dígitos, somente números. "
                    + "`endereco`: de 5 a 200 caracteres. "
                    + "Normalmente o contato é criado junto com o cliente. "
                    + "Este endpoint permite o cadastro avulso.",

            "/api/clientes",
            "**Como usar:** exige `X-API-Key`. "
                    + "`nome`: 3 a 100 caracteres. "
                    + "`email`: formato válido. "
                    + "`contato`: obrigatório; informe `telefone` "
                    + "(10 ou 11 dígitos) e `endereco` "
                    + "(5 a 200 caracteres). "
                    + "O contato é criado junto com o cliente. "
                    + "Não envie `id`.",

            "/api/pets",
            "**Como usar:** exige `X-API-Key`. "
                    + "`nome`: 2 a 50 caracteres. "
                    + "`tamanho`: `PEQUENO`, `MEDIO` ou `GRANDE`. "
                    + "`cliente`: informe apenas o `id` de um cliente existente. "
                    + "`servicos`: opcional; lista com o `id` de serviços existentes.",

            "/api/funcionarios",
            "**Como usar:** exige `X-API-Key`. "
                    + "`nome`: 3 a 100 caracteres. "
                    + "`funcao`: `BANHISTA` ou `TOSADOR`.",

            "/api/servicos",
            "**Como usar:** exige `X-API-Key`. "
                    + "`tipo`: `BANHO` ou `BANHO_E_TOSA`. "
                    + "`preco`: número obrigatório (ex.: 50.0).",

            "/api/agendamentos",
            "**Como usar:** exige `X-API-Key`. "
                    + "`data`: formato `AAAA-MM-DD`. "
                    + "`horario`: formato `HH:mm:ss`. "
                    + "`status`: `AGENDADO`, `REALIZADO` ou `CANCELADO`. "
                    + "`pet`, `funcionario` e `servico`: informe apenas "
                    + "o `id` de registros existentes. "
                    + "Envie `X-Idempotency-Key` para evitar duplicidade."
    );

    // ==================================================
    // REGRAS DE PREENCHIMENTO DA V2
    // ==================================================

    private static final String NOTA_CLIENTE_V2 =
            "**Como usar:** exige `X-API-Key`.\n\n"
                    + "- `X-API-Version`: 2.\n"
                    + "- `nome`: 3 a 100 caracteres.\n"
                    + "- `email`: formato válido.\n"
                    + "- `telefone`: 10 ou 11 dígitos.\n"
                    + "- `endereco`: 5 a 200 caracteres.\n\n"
                    + "Na V2, telefone e endereço são informados "
                    + "diretamente no JSON, sem o objeto contato.";

    // ==================================================
    // INFORMAÇÕES GERAIS DA API
    // ==================================================

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()

                .info(new Info()

                        .title("Pet Shop Gaivota")

                        .version("2.0.0")

                        .description(

                                "O Pet Shop Gaivota é especializado "
                                        + "em serviços de banho e tosa, "
                                        + "oferecendo cuidado, higiene e "
                                        + "bem-estar para os animais de estimação.\n\n"

                                        + "Sistema desenvolvido para gerenciar "
                                        + "clientes, contatos, pets, funcionários, "
                                        + "serviços e agendamentos.\n\n"

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

                                        + "1. `POST /api/keys` — gerar a chave de API "
                                        + "ou utilizar a chave fixa do servidor.\n"

                                        + "2. `POST /api/clientes` — cadastrar o cliente "
                                        + "junto com seu contato.\n"

                                        + "3. `POST /api/servicos` e "
                                        + "`POST /api/funcionarios` — cadastrar "
                                        + "serviços e funcionários.\n"

                                        + "4. `POST /api/pets` — cadastrar o pet "
                                        + "vinculado ao cliente.\n"

                                        + "5. `POST /api/agendamentos` — cadastrar "
                                        + "o agendamento.\n\n"

                                        + "**Versionamento da API**\n\n"

                                        + "- **V1:** disponibiliza todos os recursos "
                                        + "da API com a estrutura original.\n"

                                        + "- **V2:** disponibiliza exclusivamente "
                                        + "o CRUD de clientes com DTOs simplificados.\n"

                                        + "- Utilize `X-API-Version: 1` "
                                        + "para acessar clientes na V1.\n"

                                        + "- Utilize `X-API-Version: 2` "
                                        + "para acessar clientes na V2.\n"

                                        + "- As duas versões compartilham "
                                        + "o mesmo banco de dados.\n\n"

                                        + "**Recursos avançados**\n\n"

                                        + "- **API Key:** operações de escrita "
                                        + "exigem `X-API-Key`. "
                                        + "A geração de chaves é pública.\n"

                                        + "- **Idempotência:** utilize "
                                        + "`X-Idempotency-Key` no cadastro "
                                        + "de agendamentos para evitar duplicidade.\n"

                                        + "- **Rate Limiting:** requisições acima "
                                        + "do limite recebem HTTP 429 e o "
                                        + "cabeçalho `Retry-After`.\n"

                                        + "- **Validação:** dados inválidos "
                                        + "recebem HTTP 400 com mensagens explicativas.\n"

                                        + "- **HATEOAS:** os recursos da V1 "
                                        + "disponibilizam links para navegação "
                                        + "entre endpoints."
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

                        .addSecuritySchemes(
                                "apiKey",

                                new SecurityScheme()

                                        .type(SecurityScheme.Type.APIKEY)

                                        .in(SecurityScheme.In.HEADER)

                                        .name("X-API-Key")

                                        .description(
                                                "Informe a chave original gerada "
                                                        + "em POST /api/keys ou a chave "
                                                        + "fixa configurada no servidor."
                                        )
                        )
                );
    }

    // ==================================================
    // DOCUMENTAÇÃO DOS ESQUELETOS JSON
    // ==================================================

    private static void documentarPost(
            String caminho,
            Operation operacao,
            boolean versao2) {

        String nota = NOTAS_POST.get(caminho);

        Object exemplo = EXEMPLOS_POST.get(caminho);

        // Utiliza o esqueleto simplificado na V2.
        if (versao2 && caminho.equals("/api/clientes")) {

            exemplo = EXEMPLO_CLIENTE_V2;

            nota = NOTA_CLIENTE_V2;
        }

        // Acrescenta as instruções de preenchimento.
        if (nota != null) {

            String atual = operacao.getDescription() == null
                    ? ""
                    : operacao.getDescription();

            if (!atual.contains(nota)) {

                operacao.setDescription(
                        atual.isBlank()
                                ? nota
                                : atual + "\n\n" + nota
                );
            }
        }

        // Define o esqueleto JSON correto.
        if (exemplo != null
                && operacao.getRequestBody() != null
                && operacao.getRequestBody().getContent() != null) {

            Object exemploFinal = exemplo;

            operacao.getRequestBody()
                    .getContent()
                    .values()
                    .forEach(tipo -> {

                        // Remove exemplos antigos.
                        tipo.setExamples(null);

                        // Aplica o exemplo da versão selecionada.
                        tipo.setExample(exemploFinal);
                    });
        }
    }

    // ==================================================
    // HEADER DE VERSIONAMENTO
    // ==================================================

    private static void documentarVersao(
            Operation operacao,
            String versao) {

        // Remove possíveis headers antigos.
        if (operacao.getParameters() != null) {

            operacao.getParameters().removeIf(
                    parametro -> "X-API-Version"
                            .equalsIgnoreCase(parametro.getName())
            );
        }

        // Adiciona o header correto para cada versão.
        operacao.addParametersItem(

                new HeaderParameter()

                        .name("X-API-Version")

                        .required(true)

                        .description(
                                "Versão da API utilizada nesta operação."
                        )

                        .schema(

                                new StringSchema()

                                        ._default(versao)

                                        ._enum(List.of(versao))
                        )
        );
    }

    // ==================================================
    // DOCUMENTAÇÃO DE SEGURANÇA E ERROS
    // ==================================================

    private OpenApiCustomizer errosDeFiltros(boolean versao2) {

        return openApi -> {

            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach(

                    (caminho, pathItem) ->

                            pathItem.readOperationsMap().forEach(

                                    (metodo, operacao) -> {

                                        boolean endpointCliente =
                                                caminho.equals("/api/clientes")
                                                        || caminho.startsWith(
                                                        "/api/clientes/"
                                                );

                                        // Header de versionamento dos clientes.
                                        if (endpointCliente) {

                                            documentarVersao(
                                                    operacao,
                                                    versao2 ? "2" : "1"
                                            );
                                        }

                                        // Esqueleto de cadastro.
                                        if (metodo == PathItem.HttpMethod.POST) {

                                            documentarPost(
                                                    caminho,
                                                    operacao,
                                                    versao2
                                            );
                                        }

                                        // Esqueleto de atualização de clientes.
                                        if (metodo == PathItem.HttpMethod.PUT
                                                && caminho.equals(
                                                "/api/clientes/{id}"
                                        )) {

                                            documentarPost(
                                                    "/api/clientes",
                                                    operacao,
                                                    versao2
                                            );
                                        }

                                        // Dados inválidos.
                                        if (metodo == PathItem.HttpMethod.POST
                                                || metodo == PathItem.HttpMethod.PUT) {

                                            operacao.getResponses().putIfAbsent(

                                                    "400",

                                                    new ApiResponse()
                                                            .description(
                                                                    "Dados inválidos."
                                                            )
                                            );
                                        }

                                        // Rate Limiting.
                                        operacao.getResponses().addApiResponse(

                                                "429",

                                                new ApiResponse()
                                                        .description(
                                                                "Limite de requisições "
                                                                        + "excedido. Veja o "
                                                                        + "header Retry-After."
                                                        )
                                        );

                                        // GET público, exceto chaves.
                                        boolean leituraPublica =
                                                metodo == PathItem.HttpMethod.GET
                                                        && !caminho.startsWith(
                                                        "/api/keys"
                                                );

                                        // Geração pública de API Key.
                                        boolean geracaoPublica =
                                                metodo == PathItem.HttpMethod.POST
                                                        && caminho.equals(
                                                        "/api/keys"
                                                );

                                        // Operações protegidas.
                                        boolean operacaoProtegida =
                                                !leituraPublica
                                                        && !geracaoPublica
                                                        && metodo !=
                                                        PathItem.HttpMethod.OPTIONS;

                                        if (operacaoProtegida) {

                                            boolean possuiApiKey =
                                                    operacao.getSecurity() != null
                                                            && operacao.getSecurity()
                                                            .stream()
                                                            .anyMatch(
                                                                    requisito ->
                                                                            requisito.containsKey(
                                                                                    "apiKey"
                                                                            )
                                                            );

                                            if (!possuiApiKey) {

                                                operacao.addSecurityItem(

                                                        new SecurityRequirement()
                                                                .addList("apiKey")
                                                );
                                            }

                                            // API Key inválida.
                                            operacao.getResponses().addApiResponse(

                                                    "401",

                                                    new ApiResponse()
                                                            .description(
                                                                    "API Key ausente, "
                                                                            + "inválida ou revogada."
                                                            )
                                            );
                                        }
                                    }
                            )
            );
        };
    }

    // ==================================================
    // GRUPO V1 - API COMPLETA
    // ==================================================

    @Bean
    public GroupedOpenApi grupoV1() {

        return GroupedOpenApi.builder()

                .group("v1")

                .pathsToMatch("/api/**")

                .addOpenApiMethodFilter(

                        method -> !method.getDeclaringClass()
                                .getSimpleName()
                                .equals("ClienteV2Controller")
                )

                .addOpenApiCustomizer(errosDeFiltros(false))

                .build();
    }

    // ==================================================
    // GRUPO V2 - SOMENTE CLIENTES
    // ==================================================

    @Bean
    public GroupedOpenApi grupoV2() {

        return GroupedOpenApi.builder()

                .group("v2")

                .pathsToMatch(
                        "/api/clientes",
                        "/api/clientes/**"
                )

                .addOpenApiMethodFilter(

                        method -> method.getDeclaringClass()
                                .getSimpleName()
                                .equals("ClienteV2Controller")
                )

                .addOpenApiCustomizer(errosDeFiltros(true))

                .build();
    }
}
