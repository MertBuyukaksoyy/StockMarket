package com.example.stockmarket.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
@SecurityScheme(name = "Bearer Authentication",type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                //.addSecurityItem(new SecurityRequirement().addList("Bearer"))
               // .components(new Components().addSecuritySchemes("Bearer" , SecuritySchemeType.APIKEY ))
                .info(new Info()
                        .title("Stock Market API")
                        .version("1.0")
                        .description("Stock Market API Documentation"));
    }

}
