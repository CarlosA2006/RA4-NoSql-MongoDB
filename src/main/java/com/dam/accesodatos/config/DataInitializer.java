package com.dam.accesodatos.config;

import com.dam.accesodatos.model.User;
import com.dam.accesodatos.mongodb.springdata.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                log.info("Base de datos ya contiene datos, omitiendo inicialización");
                return;
            }

            log.info("Inicializando base de datos con usuarios de prueba...");

            var users = Arrays.asList(
                    createUser("Juan Pérez", "juan.perez@empresa.com", "IT", "Developer",
                            LocalDateTime.of(2024, 1, 15, 9, 30)),
                    createUser("María García", "maria.garcia@empresa.com", "HR", "Manager",
                            LocalDateTime.of(2024, 1, 16, 10, 15)),
                    createUser("Carlos López", "carlos.lopez@empresa.com", "Finance", "Analyst",
                            LocalDateTime.of(2024, 1, 17, 11, 0)),
                    createUser("Ana Martínez", "ana.martinez@empresa.com", "IT", "Senior Developer",
                            LocalDateTime.of(2024, 1, 18, 8, 45)),
                    createUser("Luis Rodríguez", "luis.rodriguez@empresa.com", "Marketing", "Specialist",
                            LocalDateTime.of(2024, 1, 19, 13, 20)),
                    createUserInactive("Elena Fernández", "elena.fernandez@empresa.com", "IT", "DevOps",
                            LocalDateTime.of(2024, 1, 20, 9, 0)),
                    createUser("Pedro Sánchez", "pedro.sanchez@empresa.com", "Sales", "Representative",
                            LocalDateTime.of(2024, 1, 21, 10, 30)),
                    createUser("Laura González", "laura.gonzalez@empresa.com", "HR", "Recruiter",
                            LocalDateTime.of(2024, 1, 22, 14, 0))
            );

            repository.saveAll(users);

            log.info("✓ Base de datos inicializada con {} usuarios", users.size());
            log.info("  - IT: 3 usuarios (1 inactivo)");
            log.info("  - HR: 2 usuarios");
            log.info("  - Finance, Marketing, Sales: 1 usuario cada uno");
        };
    }

    private User createUser(String name, String email, String department, String role, LocalDateTime createdAt) {
        User user = new User(name, email, department, role);
        user.setActive(true);
        user.setCreatedAt(createdAt);
        user.setUpdatedAt(createdAt);
        return user;
    }

    private User createUserInactive(String name, String email, String department, String role, LocalDateTime createdAt) {
        User user = createUser(name, email, department, role, createdAt);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.of(2024, 2, 1, 10, 0));
        return user;
    }
}
