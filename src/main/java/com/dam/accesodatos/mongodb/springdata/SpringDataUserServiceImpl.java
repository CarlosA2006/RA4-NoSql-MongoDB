package com.dam.accesodatos.mongodb.springdata;

import com.dam.accesodatos.exception.DuplicateEmailException;
import com.dam.accesodatos.exception.UserNotFoundException;
import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;
import com.dam.accesodatos.model.UserQueryDto;
import com.dam.accesodatos.model.UserUpdateDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SpringDataUserServiceImpl implements SpringDataUserService {

    private static final Logger log = LoggerFactory.getLogger(SpringDataUserServiceImpl.class);

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public SpringDataUserServiceImpl(UserRepository userRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        log.info("SpringDataUserService inicializado");
    }

    @Override
    public String testConnection() {
        log.debug("Probando conexión a MongoDB con Spring Data...");
        try {
            long count = mongoTemplate.count(new Query(), User.class);
            boolean collectionExists = mongoTemplate.collectionExists(User.class);
            String collectionName = mongoTemplate.getCollectionName(User.class);

            String message = String.format("Conexión Spring Data exitosa | Colección: %s | Existe: %s | Usuarios: %d",
                    collectionName, collectionExists, count);
            log.info(message);
            return message;
        } catch (Exception e) {
            log.error("Error al probar conexión: {}", e.getMessage(), e);
            throw new RuntimeException("Error al probar conexión: " + e.getMessage(), e);
        }
    }

    @Override
    public User createUser(UserCreateDto dto) {
        log.debug("Creando usuario con email: {}", dto.getEmail());
        try {
            User user = new User(dto.getName(), dto.getEmail(), dto.getDepartment(), dto.getRole());
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);
            log.info("Usuario creado exitosamente con ID: {}", savedUser.getId());
            return savedUser;
        } catch (Exception e) {
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("E11000")) {
                log.warn("Intento de crear usuario con email duplicado: {}", dto.getEmail());
                throw new DuplicateEmailException(dto.getEmail());
            }
            log.error("Error al crear usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public User findUserById(String id) {
        log.debug("Buscando usuario por ID: {}", id);
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.warn("Usuario no encontrado con ID: {}", id);
            throw new UserNotFoundException(id);
        }
        log.debug("Usuario encontrado: {}", user.getEmail());
        return user;
    }

    @Override
    public User updateUser(String id, UserUpdateDto dto) {
        log.debug("Actualizando usuario con ID: {}", id);
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Usuario no encontrado para actualizar con ID: {}", id);
                        return new UserNotFoundException(id);
                    });

            dto.applyTo(user);
            user.setUpdatedAt(LocalDateTime.now());

            User updatedUser = userRepository.save(user);
            log.info("Usuario actualizado exitosamente: {}", id);
            return updatedUser;
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            if (e.getMessage() != null && (e.getMessage().contains("duplicate key") || e.getMessage().contains("E11000"))) {
                log.warn("Intento de actualizar con email duplicado: {}", dto.getEmail());
                throw new DuplicateEmailException(dto.getEmail());
            }
            log.error("Error al actualizar usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUser(String id) {
        log.debug("Eliminando usuario con ID: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Usuario no encontrado para eliminar: {}", id);
            return false;
        }
        userRepository.deleteById(id);
        log.info("Usuario eliminado exitosamente: {}", id);
        return true;
    }

    @Override
    public List<User> findAll() {
        // TODO: Implementar findAll() - Los estudiantes deben completar este método
        // PISTAS:
        // 1. Usar userRepository.findAll()
        // 2. Es una sola línea de código
        throw new UnsupportedOperationException("TODO: Implementar findAll() - Los estudiantes deben completar este método");
    }

    @Override
    public List<User> findUsersByDepartment(String department) {
        // TODO: Implementar findUsersByDepartment() - Los estudiantes deben completar este método
        // PISTAS:
        // 1. Usar userRepository.findByDepartment(department)
        // 2. El método ya está definido en UserRepository
        throw new UnsupportedOperationException("TODO: Implementar findUsersByDepartment() - Los estudiantes deben completar este método");
    }

    @Override
    public List<User> searchUsers(UserQueryDto query) {
        // TODO: Implementar searchUsers() - Los estudiantes deben completar este método
        // PISTAS:
        // 1. Usar mongoTemplate con Query y Criteria
        // 2. Construir criterios dinámicos: Criteria.where("campo").is(valor)
        // 3. Para búsqueda parcial: Criteria.where("name").regex(query.getName(), "i")
        // 4. Aplicar paginación con query.skip() y query.limit()
        // 5. Aplicar ordenamiento con query.with(Sort.by(...))
        throw new UnsupportedOperationException("TODO: Implementar searchUsers() - Los estudiantes deben completar este método");
    }

    @Override
    public long countByDepartment(String department) {
        // TODO: Implementar countByDepartment() - Los estudiantes deben completar este método
        // PISTAS:
        // 1. Usar userRepository.countByDepartment(department)
        // 2. El método ya está definido en UserRepository
        throw new UnsupportedOperationException("TODO: Implementar countByDepartment() - Los estudiantes deben completar este método");
    }
}
