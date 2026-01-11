# Proyecto Pedagógico: API Nativa de MongoDB y Spring Data MongoDB

## 📋 Descripción

Proyecto educativo diseñado para enseñar acceso a datos con MongoDB en dos modalidades complementarias:

1. **API Nativa de MongoDB** - Driver nativo con MongoClient, MongoCollection y Document
2. **Spring Data MongoDB** - Abstracción de alto nivel con MongoRepository y MongoTemplate

Este proyecto es similar al proyecto JDBC de referencia, pero adaptado para MongoDB NoSQL.

## 🎯 Objetivos de Aprendizaje

- ✅ Comprender la diferencia entre API nativa y abstracciones de alto nivel
- ✅ Dominar operaciones CRUD básicas en MongoDB
- ✅ Aprender construcción de queries y filtros (Filters, Criteria)
- ✅ Implementar búsquedas avanzadas con paginación
- ✅ Usar aggregation pipeline para analíticas
- ✅ Comparar paradigmas: NoSQL vs SQL, API nativa vs ORM

## 🚀 Inicio Rápido

### Pre-requisitos

- **Java 21** o superior
- **Gradle** (incluido via wrapper)
- **NO necesitas instalar MongoDB** - Usa MongoDB embebido (Flapdoodle)

### Compilar

```bash
cd proyecto-pedagogico-mongodb
./gradlew clean build
```

### Ejecutar

```bash
./gradlew bootRun
```

El servidor arranca en: **http://localhost:8083**

### MongoDB Embebido

El proyecto usa **Flapdoodle** que inicia una instancia de MongoDB en memoria automáticamente:
- **Puerto**: 27017
- **Base de datos**: pedagogico_db
- **Colección**: users

No necesitas instalar MongoDB ni Docker. Todo funciona automáticamente.

## 📚 Estructura del Proyecto

```
proyecto-pedagogico-mongodb/
├── src/main/java/com/dam/accesodatos/
│   ├── MongoDbTeachingApplication.java           # Aplicación principal
│   ├── config/
│   │   ├── MongoConfig.java                      # Configuración MongoDB
│   │   └── DataInitializer.java                  # Carga 8 usuarios iniciales
│   ├── model/
│   │   ├── User.java                             # Modelo con @Document
│   │   ├── UserCreateDto.java                    # DTO para crear
│   │   ├── UserUpdateDto.java                    # DTO para actualizar
│   │   └── UserQueryDto.java                     # DTO para búsquedas
│   ├── mongodb/
│   │   ├── nativeapi/                            # ⭐ MÓDULO 1: API NATIVA
│   │   │   ├── NativeMongoUserService.java       # Interface
│   │   │   └── NativeMongoUserServiceImpl.java   # 5 ejemplos + 4 TODOs
│   │   └── springdata/                           # ⭐ MÓDULO 2: SPRING DATA
│   │       ├── UserRepository.java               # MongoRepository
│   │       ├── SpringDataUserService.java        # Interface
│   │       └── SpringDataUserServiceImpl.java    # 5 ejemplos + 4 TODOs
│   └── controller/
│       ├── NativeMongoController.java            # REST API nativa
│       └── SpringDataController.java             # REST Spring Data
└── src/test/java/com/dam/accesodatos/mongodb/
    ├── nativeapi/NativeMongoUserServiceTest.java
    └── springdata/SpringDataUserServiceTest.java
```

## 📦 Módulo 1: API Nativa de MongoDB

### Conceptos que se enseñan

- `MongoClient` - Conexión directa a MongoDB
- `MongoDatabase` - Acceso a base de datos
- `MongoCollection<Document>` - Operaciones CRUD con documentos
- `Document` / `Bson` - Construcción de documentos BSON
- `Filters` - Construcción de filtros de búsqueda
- `Updates` - Construcción de operaciones de actualización
- `Sorts` - Ordenamiento de resultados

### ✅ Métodos Implementados (5 ejemplos para aprender)

#### 1. testConnection() - Verificar conexión

```java
// Muestra cómo:
// - Obtener MongoDatabase
// - Listar colecciones
// - Ejecutar comando ping
// - Contar documentos
```

**Ubicación**: `NativeMongoUserServiceImpl.java` línea 51

#### 2. createUser() - INSERT con Document

```java
// Muestra cómo:
// - Crear Document con datos
// - Usar insertOne()
// - Obtener ObjectId generado automáticamente
// - Manejar errores de clave duplicada
```

**Ubicación**: `NativeMongoUserServiceImpl.java` línea 68

**Ejemplo de uso**:
```bash
curl -X POST http://localhost:8083/api/native/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Test User","email":"test@test.com","department":"IT","role":"Developer"}'
```

#### 3. findUserById() - SELECT por ID

```java
// Muestra cómo:
// - Usar Filters.eq() para filtrar por _id
// - Convertir String a ObjectId
// - find().first() para obtener un documento
// - Mapear Document a objeto User
```

**Ubicación**: `NativeMongoUserServiceImpl.java` línea 94

#### 4. updateUser() - UPDATE con Updates

```java
// Muestra cómo:
// - Construir actualizaciones con Updates.set()
// - Combinar múltiples Updates con Updates.combine()
// - Usar updateOne() con filtro y update
// - Verificar modificaciones con getModifiedCount()
```

**Ubicación**: `NativeMongoUserServiceImpl.java` línea 116

#### 5. deleteUser() - DELETE

```java
// Muestra cómo:
// - Usar deleteOne() con filtro
// - Verificar eliminación con getDeletedCount()
```

**Ubicación**: `NativeMongoUserServiceImpl.java` línea 151

### ❌ Métodos TODO (4 para que estudiantes implementen)

#### TODO 1: findAll() - Dificultad ⭐ Básica

**Objetivo**: Listar todos los usuarios de la colección

**Instrucciones paso a paso**:
1. Obtener colección con `getCollection()`
2. Ejecutar `find()` sin filtros
3. Iterar con `MongoCursor<Document>` o usar `.into(new ArrayList<>())`
4. Mapear cada `Document` a `User` usando `mapDocumentToUser()`
5. Retornar lista de usuarios

**Ejemplo de estructura**:
```java
public List<User> findAll() {
    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();
    
    FindIterable<Document> documents = collection.find();
    for (Document doc : documents) {
        users.add(mapDocumentToUser(doc));
    }
    
    return users;
}
```

**Equivalente SQL**: `SELECT * FROM users`

---

#### TODO 2: findUsersByDepartment() - Dificultad ⭐⭐ Media

**Objetivo**: Buscar usuarios de un departamento específico

**Instrucciones paso a paso**:
1. Crear filtro: `Filters.eq("department", department)`
2. Ejecutar `find(filtro)`
3. Iterar resultados y mapear a lista de Users

**Clases requeridas**:
- `Filters.eq()`
- `MongoCollection.find(Bson filter)`

**Equivalente SQL**: `SELECT * FROM users WHERE department = 'IT'`

---

#### TODO 3: searchUsers() - Dificultad ⭐⭐⭐ Alta

**Objetivo**: Búsqueda avanzada con filtros múltiples y paginación

**Instrucciones paso a paso**:
1. Construir filtros dinámicos según `UserQueryDto`:
   - Si `name != null`: `Filters.regex("name", Pattern.quote(name), "i")`
   - Si `department != null`: `Filters.eq("department", department)`
   - Si `active != null`: `Filters.eq("active", active)`
2. Combinar filtros con `Filters.and(filtro1, filtro2, ...)`
3. Aplicar paginación: `.skip(query.getOffset()).limit(query.getSize())`
4. Aplicar ordenamiento: `.sort(Sorts.ascending("name"))`
5. Iterar y mapear resultados

**Clases requeridas**:
- `Filters.and()`, `Filters.regex()`, `Filters.eq()`
- `FindIterable.skip()`, `.limit()`, `.sort()`
- `Sorts.ascending()` o `Sorts.descending()`

**Equivalente SQL**: 
```sql
SELECT * FROM users 
WHERE name LIKE '%search%' AND department = 'IT' AND active = true
ORDER BY name ASC
LIMIT 10 OFFSET 0
```

---

#### TODO 4: countByDepartment() - Dificultad ⭐⭐⭐ Alta

**Objetivo**: Contar usuarios por departamento usando Aggregation Pipeline

**Instrucciones paso a paso**:
1. Crear pipeline de agregación:
   - Stage 1: `Aggregates.match(Filters.eq("department", department))`
   - Stage 2: `Aggregates.count("total")`
2. Ejecutar: `collection.aggregate(Arrays.asList(matchStage, countStage))`
3. Obtener primer resultado con `.first()`
4. Extraer valor: `result.getInteger("total", 0)`

**Clases requeridas**:
- `Aggregates.match()`, `Aggregates.count()`
- `MongoCollection.aggregate()`

**Equivalente SQL**: `SELECT COUNT(*) FROM users WHERE department = 'IT'`

**Equivalente Aggregation Pipeline**:
```javascript
[
  { $match: { department: "IT" } },
  { $count: "total" }
]
```

---

## 🔧 Módulo 2: Spring Data MongoDB

### Conceptos que se enseñan

- `MongoRepository<User, String>` - CRUD automático sin código
- **Query Methods** - Métodos derivados del nombre (findByDepartment)
- `MongoTemplate` - Queries complejas de bajo nivel
- `Criteria` - Construcción de queries dinámicas
- `Query` - Wrapper para queries con paginación y ordenamiento
- `@Document`, `@Id`, `@Indexed` - Anotaciones de mapeo

### ✅ Métodos Implementados (5 ejemplos para aprender)

#### 1. testConnection() - Verificar con MongoTemplate

```java
// Muestra cómo:
// - Usar MongoTemplate para operaciones de bajo nivel
// - Verificar existencia de colección
// - Contar documentos con Query vacía
```

#### 2. createUser() - INSERT con save()

```java
// Muestra cómo:
// - Crear objeto User directamente (no Document)
// - Usar repository.save()
// - Spring Data genera ObjectId automáticamente
```

#### 3. findUserById() - SELECT con findById()

```java
// Muestra cómo:
// - Usar repository.findById() que retorna Optional<User>
// - Manejar Optional con orElse(null)
```

#### 4. updateUser() - UPDATE con save()

```java
// Muestra cómo:
// - Buscar primero con findById()
// - Modificar objeto Java
// - save() detecta cambios y actualiza automáticamente
```

#### 5. deleteUser() - DELETE con deleteById()

```java
// Muestra cómo:
// - Verificar existencia con existsById()
// - Eliminar con repository.deleteById()
```

### ❌ Métodos TODO (4 para que estudiantes implementen)

#### TODO 1: findAll() - Dificultad ⭐ Básica

**Instrucciones**:
```java
public List<User> findAll() {
    return userRepository.findAll();  // ¡Una sola línea!
}
```

**Nota pedagógica**: Compara con la complejidad de la API nativa.

---

#### TODO 2: findUsersByDepartment() - Dificultad ⭐ Básica

**Instrucciones**:
1. En `UserRepository.java`, agregar método:
   ```java
   List<User> findByDepartment(String department);
   ```
2. Spring Data genera la implementación automáticamente
3. En el servicio, llamar: `return userRepository.findByDepartment(department);`

**Nota pedagógica**: Spring Data deriva la query del nombre del método:
- `findBy` + `Department` = consulta por campo "department"

---

#### TODO 3: searchUsers() - Dificultad ⭐⭐⭐ Alta

**Objetivo**: Búsqueda compleja con MongoTemplate y Criteria

**Instrucciones paso a paso**:
1. Crear `Query query = new Query()`
2. Construir `List<Criteria> criteria = new ArrayList<>()`
3. Agregar criterios dinámicamente:
   ```java
   if (queryDto.getName() != null) {
       criteria.add(Criteria.where("name").regex(queryDto.getName(), "i"));
   }
   if (queryDto.getDepartment() != null) {
       criteria.add(Criteria.where("department").is(queryDto.getDepartment()));
   }
   ```
4. Combinar criterios: `query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])))`
5. Paginación: `query.skip(queryDto.getOffset()).limit(queryDto.getSize())`
6. Ordenamiento: `query.with(Sort.by(Sort.Direction.ASC, "name"))`
7. Ejecutar: `return mongoTemplate.find(query, User.class)`

**Clases requeridas**:
- `Query`, `Criteria`
- `MongoTemplate.find()`
- `Sort`

---

#### TODO 4: countByDepartment() - Dificultad ⭐⭐ Media

**Opción A (Query Method - más fácil)**:
1. En `UserRepository.java` agregar: `long countByDepartment(String department);`
2. Llamar desde el servicio

**Opción B (Aggregation - más educativo)**:
1. Crear `MatchOperation match = Aggregation.match(Criteria.where("department").is(department))`
2. Crear `CountOperation count = Aggregation.count().as("total")`
3. Crear `Aggregation aggregation = Aggregation.newAggregation(match, count)`
4. Ejecutar: `AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "users", Document.class)`
5. Obtener resultado: `results.getUniqueMappedResult().getInteger("total")`

**Recomendación pedagógica**: Implementa ambas opciones para comparar.

---

## 🔍 Comparación: API Nativa vs Spring Data

| Operación | API Nativa | Spring Data | Líneas de código |
|-----------|------------|-------------|------------------|
| **Insert** | `collection.insertOne(doc)` + mapeo manual | `repository.save(user)` | 15 vs 3 |
| **Find by ID** | `collection.find(Filters.eq("_id", objectId)).first()` + mapeo | `repository.findById(id)` | 10 vs 1 |
| **Find All** | `collection.find().into(list)` + loop mapeo | `repository.findAll()` | 8 vs 1 |
| **Update** | `collection.updateOne(filter, Updates.combine(...))` | `repository.save(modifiedUser)` | 20 vs 5 |
| **Delete** | `collection.deleteOne(filter)` | `repository.deleteById(id)` | 5 vs 2 |
| **Query compleja** | Filters.and() + skip/limit manual | Criteria + Query + MongoTemplate | 25 vs 15 |

### Conclusión Pedagógica

- **API Nativa**: Más control, más código, mejor comprensión de cómo funciona MongoDB internamente
- **Spring Data**: Menos código, más productividad, pero "magia" que oculta complejidad

**Aprendizaje recomendado**: Empezar con API nativa para entender los fundamentos, luego apreciar la abstracción de Spring Data.

---

## 📊 Datos de Prueba

El proyecto carga automáticamente 8 usuarios:

| Nombre | Email | Departamento | Rol | Activo |
|--------|-------|--------------|-----|--------|
| Juan Pérez | juan.perez@empresa.com | IT | Developer | ✅ |
| María García | maria.garcia@empresa.com | HR | Manager | ✅ |
| Carlos López | carlos.lopez@empresa.com | Finance | Analyst | ✅ |
| Ana Martínez | ana.martinez@empresa.com | IT | Senior Developer | ✅ |
| Luis Rodríguez | luis.rodriguez@empresa.com | Marketing | Specialist | ✅ |
| Elena Fernández | elena.fernandez@empresa.com | IT | DevOps | ❌ |
| Pedro Sánchez | pedro.sanchez@empresa.com | Sales | Representative | ✅ |
| Laura González | laura.gonzalez@empresa.com | HR | Recruiter | ✅ |

**Distribución**:
- IT: 3 usuarios (1 inactivo)
- HR: 2 usuarios
- Finance, Marketing, Sales: 1 usuario cada uno

---

## 🧪 Testing

### Ejecutar todos los tests

```bash
./gradlew test
```

### Estrategia TDD

1. **RED**: Ejecutar test → Falla (UnsupportedOperationException en TODOs)
2. **GREEN**: Implementar método → Test pasa
3. **REFACTOR**: Mejorar código → Tests siguen pasando

### Tests incluidos

- ✅ Tests para 5 métodos implementados de API Nativa
- ✅ Tests para 5 métodos implementados de Spring Data
- ❌ Tests para TODOs (fallan hasta que estudiantes implementen)

---

## 🌐 API REST

### Endpoints API Nativa

Base URL: `http://localhost:8083/api/native`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/test-connection` | Prueba de conexión |
| POST | `/users` | Crear usuario |
| GET | `/users/{id}` | Buscar por ID |
| PUT | `/users/{id}` | Actualizar usuario |
| DELETE | `/users/{id}` | Eliminar usuario |
| GET | `/users` | Listar todos |
| GET | `/users/department/{dept}` | Filtrar por departamento |
| POST | `/users/search` | Búsqueda avanzada |
| GET | `/users/count/department/{dept}` | Contar por departamento |

### Endpoints Spring Data

Base URL: `http://localhost:8083/api/springdata`

(Mismos endpoints que API Nativa)

### Ejemplos de uso con curl

```bash
# Probar conexión API Nativa
curl http://localhost:8083/api/native/test-connection

# Probar conexión Spring Data
curl http://localhost:8083/api/springdata/test-connection

# Crear usuario con API Nativa
curl -X POST http://localhost:8083/api/native/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Nuevo Usuario","email":"nuevo@test.com","department":"IT","role":"Tester"}'

# Buscar por ID (reemplazar {id} con ID real)
curl http://localhost:8083/api/native/users/{id}

# Listar todos los usuarios (requiere implementar TODO)
curl http://localhost:8083/api/native/users

# Buscar por departamento (requiere implementar TODO)
curl http://localhost:8083/api/native/users/department/IT
```

---

## 💡 Tips para Estudiantes

### 1. Orden de Implementación Recomendado

**API Nativa** (más difícil, más aprendizaje):
1. ⭐ `findAll()` - Básico, construir sobre ejemplo de `findUserById()`
2. ⭐⭐ `findUsersByDepartment()` - Similar a findAll() pero con filtro
3. ⭐⭐⭐ `searchUsers()` - Combina filtros, paginación y ordenamiento
4. ⭐⭐⭐ `countByDepartment()` - Introduce aggregation pipeline

**Spring Data** (más fácil, menos código):
1. ⭐ `findAll()` - Literalmente una línea
2. ⭐ `findUsersByDepartment()` - Query method automático
3. ⭐⭐⭐ `searchUsers()` - Criteria dinámico con MongoTemplate
4. ⭐⭐ `countByDepartment()` - Query method o aggregation

### 2. Debugging

**Ver queries ejecutadas**:
```yaml
# En application.yml ya está configurado:
logging:
  level:
    org.springframework.data.mongodb: DEBUG
```

Verás en consola las queries MongoDB reales.

**Usar MongoTemplate desde tests**:
```java
@Autowired
private MongoTemplate mongoTemplate;

@Test
void debugQuery() {
    List<Document> docs = mongoTemplate.findAll(Document.class, "users");
    docs.forEach(doc -> System.out.println(doc.toJson()));
}
```

### 3. Errores Comunes

**Error: "Invalid hexadecimal representation of an ObjectId"**
- **Causa**: El ID no es un ObjectId válido
- **Solución**: Verifica que el ID sea de 24 caracteres hexadecimales

**Error: "E11000 duplicate key error"**
- **Causa**: Email duplicado (campo único)
- **Solución**: Cambia el email o elimina el usuario existente

**UnsupportedOperationException**
- **Causa**: Método TODO no implementado aún
- **Solución**: Implementa el método siguiendo las instrucciones

### 4. Recursos Adicionales

- **MongoDB Manual**: https://docs.mongodb.com/manual/
- **Spring Data MongoDB**: https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/
- **MongoDB Driver Java**: https://mongodb.github.io/mongo-java-driver/
- **BSON Specification**: http://bsonspec.org/

---

## 🎓 Comparación con Proyecto JDBC

| Aspecto | JDBC (SQL) | MongoDB (NoSQL) |
|---------|------------|-----------------|
| **Modelo de datos** | Tablas relacionales | Documentos JSON (BSON) |
| **ID** | Long autogenerado | String ObjectId (24 chars hex) |
| **Mapeo** | ResultSet manual | Document ↔ POJO |
| **Queries** | SQL strings | Filters / Criteria |
| **Joins** | JOIN nativo | $lookup o documentos embebidos |
| **Índices** | CREATE INDEX | @Indexed o createIndex() |
| **Transacciones** | setAutoCommit(false) | ClientSession (replica sets) |
| **Abstracción ORM** | JPA/Hibernate | Spring Data MongoDB |

---

## 📂 Estructura de Archivos Generados

```
proyecto-pedagogico-mongodb/
├── build.gradle                    # Dependencias
├── settings.gradle
├── .gitignore
├── README.md                       # Este archivo
├── ARQUITECTURA.md                 # Documentación técnica
└── src/
    ├── main/
    │   ├── java/com/dam/accesodatos/
    │   │   ├── MongoDbTeachingApplication.java
    │   │   ├── config/
    │   │   │   ├── MongoConfig.java
    │   │   │   └── DataInitializer.java
    │   │   ├── model/
    │   │   │   ├── User.java
    │   │   │   ├── UserCreateDto.java
    │   │   │   ├── UserUpdateDto.java
    │   │   │   └── UserQueryDto.java
    │   │   ├── mongodb/
    │   │   │   ├── nativeapi/
    │   │   │   │   ├── NativeMongoUserService.java
    │   │   │   │   └── NativeMongoUserServiceImpl.java
    │   │   │   └── springdata/
    │   │   │       ├── UserRepository.java
    │   │   │       ├── SpringDataUserService.java
    │   │   │       └── SpringDataUserServiceImpl.java
    │   │   └── controller/
    │   │       ├── NativeMongoController.java
    │   │       └── SpringDataController.java
    │   └── resources/
    │       └── application.yml
    └── test/
        └── java/com/dam/accesodatos/mongodb/
            ├── nativeapi/NativeMongoUserServiceTest.java
            └── springdata/SpringDataUserServiceTest.java
```

---

## 📞 Soporte y Contribuciones

- **Consultar con el profesor** sobre conceptos de MongoDB
- **Revisar los 10 ejemplos implementados** antes de preguntar
- **Leer las instrucciones detalladas** en cada método TODO
- **Ejecutar tests** para validar tu implementación

---

## 📜 Licencia

Proyecto educativo para uso académico - DAM (Desarrollo de Aplicaciones Multiplataforma)

**Versión**: 1.0.0  
**Autor**: Proyecto Pedagógico RA4  
**Fecha**: 2024

---

¡Buen aprendizaje con MongoDB! 🚀📚
