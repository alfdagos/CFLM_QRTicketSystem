package it.cflm.qrticketsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione per la documentazione API OpenAPI/Swagger.
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CFLM QR Ticket System API")
                        .version("1.0")
                        .description("API per la gestione di biglietti con QR Code per eventi CFLM")
                        .contact(new Contact()
                                .name("CFLM Team")
                                .email("info@cflm.it"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
