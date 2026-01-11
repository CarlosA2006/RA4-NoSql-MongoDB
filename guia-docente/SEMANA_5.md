# Semana 5: searchUsers() API Nativa + Defensa Spring Data

## Información de la Sesión

| Aspecto | Detalle |
|---------|---------|
| **Duración** | 5 horas |
| **Defensa oral** | searchUsers() Spring Data (semana anterior) |
| **Objetivo principal** | Implementar searchUsers() con API Nativa |
| **Entregable** | Proyecto completo - Todos los TODOs implementados |

---

## Objetivos de Aprendizaje

Al finalizar esta semana, el alumnado será capaz de:

1. Defender el método searchUsers() de Spring Data explicando los criterios dinámicos.
2. Implementar filtros dinámicos con la API nativa usando Filters.
3. Aplicar paginación nativa con skip() y limit() en FindIterable.
4. Configurar ordenamiento con Sorts en consultas nativas.
5. Comparar la complejidad de ambas implementaciones.
6. Completar todos los tests del proyecto (10/10).

---

## Método a Implementar

| Método | Complejidad | Líneas de código | Tiempo estimado |
|--------|-------------|------------------|-----------------|
| `searchUsers()` | Alta | ~30-35 líneas | 1.5-2 horas |

---

## Temporización Detallada

### Bloque 1: Defensa Oral - searchUsers() Spring Data (1 hora)

#### Características de esta Defensa

Esta defensa es más exigente porque el método es más complejo:
- Tiempo por alumno: 4-5 minutos
- Se espera mayor profundidad en las explicaciones
- Preguntas más técnicas

#### Puntos a Evaluar

**Estructura del Código:**
- ¿Por qué se usa una lista de Criteria?
- ¿Cómo se combinan los criterios con andOperator?
- ¿Qué pasa si la lista está vacía?

**Filtros Dinámicos:**
- ¿Por qué se verifica null Y vacío para Strings?
- ¿Por qué el filtro de nombre usa regex y los demás no?
- ¿Qué significa la "i" en el regex?

**Paginación y Ordenamiento:**
- ¿Qué diferencia hay entre skip y limit?
- ¿Por qué se usan valores por defecto?
- ¿Cómo funciona Sort.Direction?

#### Preguntas Tipo (Nivel Avanzado)

- "¿Qué pasaría si usaras `orOperator` en lugar de `andOperator`?"
- "¿Cómo añadirías un filtro para buscar usuarios creados después de una fecha?"
- "¿Qué consulta MongoDB se genera con estos filtros: name='Ana', active=true?"
- "¿Por qué MongoTemplate y no UserRepository para este método?"

#### Registro de Evaluación

| Alumno | Funciona | Criterios | Paginación | Preguntas | Nota |
|--------|----------|-----------|------------|-----------|------|
| | /10 | /10 | /10 | /10 | |

#### Feedback Colectivo (15 min)

- Patrones comunes en las soluciones
- Errores típicos observados
- Mejores prácticas destacables

---

### Bloque 2: Repaso - Filters Avanzados (30 min)

#### Recordatorio: Filters Básicos (10 min)

```java
import static com.mongodb.client.model.Filters.*;

// Ya conocidos
Bson eq = eq("department", "IT");      // Igualdad
Bson gt = gt("age", 25);               // Mayor que
Bson and = and(filter1, filter2);      // Combinación AND
```

#### Filters para searchUsers() (20 min)

**Regex para Búsqueda Parcial:**
```java
// Buscar nombres que contengan "garcía" (case-insensitive)
Bson nameFilter = regex("name", "garcía", "i");
// → { "name": { "$regex": "garcía", "$options": "i" } }

// Alternativa con Pattern
import java.util.regex.Pattern;
Bson nameFilter = regex("name", Pattern.compile("garcía", Pattern.CASE_INSENSITIVE));
```

**Filtros Opcionales (patrón):**
```java
List<Bson> filters = new ArrayList<>();

if (name != null && !name.isEmpty()) {
    filters.add(regex("name", name, "i"));
}
if (department != null && !department.isEmpty()) {
    filters.add(eq("department", department));
}
if (active != null) {
    filters.add(eq("active", active));
}

// Combinar solo si hay filtros
Bson finalFilter = filters.isEmpty()
    ? new Document()  // Sin filtro (todos los documentos)
    : and(filters);   // Combina con AND
```

---

### Bloque 3: Teoría - Paginación y Ordenamiento Nativo (45 min)

#### FindIterable: El Resultado de find() (15 min)

```java
MongoCollection<Document> collection = getCollection();

// find() devuelve FindIterable<Document>
FindIterable<Document> results = collection.find();

// FindIterable es "lazy" - no ejecuta hasta que iteramos
// Podemos encadenar operaciones antes de ejecutar
```

#### Paginación con skip() y limit() (15 min)

```java
FindIterable<Document> results = collection.find(filter)
    .skip(20)    // Saltar los primeros 20 documentos
    .limit(10);  // Devolver máximo 10

// Equivale a SQL: OFFSET 20 LIMIT 10
// Equivale a MongoDB shell: db.users.find().skip(20).limit(10)
```

**Importante:** El orden de skip/limit no importa en la API, pero conceptualmente:
1. Se aplica el filtro
2. Se ordenan los resultados
3. Se saltan N documentos (skip)
4. Se devuelven M documentos (limit)

#### Ordenamiento con Sorts (15 min)

```java
import com.mongodb.client.model.Sorts;

// Ordenar por un campo ascendente
FindIterable<Document> results = collection.find()
    .sort(Sorts.ascending("name"));

// Ordenar descendente
FindIterable<Document> results = collection.find()
    .sort(Sorts.descending("createdAt"));

// Múltiples campos
FindIterable<Document> results = collection.find()
    .sort(Sorts.orderBy(
        Sorts.ascending("department"),
        Sorts.descending("name")
    ));
```

**Construir Sort dinámicamente:**
```java
Bson sortOrder = "desc".equalsIgnoreCase(sortDirection)
    ? Sorts.descending(sortBy)
    : Sorts.ascending(sortBy);
```

---

### Bloque 4: Comparativa Spring Data vs API Nativa (30 min)

#### Lado a Lado: Filtro por Nombre

**Spring Data:**
```java
Criteria.where("name").regex(queryDto.getName(), "i")
```

**API Nativa:**
```java
Filters.regex("name", queryDto.getName(), "i")
```

#### Lado a Lado: Combinación de Filtros

**Spring Data:**
```java
new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))
```

**API Nativa:**
```java
Filters.and(filtersList)
```

#### Lado a Lado: Paginación

**Spring Data:**
```java
query.skip(offset);
query.limit(limit);
```

**API Nativa:**
```java
findIterable.skip(offset).limit(limit);
```

#### Lado a Lado: Ordenamiento

**Spring Data:**
```java
query.with(Sort.by(Sort.Direction.ASC, "name"));
```

**API Nativa:**
```java
findIterable.sort(Sorts.ascending("name"));
```

**Conclusión:** Los conceptos son idénticos, solo cambia la sintaxis.

---

### Bloque 5: Práctica - Implementar searchUsers() API Nativa (1h 30min)

#### Estructura Base (15 min)

```java
@Override
public List<User> searchUsers(UserQueryDto queryDto) {
    log.debug("Buscando usuarios con API Nativa: {}", queryDto);

    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();

    // 1. Construir filtros
    // 2. Configurar consulta
    // 3. Ejecutar y mapear

    return users;
}
```

#### Paso 1: Construir Filtros Dinámicos (25 min)

```java
List<Bson> filters = new ArrayList<>();

// Filtro por nombre (búsqueda parcial)
if (queryDto.getName() != null && !queryDto.getName().trim().isEmpty()) {
    filters.add(Filters.regex("name", queryDto.getName(), "i"));
}

// Filtro por email (exacto)
if (queryDto.getEmail() != null && !queryDto.getEmail().trim().isEmpty()) {
    filters.add(Filters.eq("email", queryDto.getEmail()));
}

// Filtro por departamento (exacto)
if (queryDto.getDepartment() != null && !queryDto.getDepartment().trim().isEmpty()) {
    filters.add(Filters.eq("department", queryDto.getDepartment()));
}

// Filtro por rol (exacto)
if (queryDto.getRole() != null && !queryDto.getRole().trim().isEmpty()) {
    filters.add(Filters.eq("role", queryDto.getRole()));
}

// Filtro por active
if (queryDto.getActive() != null) {
    filters.add(Filters.eq("active", queryDto.getActive()));
}
```

#### Paso 2: Determinar el Filtro Final (10 min)

```java
// Si no hay filtros, usar documento vacío (sin filtro)
// Si hay filtros, combinarlos con AND
Bson finalFilter = filters.isEmpty()
    ? new Document()
    : Filters.and(filters);
```

#### Paso 3: Configurar Paginación (15 min)

```java
int offset = queryDto.getOffset() != null ? queryDto.getOffset() : 0;
int limit = queryDto.getLimit() != null ? queryDto.getLimit() : 20;
```

#### Paso 4: Configurar Ordenamiento (15 min)

```java
String sortBy = queryDto.getSortBy() != null ? queryDto.getSortBy() : "name";
Bson sortOrder = "desc".equalsIgnoreCase(queryDto.getSortDirection())
    ? Sorts.descending(sortBy)
    : Sorts.ascending(sortBy);
```

#### Paso 5: Ejecutar y Mapear (10 min)

```java
FindIterable<Document> results = collection.find(finalFilter)
    .sort(sortOrder)
    .skip(offset)
    .limit(limit);

for (Document doc : results) {
    users.add(mapDocumentToUser(doc));
}

log.debug("Encontrados {} usuarios", users.size());
return users;
```

---

### Bloque 6: Tests Completos y Cierre (45 min)

#### Ejecutar Todos los Tests (15 min)

```bash
./gradlew test
```

**Resultado esperado:**
```
NativeMongoUserServiceTest
  ✓ testConnectionTest
  ✓ createUserTest
  ✓ findUserByIdTest
  ✓ updateUserTest
  ✓ deleteUserTest
  ✓ findAllTest
  ✓ findUsersByDepartmentTest
  ✓ countByDepartmentTest
  ✓ searchUsersTest

SpringDataUserServiceTest
  ✓ testConnectionTest
  ✓ createUserTest
  ✓ findUserByIdTest
  ✓ updateUserTest
  ✓ deleteUserTest
  ✓ findAllTest
  ✓ findUsersByDepartmentTest
  ✓ countByDepartmentTest
  ✓ searchUsersTest

18 tests completed, 0 failed
```

#### Pruebas en Swagger (15 min)

Probar `POST /api/native/users/search` con diferentes combinaciones:

```json
// Todos los filtros
{
  "name": "García",
  "department": "IT",
  "active": true,
  "offset": 0,
  "limit": 5,
  "sortBy": "name",
  "sortDirection": "asc"
}
```

Comparar resultados con el endpoint de Spring Data.

#### Preparación para la Defensa Final (10 min)

La semana 6 defenderéis searchUsers() de API Nativa. Preparad:
- Explicar las diferencias con la versión Spring Data
- Justificar cada decisión de diseño
- Poder modificar el código en vivo si se pide

#### Resumen del Progreso (5 min)

**Métodos completados:**

| Módulo | Método | Estado |
|--------|--------|--------|
| Spring Data | findAll() | ✅ |
| Spring Data | findByDepartment() | ✅ |
| Spring Data | countByDepartment() | ✅ |
| Spring Data | searchUsers() | ✅ |
| API Nativa | findAll() | ✅ |
| API Nativa | findByDepartment() | ✅ |
| API Nativa | countByDepartment() | ✅ |
| API Nativa | searchUsers() | ✅ |

**¡Todos los TODOs completados!**

---

## Solución Completa con Comentarios

```java
@Override
public List<User> searchUsers(UserQueryDto queryDto) {
    log.debug("Buscando usuarios con API Nativa: {}", queryDto);

    // 1. Obtener colección
    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();

    // 2. CONSTRUIR FILTROS DINÁMICOS
    List<Bson> filters = new ArrayList<>();

    // Filtro por nombre - usa regex para búsqueda parcial
    // La "i" significa case-insensitive
    if (queryDto.getName() != null && !queryDto.getName().trim().isEmpty()) {
        filters.add(Filters.regex("name", queryDto.getName(), "i"));
    }

    // Filtro por email - búsqueda exacta
    if (queryDto.getEmail() != null && !queryDto.getEmail().trim().isEmpty()) {
        filters.add(Filters.eq("email", queryDto.getEmail()));
    }

    // Filtro por departamento - búsqueda exacta
    if (queryDto.getDepartment() != null && !queryDto.getDepartment().trim().isEmpty()) {
        filters.add(Filters.eq("department", queryDto.getDepartment()));
    }

    // Filtro por rol - búsqueda exacta
    if (queryDto.getRole() != null && !queryDto.getRole().trim().isEmpty()) {
        filters.add(Filters.eq("role", queryDto.getRole()));
    }

    // Filtro por active - solo si se especifica (puede ser true o false)
    if (queryDto.getActive() != null) {
        filters.add(Filters.eq("active", queryDto.getActive()));
    }

    // 3. COMBINAR FILTROS
    // Si no hay filtros, usamos documento vacío (sin filtro = todos)
    // Si hay filtros, los combinamos con AND
    Bson finalFilter = filters.isEmpty()
        ? new Document()
        : Filters.and(filters);

    // 4. CONFIGURAR PAGINACIÓN
    int offset = queryDto.getOffset() != null ? queryDto.getOffset() : 0;
    int limit = queryDto.getLimit() != null ? queryDto.getLimit() : 20;

    // 5. CONFIGURAR ORDENAMIENTO
    String sortBy = queryDto.getSortBy() != null ? queryDto.getSortBy() : "name";
    Bson sortOrder = "desc".equalsIgnoreCase(queryDto.getSortDirection())
        ? Sorts.descending(sortBy)
        : Sorts.ascending(sortBy);

    // 6. EJECUTAR CONSULTA
    // El orden de las operaciones: find → sort → skip → limit
    FindIterable<Document> results = collection.find(finalFilter)
        .sort(sortOrder)
        .skip(offset)
        .limit(limit);

    // 7. MAPEAR RESULTADOS
    for (Document doc : results) {
        users.add(mapDocumentToUser(doc));
    }

    log.debug("Encontrados {} usuarios", users.size());
    return users;
}
```

---

## Imports Necesarios

```java
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
```

---

## Comparativa Final: Líneas de Código

| Aspecto | Spring Data | API Nativa |
|---------|-------------|------------|
| Filtros dinámicos | ~12 líneas | ~15 líneas |
| Combinación | 3 líneas | 3 líneas |
| Paginación | 4 líneas | 4 líneas |
| Ordenamiento | 4 líneas | 4 líneas |
| Ejecución | 1 línea | 5 líneas |
| **Total** | ~24 líneas | ~31 líneas |

La diferencia no es tan grande como en métodos simples porque la lógica de filtros dinámicos es similar en ambos casos.

---

## Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| `Filters.and()` con lista vacía falla | No se puede hacer AND de cero elementos | Verificar si la lista está vacía antes |
| Ordenamiento no funciona | Campo no existe o nombre incorrecto | Verificar nombre exacto del campo |
| Regex muy lento | Patrón complejo en colección grande | Simplificar patrón o usar índices |
| `ClassCastException` en mapeo | Campo con tipo inesperado | Verificar tipo antes de castear |

---

## Ejercicios de Ampliación

### Ejercicio 1: Proyección
Devolver solo ciertos campos para mejorar rendimiento:
```java
collection.find(filter).projection(
    Projections.include("name", "email", "department")
);
```

### Ejercicio 2: Índice de Texto
Crear índice de texto y usar `$text` para búsqueda:
```java
Filters.text("palabra a buscar")
```

### Ejercicio 3: Consulta con $or
Modificar para buscar en nombre O email:
```java
Filters.or(
    Filters.regex("name", searchTerm, "i"),
    Filters.regex("email", searchTerm, "i")
)
```

---

## Checklist de Fin de Semana

- [ ] He defendido searchUsers() de Spring Data
- [ ] searchUsers() de API Nativa está implementado
- [ ] `./gradlew test` pasa los 18 tests (10 nativa + 8 spring)
- [ ] He comparado ambas implementaciones
- [ ] Entiendo Filters.and(), Sorts, skip(), limit()
- [ ] Tengo notas para la defensa final de la semana 6
- [ ] **Todos los TODOs del proyecto están completados**
