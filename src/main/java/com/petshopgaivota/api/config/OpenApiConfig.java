
package com.petshopgaivota.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pet Shop Gaivota ")
                        .version("1.0.0")
                        .description("O Pet Shop Gaivota é especializado em serviços de banho e tosa," +
                                " oferecendo cuidado, higiene e bem-estar para cães e outros animais de estimação." +
                                " Nosso compromisso é proporcionar um atendimento atencioso, seguro e de qualidade," +
                                " garantindo que cada pet receba o carinho e os cuidados que merece," ));


    }
}
