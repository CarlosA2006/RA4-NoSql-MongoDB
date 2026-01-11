package com.dam.accesodatos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI (Swagger) para documentación de la API.
 *
 * Acceder a:
 * - Swagger UI: http://localhost:8083/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8083/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8083}")
    private int serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Proyecto Pedagógico MongoDB - API REST")
                        .version("1.0.0")
                        .description("""
                                API REST educativa que demuestra dos enfoques para trabajar con MongoDB en Spring Boot:

                                **API Nativa** (`/api/native`): Usa directamente el driver de MongoDB con MongoClient,
                                MongoCollection y Document. Ideal para entender el funcionamiento interno de MongoDB.

                                **Spring Data** (`/api/springdata`): Usa las abstracciones de Spring Data MongoDB
                                como MongoRepository y MongoTemplate. Más productivo para desarrollo real.

                                ## Usuarios de prueba
                                - `admin` / `admin123` (rol ADMIN)
                                - `user` / `user123` (rol USER)

                                ## Métodos TODO para estudiantes
                                Los siguientes métodos deben ser implementados por los estudiantes:
                                - `findAll()` - Listar todos los usuarios
                                - `findUsersByDepartment()` - Filtrar por departamento
                                - `searchUsers()` - Búsqueda avanzada con paginación
                                - `countByDepartment()` - Contar usuarios por departamento
                                """)
                        .contact(new Contact()
                                .name("DAM - Acceso a Datos")
                                .email("dam@instituto.edu"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor local de desarrollo")))
                .tags(List.of(
                        new Tag()
                                .name("API Nativa")
                                .description("Endpoints usando el driver nativo de MongoDB"),
                        new Tag()
                                .name("Spring Data")
                                .description("Endpoints usando Spring Data MongoDB")));
    }
}
