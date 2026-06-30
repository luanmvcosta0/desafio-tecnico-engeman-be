package com.luanmvcosta0.desafio_tecnico_engeman_be.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info().title("AcheImovel").version("1.0.0").description("Aplicação gestão de imóveis"));
    }

}
