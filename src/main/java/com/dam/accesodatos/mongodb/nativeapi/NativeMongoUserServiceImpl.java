package com.dam.accesodatos.mongodb.nativeapi;

import com.dam.accesodatos.exception.DuplicateEmailException;
import com.dam.accesodatos.exception.InvalidUserIdException;
import com.dam.accesodatos.exception.UserNotFoundException;
import com.dam.accesodatos.model.DepartmentStatsDto;
import com.dam.accesodatos.model.User;
import com.dam.accesodatos.model.UserCreateDto;
import com.dam.accesodatos.model.UserQueryDto;
import com.dam.accesodatos.model.UserUpdateDto;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NativeMongoUserServiceImpl implements NativeMongoUserService {

    private static final Logger log = LoggerFactory.getLogger(NativeMongoUserServiceImpl.class);

    private final MongoClient mongoClient;
    private final String databaseName;

    @Autowired
    public NativeMongoUserServiceImpl(MongoClient mongoClient,
                                      @Value("${spring.data.mongodb.database}") String databaseName) {
        this.mongoClient = mongoClient;
        this.databaseName = databaseName;
        log.info("NativeMongoUserService inicializado con base de datos: {}", databaseName);
    }

    private MongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(databaseName).getCollection("users");
    }

    @Override
    public String testConnection() {
        log.debug("Probando conexión a MongoDB...");
        try {
            MongoDatabase database = mongoClient.getDatabase(databaseName);

            List<String> collections = new ArrayList<>();
            database.listCollectionNames().into(collections);

            Document pingCommand = new Document("ping", 1);
            Document result = database.runCommand(pingCommand);

            long userCount = getCollection().countDocuments();

            String message = String.format("Conexión API Nativa exitosa | BD: %s | Colecciones: %d | Usuarios: %d | Ping: %s",
                    databaseName, collections.size(), userCount, result.get("ok"));
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
            MongoCollection<Document> collection = getCollection();

            Document doc = new Document()
                    .append("name", dto.getName())
                    .append("email", dto.getEmail())
                    .append("department", dto.getDepartment())
                    .append("role", dto.getRole())
                    .append("active", true)
                    .append("createdAt", new Date())
                    .append("updatedAt", new Date());

            InsertOneResult result = collection.insertOne(doc);
            ObjectId id = result.getInsertedId().asObjectId().getValue();

            User user = mapDocumentToUser(doc, id.toString());
            log.info("Usuario creado exitosamente con ID: {}", id);
            return user;
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
        try {
            MongoCollection<Document> collection = getCollection();

            Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();

            if (doc == null) {
                log.warn("Usuario no encontrado con ID: {}", id);
                throw new UserNotFoundException(id);
            }

            User user = mapDocumentToUser(doc);
            log.debug("Usuario encontrado: {}", user.getEmail());
            return user;
        } catch (UserNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("ID de usuario inválido: {}", id);
            throw new InvalidUserIdException(id, e);
        } catch (Exception e) {
            log.error("Error al buscar usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public User updateUser(String id, UserUpdateDto dto) {
        log.debug("Actualizando usuario con ID: {}", id);
        try {
            MongoCollection<Document> collection = getCollection();

            List<Bson> updates = new ArrayList<>();
            if (dto.getName() != null) {
                updates.add(Updates.set("name", dto.getName()));
            }
            if (dto.getEmail() != null) {
                updates.add(Updates.set("email", dto.getEmail()));
            }
            if (dto.getDepartment() != null) {
                updates.add(Updates.set("department", dto.getDepartment()));
            }
            if (dto.getRole() != null) {
                updates.add(Updates.set("role", dto.getRole()));
            }
            if (dto.getActive() != null) {
                updates.add(Updates.set("active", dto.getActive()));
            }
            updates.add(Updates.set("updatedAt", new Date()));

            Bson updateOperation = Updates.combine(updates);

            UpdateResult result = collection.updateOne(
                    Filters.eq("_id", new ObjectId(id)),
                    updateOperation
            );

            if (result.getMatchedCount() == 0) {
                log.warn("Usuario no encontrado para actualizar con ID: {}", id);
                throw new UserNotFoundException(id);
            }

            User user = findUserById(id);
            log.info("Usuario actualizado exitosamente: {}", id);
            return user;
        } catch (UserNotFoundException | InvalidUserIdException e) {
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
        try {
            MongoCollection<Document> collection = getCollection();

            DeleteResult result = collection.deleteOne(Filters.eq("_id", new ObjectId(id)));

            if (result.getDeletedCount() > 0) {
                log.info("Usuario eliminado exitosamente: {}", id);
                return true;
            } else {
                log.warn("Usuario no encontrado para eliminar: {}", id);
                return false;
            }
        } catch (IllegalArgumentException e) {
            log.warn("ID de usuario inválido para eliminar: {}", id);
            throw new InvalidUserIdException(id, e);
        } catch (Exception e) {
            log.error("Error al eliminar usuario: {}", e.getMessage(), e);
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findAll() {
        // TODO: Implementar findAll() - Los estudiantes deben completar este método
        // PISTAS:
        // 1. Obtener la colección con getCollection()
        // 2. Usar collection.find() sin filtros
        // 3. Iterar con MongoCursor y mapear cada Document a User
        // 4. Retornar la lista de usuarios
        throw new UnsupportedOperationException("TODO: Implementar findAll() - Los estudiantes deben completar este método");
    }

    @Override
    public List<User> findUsersByDepartment(String department) {
        // TODO: Implementar findUsersByDepartment() - Los estudiantes deben completar este método
        // PISTAS:
        // 1. Obtener la colección con getCollection()
        // 2. Usar Filters.eq("department", department)
        // 3. Iterar con MongoCursor y mapear cada Document a User
        // 4. Retornar la lista de usuarios
        throw new UnsupportedOperationException("TODO: Implementar findUsersByDepartment() - Los estudiantes deben completar este método");
    }

    @Override
    public List<User> searchUsers(UserQueryDto query) {
        // TODO: Implementar searchUsers() - Los estudiantes deben completar este método
        // PISTAS:
        // 1. Construir filtros dinámicos con Filters.and()
        // 2. Usar Filters.regex() para búsqueda parcial por nombre
        // 3. Aplicar paginación con skip() y limit()
        // 4. Aplicar ordenamiento con Sorts.ascending() o Sorts.descending()
        throw new UnsupportedOperationException("TODO: Implementar searchUsers() - Los estudiantes deben completar este método");
    }

    @Override
    public long countByDepartment(String department) {
        // TODO: Implementar countByDepartment() - Los estudiantes deben completar este método
        // PISTAS:
        // 1. Obtener la colección con getCollection()
        // 2. Usar collection.countDocuments(Filters.eq("department", department))
        throw new UnsupportedOperationException("TODO: Implementar countByDepartment() - Los estudiantes deben completar este método");
    }

    /**
     * Ejemplo de Aggregation Pipeline: Obtiene estadísticas de usuarios por departamento.
     *
     * Este método demuestra el uso del framework de agregación de MongoDB, que permite:
     * - Agrupar documentos por campo ($group)
     * - Calcular totales ($sum)
     * - Calcular condicionalmente ($cond)
     * - Ordenar resultados ($sort)
     *
     * Pipeline equivalente en MongoDB Shell:
     * db.users.aggregate([
     *   { $group: {
     *       _id: "$department",
     *       totalUsers: { $sum: 1 },
     *       activeUsers: { $sum: { $cond: [{ $eq: ["$active", true] }, 1, 0] } }
     *   }},
     *   { $sort: { totalUsers: -1 } }
     * ])
     */
    @Override
    public List<DepartmentStatsDto> getStatsByDepartment() {
        log.debug("Obteniendo estadísticas por departamento con aggregation pipeline");
        try {
            MongoCollection<Document> collection = getCollection();
            List<DepartmentStatsDto> stats = new ArrayList<>();

            // Pipeline de agregación usando el Aggregates builder
            List<org.bson.conversions.Bson> pipeline = List.of(
                    // Etapa 1: Agrupar por departamento y calcular totales
                    Aggregates.group("$department",
                            Accumulators.sum("totalUsers", 1),
                            Accumulators.sum("activeUsers",
                                    new Document("$cond", List.of(
                                            new Document("$eq", List.of("$active", true)),
                                            1,
                                            0
                                    ))
                            )
                    ),
                    // Etapa 2: Ordenar por total de usuarios descendente
                    Aggregates.sort(Sorts.descending("totalUsers"))
            );

            // Ejecutar el pipeline y mapear resultados
            try (MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    DepartmentStatsDto dto = new DepartmentStatsDto(
                            doc.getString("_id"),           // department
                            doc.getInteger("totalUsers"),   // total
                            doc.getInteger("activeUsers")   // active
                    );
                    stats.add(dto);
                }
            }

            log.info("Estadísticas por departamento obtenidas: {} departamentos", stats.size());
            return stats;
        } catch (Exception e) {
            log.error("Error al obtener estadísticas: {}", e.getMessage(), e);
            throw new RuntimeException("Error al obtener estadísticas: " + e.getMessage(), e);
        }
    }

    private User mapDocumentToUser(Document doc) {
        return mapDocumentToUser(doc, doc.getObjectId("_id").toString());
    }

    private User mapDocumentToUser(Document doc, String id) {
        User user = new User();
        user.setId(id);
        user.setName(doc.getString("name"));
        user.setEmail(doc.getString("email"));
        user.setDepartment(doc.getString("department"));
        user.setRole(doc.getString("role"));
        user.setActive(doc.getBoolean("active", true));

        Date createdAt = doc.getDate("createdAt");
        if (createdAt != null) {
            user.setCreatedAt(LocalDateTime.ofInstant(createdAt.toInstant(), ZoneId.systemDefault()));
        }

        Date updatedAt = doc.getDate("updatedAt");
        if (updatedAt != null) {
            user.setUpdatedAt(LocalDateTime.ofInstant(updatedAt.toInstant(), ZoneId.systemDefault()));
        }

        return user;
    }
}
