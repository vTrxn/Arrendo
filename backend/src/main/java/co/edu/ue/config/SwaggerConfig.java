// package: Configuración OpenAPI / Swagger UI para Eclipse IDE
package co.edu.ue.config;

// import: Clases OpenAPI / Swagger UI
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration: Registra la configuración de Swagger UI
@Configuration
public class SwaggerConfig {

    // @Bean: Define el componente de documentación interactiva OpenAPI/Swagger UI con soporte para Token JWT Bearer
    @Bean
    public OpenAPI customOpenAPI() {
        String securitySchemeName = "BearerAuth";

        return new OpenAPI()
            .info(new Info()
                .title("API REST Arrendo - Servidor Eclipse IDE (IEEE 830)")
                .description("Servidor multiservicio backend Spring Boot en Java 17 con autenticación JWT, PostgreSQL y Swagger UI para el proyecto Arrendo de Uniempresarial.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Equipo de Desarrollo Uniempresarial")
                    .email("soporte@ue.edu.co")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Ingrese el token JWT generado en /api/v1/auth/login para probar los endpoints autorizados.")
                )
            );
    }
}