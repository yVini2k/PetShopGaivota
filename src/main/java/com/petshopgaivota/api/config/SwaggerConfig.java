
package com.petshopgaivota.api.config;

import jakarta.servlet.http.HttpServletRequest;

import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;

import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class SwaggerConfig {

    @Bean
    public SwaggerIndexTransformer swaggerIndexTransformer(
            SwaggerUiConfigProperties properties,
            SwaggerUiOAuthProperties oauthProperties,
            SwaggerWelcomeCommon welcomeCommon,
            ObjectMapperProvider objectMapperProvider) {

        return new SwaggerIndexPageTransformer(
                properties,
                oauthProperties,
                welcomeCommon,
                objectMapperProvider) {

            @Override
            public Resource transform(
                    HttpServletRequest request,
                    Resource resource,
                    ResourceTransformerChain chain) throws IOException {

                Resource original = super.transform(
                        request, resource, chain);

                if ("index.html".equals(resource.getFilename())) {

                    String html = StreamUtils.copyToString(
                            original.getInputStream(),
                            StandardCharsets.UTF_8);

                    html = html.replace(
                            "</head>",
                            "<link rel=\"stylesheet\" href=\"/swagger.css?v=3\">" +
                                    "</head>");

                    return new TransformedResource(
                            original,
                            html.getBytes(StandardCharsets.UTF_8));
                }

                return original;
            }
        };
    }
}
