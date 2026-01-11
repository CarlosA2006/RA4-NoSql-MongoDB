# Semana 3: Implementación de TODOs - API Nativa + Defensa Spring Data

## Información de la Sesión

| Aspecto | Detalle |
|---------|---------|
| **Duración** | 5 horas |
| **Defensa oral** | 3 métodos Spring Data (semana anterior) |
| **Objetivo principal** | Implementar 3 métodos TODO con API Nativa |
| **Entregable** | `findAll()`, `findByDepartment()`, `countByDepartment()` (API Nativa) |

---

## Objetivos de Aprendizaje

Al finalizar esta semana, el alumnado será capaz de:

1. Defender oralmente los métodos implementados en Spring Data.
2. Explicar las diferencias entre MongoCollection y MongoRepository.
3. Utilizar la clase Document para representar documentos BSON.
4. Aplicar filtros con la clase Filters de MongoDB.
5. Iterar resultados con MongoCursor o FindIterable.
6. Mapear documentos a objetos Java manualmente.

---

## Métodos a Implementar

| Método | Complejidad | Líneas de código | Tiempo estimado |
|--------|-------------|------------------|-----------------|
| `findAll()` | Media | ~8-10 líneas | 25-30 min |
| `findByDepartment()` | Media | ~8-10 líneas | 25-30 min |
| `countByDepartment()` | Baja | ~3-4 líneas | 10-15 min |

---

## Temporización Detallada

### Bloque 1: Defensa Oral - Spring Data (1 hora)

#### Organización de la Defensa (5 min)

- Orden de defensa: alfabético, voluntarios primero, o aleatorio
- Cada alumno dispone de 3-5 minutos
- El docente puede hacer 1-2 preguntas adicionales

#### Dinámica de la Defensa

**Paso 1: Mostrar Código (30 segundos)**
El alumno muestra su implementación en pantalla o comparte código.

**Paso 2: Explicar Cada Método (2-3 min)**

Para `findAll()`:
- ¿Qué método del repositorio usas?
- ¿De dónde viene ese método?
- ¿Qué tipo de dato devuelve?

Para `findByDepartment()`:
- ¿Cómo sabe Spring Data qué consulta generar?
- ¿Qué pasa si el departamento no existe?
- ¿Dónde está definido este método?

Para `countByDepartment()`:
- ¿Por qué es más eficiente que `findByDepartment().size()`?
- ¿Qué tipo devuelve?

**Paso 3: Preguntas del Docente (1-2 min)**

Ejemplos de preguntas:
- "¿Podrías añadir un query method para buscar por email?"
- "¿Qué consulta MongoDB se ejecuta internamente?"
- "¿Qué ventajas tiene Spring Data sobre la API nativa?"

#### Registro de Evaluación

| Alumno | findAll | findByDept | countByDept | Preguntas | Nota |
|--------|---------|------------|-------------|-----------|------|
| | /10 | /10 | /10 | /10 | |

#### Feedback Colectivo (15 min)

Tras todas las defensas:
- Errores comunes observados
- Buenas prácticas destacables
- Puntos de mejora generales

---

### Bloque 2: Repaso y Transición (30 min)

#### Diferencias API Nativa vs Spring Data (15 min)

| Aspecto | Spring Data | API Nativa |
|---------|-------------|------------|
| Abstracción | Alta | Baja |
| Control | Menor | Total |
| Líneas de código | Menos | Más |
| Curva aprendizaje | Suave | Pronunciada |
| Flexibilidad | Media | Alta |
| Mapeo objetos | Automático | Manual |

#### ¿Por Qué Aprender la API Nativa? (15 min)

1. **Comprensión profunda**: Entender qué hace Spring Data internamente
2. **Casos especiales**: Operaciones que Spring Data no soporta fácilmente
3. **Rendimiento**: Optimizaciones de bajo nivel
4. **Debugging**: Saber qué buscar cuando algo falla
5. **Portabilidad**: Conocimiento transferible a otros lenguajes

---

### Bloque 3: Teoría - API Nativa MongoDB (45 min)

#### Componentes Principales (15 min)

```
┌─────────────────────────────────────────────────────────┐
│                    MongoClient                          │
│        Conexión a la instancia de MongoDB               │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                   MongoDatabase                         │
│              Base de datos específica                   │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│              MongoCollection<Document>                  │
│        Colección de documentos (como una tabla)         │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                    Document                             │
│         Documento BSON (como una fila/registro)         │
└─────────────────────────────────────────────────────────┘
```

#### La Clase Document (15 min)

`Document` es la representación Java de un documento BSON:

```java
// Crear un documento
Document doc = new Document()
    .append("name", "Ana García")
    .append("email", "ana@empresa.com")
    .append("department", "IT")
    .append("active", true);

// Leer campos de un documento
String name = doc.getString("name");
Boolean active = doc.getBoolean("active");
ObjectId id = doc.getObjectId("_id");

// Los documentos son como mapas
// doc.get("name") devuelve Object, hay que castear
```

#### Clase Filters para Consultas (15 min)

`Filters` proporciona métodos estáticos para crear filtros:

```java
import static com.mongodb.client.model.Filters.*;

// Igualdad
Bson filter1 = eq("department", "IT");
// → { "department": "IT" }

// Comparación
Bson filter2 = gt("age", 25);
// → { "age": { "$gt": 25 } }

// Combinación AND
Bson filter3 = and(eq("department", "IT"), eq("active", true));
// → { "$and": [{ "department": "IT" }, { "active": true }] }

// Combinación OR
Bson filter4 = or(eq("department", "IT"), eq("department", "HR"));

// Regex
Bson filter5 = regex("name", "García", "i");
// → { "name": { "$regex": "García", "$options": "i" } }
```

---

### Bloque 4: Análisis del Código Existente (30 min)

#### Abrir NativeMongoUserServiceImpl.java

Ubicación: `src/main/java/com/dam/accesodatos/mongodb/nativeapi/NativeMongoUserServiceImpl.java`

#### Analizar findUserById() (15 min)

```java
@Override
public User findUserById(String id) {
    log.debug("Buscando usuario por ID: {}", id);

    // 1. Validar y convertir el ID
    ObjectId objectId;
    try {
        objectId = new ObjectId(id);
    } catch (IllegalArgumentException e) {
        log.warn("ID inválido: {}", id);
        throw new InvalidUserIdException(id);
    }

    // 2. Obtener la colección
    MongoCollection<Document> collection = getCollection();

    // 3. Crear el filtro
    Bson filter = Filters.eq("_id", objectId);

    // 4. Ejecutar la consulta
    Document doc = collection.find(filter).first();

    // 5. Verificar resultado
    if (doc == null) {
        log.warn("Usuario no encontrado: {}", id);
        throw new UserNotFoundException(id);
    }

    // 6. Mapear Document a User
    User user = mapDocumentToUser(doc);
    log.debug("Usuario encontrado: {}", user.getEmail());
    return user;
}
```

**Puntos clave:**
- `getCollection()` obtiene la colección "users"
- `Filters.eq()` crea un filtro de igualdad
- `.find(filter).first()` ejecuta la consulta y obtiene el primer resultado
- `mapDocumentToUser()` convierte el Document a objeto User

#### Analizar mapDocumentToUser() (15 min)

```java
private User mapDocumentToUser(Document doc) {
    User user = new User();
    user.setId(doc.getObjectId("_id").toString());
    user.setName(doc.getString("name"));
    user.setEmail(doc.getString("email"));
    user.setDepartment(doc.getString("department"));
    user.setRole(doc.getString("role"));
    user.setActive(doc.getBoolean("active", false));

    // Manejo de fechas
    Date createdAt = doc.getDate("createdAt");
    if (createdAt != null) {
        user.setCreatedAt(createdAt.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime());
    }
    // ... similar para updatedAt

    return user;
}
```

**Notar:**
- Cada campo se extrae manualmente
- Hay que hacer casting de tipos
- Las fechas requieren conversión especial
- `getBoolean("active", false)` proporciona valor por defecto

---

### Bloque 5: Práctica - Implementar los 3 TODOs (1h 30min)

#### TODO 1: findAll() (30 min)

**Ubicación:** Buscar `// TODO` en el archivo

**Pistas del código:**
```java
// TODO: Implementar findAll() - Los estudiantes deben completar este método
// PISTAS:
// 1. Obtener la colección con getCollection()
// 2. Usar collection.find() sin filtros para obtener todos
// 3. Iterar con for-each o MongoCursor
// 4. Mapear cada Document a User con mapDocumentToUser()
// 5. Añadir cada User a una lista
// 6. Retornar la lista
```

**Solución:**
```java
@Override
public List<User> findAll() {
    log.debug("Obteniendo todos los usuarios con API Nativa");

    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();

    // find() sin argumentos devuelve todos los documentos
    for (Document doc : collection.find()) {
        users.add(mapDocumentToUser(doc));
    }

    log.debug("Encontrados {} usuarios", users.size());
    return users;
}
```

**Alternativa con FindIterable:**
```java
@Override
public List<User> findAll() {
    log.debug("Obteniendo todos los usuarios con API Nativa");

    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();

    FindIterable<Document> results = collection.find();
    for (Document doc : results) {
        users.add(mapDocumentToUser(doc));
    }

    return users;
}
```

**Explicación para la defensa:**
- `collection.find()` sin parámetros equivale a `db.users.find({})` en MongoDB shell
- El resultado es un `FindIterable<Document>` que podemos iterar
- Por cada documento, usamos `mapDocumentToUser()` para convertirlo
- Acumulamos los usuarios en una lista

#### TODO 2: findUsersByDepartment() (30 min)

**Solución:**
```java
@Override
public List<User> findUsersByDepartment(String department) {
    log.debug("Buscando usuarios del departamento: {}", department);

    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();

    // Crear filtro de igualdad
    Bson filter = Filters.eq("department", department);

    // Ejecutar consulta con filtro
    for (Document doc : collection.find(filter)) {
        users.add(mapDocumentToUser(doc));
    }

    log.debug("Encontrados {} usuarios en {}", users.size(), department);
    return users;
}
```

**Explicación para la defensa:**
- `Filters.eq("department", department)` crea el filtro `{ "department": "valor" }`
- `collection.find(filter)` aplica el filtro a la consulta
- El resto es igual que `findAll()`: iterar y mapear
- Es el equivalente manual de `findByDepartment()` de Spring Data

#### TODO 3: countByDepartment() (15 min)

**Solución:**
```java
@Override
public long countByDepartment(String department) {
    log.debug("Contando usuarios del departamento: {}", department);

    MongoCollection<Document> collection = getCollection();
    Bson filter = Filters.eq("department", department);

    long count = collection.countDocuments(filter);

    log.debug("Encontrados {} usuarios en {}", count, department);
    return count;
}
```

**Explicación para la defensa:**
- `countDocuments(filter)` cuenta sin cargar los documentos en memoria
- Es mucho más eficiente que `find(filter)` + contar en Java
- Equivale a `db.users.countDocuments({ department: "IT" })` en MongoDB shell

#### Tiempo de Implementación Individual (15 min restantes)

Los alumnos completan cualquier método pendiente.

---

### Bloque 6: Tests y Comparativa (45 min)

#### Ejecutar Tests de API Nativa (20 min)

```bash
./gradlew test --tests "*Native*"
```

**Verificar que pasan:**
```
NativeMongoUserServiceTest > findAllTest() PASSED
NativeMongoUserServiceTest > findUsersByDepartmentTest() PASSED
NativeMongoUserServiceTest > countByDepartmentTest() PASSED
```

#### Comparativa de Código (20 min)

Mostrar lado a lado las dos implementaciones de `findAll()`:

**Spring Data:**
```java
public List<User> findAll() {
    return userRepository.findAll();
}
```

**API Nativa:**
```java
public List<User> findAll() {
    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();
    for (Document doc : collection.find()) {
        users.add(mapDocumentToUser(doc));
    }
    return users;
}
```

**Discusión:**
- Spring Data: 1 línea vs API Nativa: ~8 líneas
- Spring Data oculta la iteración y el mapeo
- API Nativa te da control total del proceso
- ¿Cuándo preferirías cada uno?

#### Preparación para la Defensa de la Próxima Semana (5 min)

La semana 4 defenderéis estos 3 métodos de API Nativa. Preparad:
- Explicar el rol de cada clase (MongoCollection, Document, Filters)
- Comparar con la versión de Spring Data
- Poder responder: "¿Por qué tantas líneas más?"

---

## Soluciones Completas

### findAll()
```java
@Override
public List<User> findAll() {
    log.debug("Obteniendo todos los usuarios con API Nativa");

    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();

    for (Document doc : collection.find()) {
        users.add(mapDocumentToUser(doc));
    }

    log.debug("Encontrados {} usuarios", users.size());
    return users;
}
```

### findUsersByDepartment()
```java
@Override
public List<User> findUsersByDepartment(String department) {
    log.debug("Buscando usuarios del departamento: {}", department);

    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();

    Bson filter = Filters.eq("department", department);

    for (Document doc : collection.find(filter)) {
        users.add(mapDocumentToUser(doc));
    }

    log.debug("Encontrados {} usuarios en {}", users.size(), department);
    return users;
}
```

### countByDepartment()
```java
@Override
public long countByDepartment(String department) {
    log.debug("Contando usuarios del departamento: {}", department);

    MongoCollection<Document> collection = getCollection();
    Bson filter = Filters.eq("department", department);

    long count = collection.countDocuments(filter);

    log.debug("Encontrados {} usuarios en {}", count, department);
    return count;
}
```

---

## Ejercicios de Ampliación

### Ejercicio 1: findByActive()
Implementar un método que devuelva solo usuarios activos:
```java
public List<User> findActiveUsers() {
    // Usar Filters.eq("active", true)
}
```

### Ejercicio 2: Combinar Filtros
Implementar búsqueda por departamento Y activos:
```java
public List<User> findActiveByDepartment(String department) {
    // Usar Filters.and(...)
}
```

### Ejercicio 3: Ordenación
Añadir ordenación por nombre:
```java
collection.find(filter).sort(Sorts.ascending("name"));
```

---

## Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| `ClassCastException` | Tipo incorrecto en `doc.get()` | Usar `doc.getString()`, `doc.getBoolean()`, etc. |
| Lista vacía inesperada | Filtro incorrecto | Verificar nombre exacto del campo ("department" vs "Department") |
| `NullPointerException` en mapeo | Campo null en documento | Usar `doc.getString("field")` que devuelve null si no existe |
| Import incorrecto de Filters | Múltiples clases Filters | Usar `com.mongodb.client.model.Filters` |

---

## Imports Necesarios

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
```

---

## Checklist de Fin de Semana

- [ ] He defendido los 3 métodos de Spring Data
- [ ] Los 3 métodos de API Nativa están implementados
- [ ] `./gradlew test --tests "*Native*"` pasa todos los tests
- [ ] Puedo explicar la diferencia entre `find()` y `countDocuments()`
- [ ] Entiendo cómo funciona `mapDocumentToUser()`
- [ ] Tengo notas preparadas para la defensa de la semana 4
