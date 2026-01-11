# Arquitectura del Proyecto - MongoDB Pedagógico

## Visión General

Este documento describe la arquitectura técnica del proyecto pedagógico de MongoDB, diseñado para enseñar dos enfoques de acceso a datos NoSQL.

## Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                    Spring Boot Application                       │
│                   (Puerto 8083)                                  │
└────────┬──────────────────────────────────────────┬─────────────┘
         │                                          │
         │                                          │
    ┌────▼─────────┐                         ┌─────▼───────────┐
    │              │                         │                 │
    │  Native API  │                         │  Spring Data    │
    │   Module     │                         │     Module      │
    │              │                         │                 │
    └────┬─────────┘                         └─────┬───────────┘
         │                                          │
         │  MongoClient                             │  MongoRepository
         │  MongoCollection                         │  MongoTemplate
         │  Document/Bson                           │  Query/Criteria
         │                                          │
         └────────────┬─────────────────────────────┘
                      │
                      │
              ┌───────▼────────┐
              │                │
              │  MongoDB       │
              │  Embebido      │
              │  (Flapdoodle)  │
              │                │
              │  localhost:27017│
              │  DB: pedagogico_db │
              │  Collection: users  │
              │                │
              └────────────────┘
```

## Capas de la Aplicación

### 1. Capa de Presentación (Controllers)

**Responsabilidad**: Exposición de API REST

- `NativeMongoController` - Endpoints para API nativa
- `SpringDataController` - Endpoints para Spring Data

**Características**:
- Validación de entrada con `@Valid`
- Manejo de excepciones con `@ExceptionHandler`
- Respuestas HTTP estándar (200, 201, 404, 400)

### 2. Capa de Servicio

#### Módulo API Nativa

**Interface**: `NativeMongoUserService`  
**Implementación**: `NativeMongoUserServiceImpl`

**Dependencias**:
- `MongoClient` (inyectado desde MongoConfig)
- `String databaseName` (desde application.yml)

**Operaciones**:
- Acceso directo a `MongoCollection<Document>`
- Construcción manual de `Document` para insert/update
- Uso de `Filters`, `Updates`, `Sorts` para queries
- Mapeo manual `Document` ↔ `User`

#### Módulo Spring Data

**Interface**: `SpringDataUserService`  
**Implementación**: `SpringDataUserServiceImpl`

**Dependencias**:
- `UserRepository extends MongoRepository<User, String>`
- `MongoTemplate` (para queries complejas)

**Operaciones**:
- CRUD automático via `MongoRepository`
- Query methods derivados del nombre
- Queries complejas con `Criteria` y `Query`
- Mapeo automático `Document` ↔ `User` por Spring Data

### 3. Capa de Repositorio

**UserRepository** (solo Spring Data):
```java
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByDepartment(String department);
    long countByDepartment(String department);
    // Spring Data genera implementación automáticamente
}
```

### 4. Capa de Modelo

**Entidades**:
- `User` - Modelo principal con anotaciones MongoDB

**DTOs**:
- `UserCreateDto` - Para creación (validación completa)
- `UserUpdateDto` - Para actualización (campos opcionales)
- `UserQueryDto` - Para búsquedas (con paginación)

**Anotaciones clave**:
```java
@Document(collection = "users")     // Mapeo a colección
@Id                                  // Campo _id de MongoDB
@Indexed(unique = true)              // Índice único
@CreatedDate                         // Timestamp automático
@LastModifiedDate                    // Timestamp automático
```

### 5. Capa de Configuración

**MongoConfig**:
- Extiende `AbstractMongoClientConfiguration`
- Configura conexión a MongoDB (host, port, database)
- Proporciona beans: `MongoClient`, `MongoTemplate`
- Habilita `@EnableMongoRepositories`

**DataInitializer**:
- Implementa `CommandLineRunner`
- Carga 8 usuarios de prueba al arrancar
- Solo inserta si la colección está vacía

### 6. Capa de Persistencia

**MongoDB Embebido (Flapdoodle)**:
- Se inicia automáticamente con Spring Boot
- Puerto: 27017
- Base de datos: pedagogico_db
- Colección: users
- Versión: 6.0.5

## Flujo de Datos

### Ejemplo: Crear Usuario con API Nativa

```
1. HTTP POST /api/native/users
   Body: {"name":"Test","email":"test@test.com",...}
   
2. NativeMongoController.createUser(@RequestBody UserCreateDto dto)
   - Valida DTO con @Valid
   
3. NativeMongoUserService.createUser(dto)
   - Obtiene MongoCollection<Document>
   - Crea Document manualmente:
     new Document()
       .append("name", dto.getName())
       .append("email", dto.getEmail())
       ...
   - Ejecuta insertOne(doc)
   - Obtiene ObjectId generado
   
4. MongoDB Embebido
   - Inserta documento en colección "users"
   - Genera _id automáticamente
   
5. Respuesta
   - Mapea Document a User
   - Retorna User con ID en JSON
   - HTTP 201 Created
```

### Ejemplo: Crear Usuario con Spring Data

```
1. HTTP POST /api/springdata/users
   Body: {"name":"Test","email":"test@test.com",...}
   
2. SpringDataController.createUser(@RequestBody UserCreateDto dto)
   - Valida DTO con @Valid
   
3. SpringDataUserService.createUser(dto)
   - Crea objeto User directamente
   - Ejecuta userRepository.save(user)
   - Spring Data mapea User a Document automáticamente
   
4. MongoDB Embebido
   - Spring Data ejecuta insertOne internamente
   - Genera _id automáticamente
   
5. Respuesta
   - Spring Data mapea Document a User automáticamente
   - Retorna User con ID en JSON
   - HTTP 201 Created
```

**Diferencia clave**: Spring Data elimina el mapeo manual Document ↔ User.

## Decisiones de Diseño

### 1. ¿Por qué dos módulos separados?

**Objetivo pedagógico**: Permitir comparación directa entre:
- **API Nativa**: Más código, más control, mejor comprensión
- **Spring Data**: Menos código, más "magia", mayor productividad

Los estudiantes aprenden **qué hace Spring Data internamente**.

### 2. ¿Por qué MongoDB embebido?

**Ventajas**:
- ✅ Cero instalación requerida
- ✅ Funciona en cualquier sistema operativo
- ✅ Ideal para desarrollo y testing
- ✅ Base de datos en memoria (rápido, limpio)

**Desventajas** (aceptables para proyecto pedagógico):
- ❌ No soporta replica sets (transacciones limitadas)
- ❌ Rendimiento inferior a MongoDB real
- ❌ No persiste datos entre ejecuciones

Para producción, se usaría MongoDB real o MongoDB Atlas.

### 3. ¿Por qué String como ID en lugar de Long?

MongoDB genera **ObjectId** automáticamente:
- Formato: 24 caracteres hexadecimales
- Tipo Java: `String` (en modelo)
- Tipo MongoDB: `ObjectId` (internamente)

Convertimos en API nativa:
```java
new ObjectId(id)  // String → ObjectId
objectId.toString()  // ObjectId → String
```

Spring Data maneja esto automáticamente con `@Id private String id`.

### 4. ¿Por qué 5 ejemplos + 4 TODOs?

**5 ejemplos**: Cubren patrones fundamentales
- testConnection - Verificación
- createUser - INSERT
- findUserById - SELECT por ID
- updateUser - UPDATE
- deleteUser - DELETE

**4 TODOs**: Ejercicios progresivos
- findAll - Básico (iterar)
- findUsersByDepartment - Medio (filtros)
- searchUsers - Avanzado (filtros múltiples + paginación)
- countByDepartment - Avanzado (aggregation)

**Progresión**: De simple a complejo, preparando para uso real.

## Comparativa: JDBC vs MongoDB

| Aspecto | JDBC (SQL Relacional) | MongoDB (NoSQL Documental) |
|---------|----------------------|---------------------------|
| **Conexión** | `DriverManager.getConnection()` | `MongoClients.create()` |
| **Schema** | Fijo (CREATE TABLE) | Flexible (schema-less) |
| **ID** | `Long` autogenerado | `ObjectId` (String hex) |
| **Insertar** | `PreparedStatement.executeUpdate()` | `collection.insertOne(doc)` |
| **Buscar** | `ResultSet.next()` + getters | `find().first()` + Document |
| **Actualizar** | `UPDATE SET ...` + params | `Updates.set()` + filtro |
| **Filtros** | SQL WHERE clause | `Filters.eq/and/regex()` |
| **Joins** | JOIN nativo | $lookup (aggregation) o embeds |
| **Índices** | `CREATE INDEX` | `@Indexed` o `createIndex()` |
| **Transacciones** | `setAutoCommit(false)` | `ClientSession` (replica sets) |
| **ORM** | JPA/Hibernate | Spring Data MongoDB |

## Tecnologías Utilizadas

### Core
- **Java 21** - Lenguaje de programación
- **Spring Boot 3.2.0** - Framework de aplicación
- **Spring Data MongoDB** - Abstracción de persistencia
- **MongoDB Driver Sync 4.11.1** - Driver nativo Java

### MongoDB
- **Flapdoodle Embed Mongo 4.11.0** - MongoDB embebido
- **MongoDB 6.0.5** - Versión de base de datos

### Testing
- **JUnit 5** - Framework de testing
- **AssertJ** - Assertions fluidas
- **Spring Boot Test** - Testing de integración

### Validación y Serialización
- **Jakarta Validation** - Validación de DTOs
- **Jackson** - Serialización JSON
- **SLF4J + Logback** - Logging

## Configuración

### application.yml

```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: pedagogico_db
      auto-index-creation: true  # Crea índices @Indexed automáticamente
  
  mongodb:
    embedded:
      version: 6.0.5  # Flapdoodle inicia esta versión

server:
  port: 8083

logging:
  level:
    org.springframework.data.mongodb: DEBUG  # Ver queries ejecutadas
```

### Beans configurados

```java
@Bean
public MongoClient mongoClient() {
    return MongoClients.create("mongodb://localhost:27017");
}

@Bean
public MongoTemplate mongoTemplate() {
    return new MongoTemplate(mongoClient(), "pedagogico_db");
}
```

## Seguridad y Validación

### Validación de Entrada

**Anotaciones Jakarta Validation**:
```java
@NotBlank(message = "El nombre es obligatorio")
@Size(min = 2, max = 50)
@Email(message = "Formato de email inválido")
```

**Validación en Controllers**:
```java
public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateDto dto) {
    // Spring valida automáticamente
    // Si falla, retorna HTTP 400 con mensaje de error
}
```

### Índices Únicos

**Email único**:
```java
@Indexed(unique = true)
private String email;
```

Si se intenta insertar email duplicado:
- MongoDB lanza `MongoWriteException`
- Código captura excepción y retorna error descriptivo

## Testing

### Estrategia

**Tests de Integración**:
- Usan `@SpringBootTest`
- MongoDB embebido se inicia automáticamente
- Cada test tiene datos limpios

**TDD Approach**:
1. Tests para métodos implementados → PASAN (GREEN)
2. Tests para métodos TODO → FALLAN con `UnsupportedOperationException` (RED)
3. Estudiante implementa → Tests pasan (GREEN)

### Ejemplo de Test

```java
@SpringBootTest
class NativeMongoUserServiceTest {
    @Autowired
    private NativeMongoUserService service;

    @Test
    void createUser_ValidData_ReturnsUserWithId() {
        UserCreateDto dto = new UserCreateDto("Test", "test@test.com", "IT", "Dev");
        User created = service.createUser(dto);
        
        assertThat(created.getId()).isNotNull();  // ObjectId generado
        assertThat(created.getName()).isEqualTo("Test");
    }
}
```

## Rendimiento y Optimización

### Índices

**Índices automáticos** (Spring Data):
```java
@Indexed
private String department;  // Índice en "department"

@Indexed(unique = true)
private String email;  // Índice único en "email"
```

**Verificar índices** (API nativa):
```java
MongoCollection<Document> collection = getCollection();
collection.listIndexes().forEach(index -> 
    System.out.println(index.toJson())
);
```

### Paginación

**API Nativa**:
```java
collection.find(filter)
    .skip(page * size)
    .limit(size)
    .sort(Sorts.ascending("name"));
```

**Spring Data**:
```java
Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
Page<User> results = userRepository.findAll(pageable);
```

## Limitaciones Conocidas

1. **MongoDB Embebido**:
   - No soporta replica sets → transacciones multi-documento limitadas
   - Rendimiento inferior a MongoDB real
   - Base de datos en memoria (se pierde al cerrar)

2. **Sin Autenticación**:
   - MongoDB embebido no tiene usuario/contraseña
   - Para producción, configurar autenticación

3. **Sin Transacciones Complejas**:
   - Flapdoodle no soporta replica sets
   - Transacciones multi-documento requieren replica set

4. **Logging Verboso**:
   - DEBUG de Spring Data MongoDB genera muchos logs
   - Para producción, cambiar a INFO

## Evolución Futura

### Posibles Mejoras Pedagógicas

1. **Documentos Anidados**:
   - Agregar campo `projects: List<Project>` en User
   - Enseñar arrays y documentos embebidos

2. **Aggregation Pipeline Avanzado**:
   - $group, $project, $unwind
   - Cálculos estadísticos

3. **Índices Compuestos**:
   - Índices multi-campo
   - Índices de texto completo (text search)

4. **Change Streams**:
   - Escuchar cambios en tiempo real
   - Programación reactiva con WebFlux

5. **MongoDB Real**:
   - Configuración con Docker Compose
   - Transacciones con replica sets

## Conclusión

Este proyecto proporciona una base sólida para aprender MongoDB desde dos perspectivas:

1. **API Nativa**: Comprensión profunda de cómo funciona MongoDB
2. **Spring Data**: Productividad y patrones modernos

La arquitectura modular permite a los estudiantes comparar directamente ambos enfoques y entender el valor de las abstracciones sin perder de vista los fundamentos.

---

**Versión**: 1.0.0  
**Última actualización**: 2024  
**Proyecto**: DAM - Acceso a Datos
