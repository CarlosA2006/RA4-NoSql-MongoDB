# Semana 4: searchUsers() Spring Data + Defensa API Nativa

## Información de la Sesión

| Aspecto | Detalle |
|---------|---------|
| **Duración** | 5 horas |
| **Defensa oral** | 3 métodos API Nativa (semana anterior) |
| **Objetivo principal** | Implementar searchUsers() con filtros dinámicos |
| **Entregable** | `searchUsers()` en Spring Data funcionando |

---

## Objetivos de Aprendizaje

Al finalizar esta semana, el alumnado será capaz de:

1. Defender oralmente los métodos de API Nativa implementados.
2. Utilizar MongoTemplate para consultas complejas en Spring Data.
3. Construir criterios de búsqueda dinámicos con la clase Criteria.
4. Implementar paginación con skip() y limit().
5. Aplicar ordenamiento con Sort.
6. Combinar múltiples condiciones de filtrado opcionales.

---

## Método a Implementar

| Método | Complejidad | Líneas de código | Tiempo estimado |
|--------|-------------|------------------|-----------------|
| `searchUsers()` | Alta | ~20-25 líneas | 1.5-2 horas |

Este es el método más complejo de Spring Data porque requiere:
- Filtros dinámicos (solo aplicar si el parámetro no es null)
- Búsqueda parcial con regex (para el nombre)
- Paginación (offset y limit)
- Ordenamiento configurable

---

## Temporización Detallada

### Bloque 1: Defensa Oral - API Nativa (1 hora)

#### Organización de la Defensa (5 min)

Misma dinámica que la semana anterior.

#### Contenido de la Defensa

Para cada alumno (3-5 min):

**findAll() API Nativa:**
- ¿Qué devuelve `collection.find()` sin parámetros?
- ¿Por qué necesitas el bucle for?
- ¿Qué hace `mapDocumentToUser()`?

**findUsersByDepartment() API Nativa:**
- ¿Qué es un `Bson`?
- ¿Cómo se usa `Filters.eq()`?
- ¿Cuál es el equivalente en MongoDB shell?

**countByDepartment() API Nativa:**
- ¿Por qué `countDocuments()` es más eficiente?
- ¿Qué tipo devuelve?
- Compara con la versión Spring Data.

#### Preguntas Tipo

- "Muéstrame cómo añadirías un filtro por `active = true`"
- "¿Qué import necesitas para Filters?"
- "¿Qué pasa si el campo no existe en el documento?"

#### Registro de Evaluación

| Alumno | findAll | findByDept | countByDept | Preguntas | Nota |
|--------|---------|------------|-------------|-----------|------|
| | /10 | /10 | /10 | /10 | |

#### Feedback Colectivo (15 min)

- Comparativa general con Spring Data
- Errores comunes en el mapeo manual
- Buenas prácticas de logging

---

### Bloque 2: Introducción a searchUsers() (30 min)

#### ¿Qué es searchUsers()? (10 min)

Es un método de búsqueda avanzada que permite:

```
┌─────────────────────────────────────────────────────────────┐
│                    UserQueryDto                             │
├─────────────────────────────────────────────────────────────┤
│  name: String (opcional)       → Búsqueda parcial           │
│  email: String (opcional)      → Búsqueda exacta            │
│  department: String (opcional) → Búsqueda exacta            │
│  role: String (opcional)       → Búsqueda exacta            │
│  active: Boolean (opcional)    → Filtro booleano            │
│  offset: Integer               → Saltar N resultados        │
│  limit: Integer                → Máximo N resultados        │
│  sortBy: String                → Campo para ordenar         │
│  sortDirection: String         → "asc" o "desc"             │
└─────────────────────────────────────────────────────────────┘
```

#### Ejemplos de Uso (10 min)

**Buscar usuarios de IT activos:**
```json
{
  "department": "IT",
  "active": true
}
```

**Buscar por nombre parcial, página 2, 10 resultados:**
```json
{
  "name": "García",
  "offset": 10,
  "limit": 10,
  "sortBy": "name",
  "sortDirection": "asc"
}
```

**Solo activos, ordenados por fecha de creación:**
```json
{
  "active": true,
  "sortBy": "createdAt",
  "sortDirection": "desc"
}
```

#### El Reto: Filtros Dinámicos (10 min)

El problema principal es que los filtros son **opcionales**:

```java
// NO podemos hacer esto (siempre filtraría por todos los campos)
Criteria criteria = Criteria.where("name").is(query.getName())
    .and("department").is(query.getDepartment())
    .and("active").is(query.getActive());

// Si query.getName() es null, buscaría { "name": null }
// ¡Incorrecto! Debemos ignorar campos null
```

**Solución: Construir criterios condicionalmente**
```java
List<Criteria> criteriaList = new ArrayList<>();

if (query.getName() != null) {
    criteriaList.add(Criteria.where("name").regex(query.getName(), "i"));
}
if (query.getDepartment() != null) {
    criteriaList.add(Criteria.where("department").is(query.getDepartment()));
}
// ... etc
```

---

### Bloque 3: Teoría - MongoTemplate y Criteria (45 min)

#### ¿Por Qué MongoTemplate? (10 min)

Los query methods de Spring Data son limitados:
- No soportan filtros opcionales fácilmente
- No combinan bien con paginación dinámica
- Son difíciles de parametrizar con Sort

`MongoTemplate` es la alternativa flexible:
- Permite construir consultas programáticamente
- Soporta cualquier combinación de filtros
- Control total sobre la ejecución

#### Anatomía de una Consulta con MongoTemplate (15 min)

```java
// 1. Crear criterios de búsqueda
Criteria criteria = Criteria.where("department").is("IT");

// 2. Crear objeto Query con los criterios
Query query = new Query(criteria);

// 3. Añadir paginación
query.skip(0);      // Saltar 0 documentos
query.limit(10);    // Máximo 10 resultados

// 4. Añadir ordenamiento
query.with(Sort.by(Sort.Direction.ASC, "name"));

// 5. Ejecutar
List<User> results = mongoTemplate.find(query, User.class);
```

#### La Clase Criteria (10 min)

```java
// Igualdad exacta
Criteria.where("department").is("IT")
// → { "department": "IT" }

// Regex (búsqueda parcial, case-insensitive)
Criteria.where("name").regex("garcía", "i")
// → { "name": { "$regex": "garcía", "$options": "i" } }

// Booleano
Criteria.where("active").is(true)
// → { "active": true }

// Combinar con AND
new Criteria().andOperator(criteria1, criteria2, criteria3)
// → { "$and": [ {...}, {...}, {...} ] }
```

#### Paginación y Ordenamiento (10 min)

```java
Query query = new Query();

// Paginación
query.skip(20);    // Saltar los primeros 20
query.limit(10);   // Devolver máximo 10

// Ordenamiento ascendente
query.with(Sort.by(Sort.Direction.ASC, "name"));

// Ordenamiento descendente
query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

// Múltiples campos de ordenamiento
query.with(Sort.by(
    Sort.Order.asc("department"),
    Sort.Order.desc("name")
));
```

---

### Bloque 4: Demostración Paso a Paso (30 min)

#### Abrir UserQueryDto

Ubicación: `src/main/java/com/dam/accesodatos/model/UserQueryDto.java`

```java
public class UserQueryDto {
    private String name;        // Búsqueda parcial
    private String email;       // Búsqueda exacta
    private String department;  // Búsqueda exacta
    private String role;        // Búsqueda exacta
    private Boolean active;     // Filtro booleano

    private Integer offset = 0;     // Paginación: desde
    private Integer limit = 20;     // Paginación: cantidad
    private String sortBy = "name"; // Ordenar por
    private String sortDirection = "asc"; // Dirección

    // getters y setters...
}
```

#### Construcción Incremental

**Paso 1: Estructura básica**
```java
@Override
public List<User> searchUsers(UserQueryDto queryDto) {
    log.debug("Buscando usuarios con filtros: {}", queryDto);

    // Aquí irá nuestra lógica
    Query query = new Query();

    return mongoTemplate.find(query, User.class);
}
```

**Paso 2: Añadir filtros dinámicos**
```java
List<Criteria> criteriaList = new ArrayList<>();

// Filtro por nombre (regex para búsqueda parcial)
if (queryDto.getName() != null && !queryDto.getName().isEmpty()) {
    criteriaList.add(Criteria.where("name").regex(queryDto.getName(), "i"));
}

// Filtro por departamento (exacto)
if (queryDto.getDepartment() != null && !queryDto.getDepartment().isEmpty()) {
    criteriaList.add(Criteria.where("department").is(queryDto.getDepartment()));
}

// Filtro por active (solo si no es null)
if (queryDto.getActive() != null) {
    criteriaList.add(Criteria.where("active").is(queryDto.getActive()));
}
```

**Paso 3: Combinar criterios**
```java
Query query = new Query();

if (!criteriaList.isEmpty()) {
    query.addCriteria(new Criteria().andOperator(
        criteriaList.toArray(new Criteria[0])
    ));
}
```

**Paso 4: Añadir paginación y ordenamiento**
```java
// Paginación
query.skip(queryDto.getOffset() != null ? queryDto.getOffset() : 0);
query.limit(queryDto.getLimit() != null ? queryDto.getLimit() : 20);

// Ordenamiento
String sortBy = queryDto.getSortBy() != null ? queryDto.getSortBy() : "name";
Sort.Direction direction = "desc".equalsIgnoreCase(queryDto.getSortDirection())
    ? Sort.Direction.DESC
    : Sort.Direction.ASC;
query.with(Sort.by(direction, sortBy));
```

---

### Bloque 5: Práctica Guiada (1h 30min)

#### Implementación con Apoyo (1h 15min)

El docente guía mientras los alumnos implementan en sus equipos:

**Paso 1 (15 min):** Crear la estructura básica
- Añadir logging inicial
- Crear Query vacío
- Ejecutar con mongoTemplate.find()
- Verificar que devuelve todos los usuarios

**Paso 2 (20 min):** Implementar filtro por nombre con regex
- Crear lista de criterios
- Añadir condición para name
- Probar con Swagger

**Paso 3 (20 min):** Añadir filtros por department y active
- Seguir el mismo patrón
- Combinar con andOperator
- Probar combinaciones en Swagger

**Paso 4 (20 min):** Implementar paginación y ordenamiento
- Añadir skip() y limit()
- Configurar Sort
- Probar con diferentes valores

#### Solución Completa

```java
@Override
public List<User> searchUsers(UserQueryDto queryDto) {
    log.debug("Buscando usuarios con filtros: {}", queryDto);

    List<Criteria> criteriaList = new ArrayList<>();

    // Filtro por nombre (búsqueda parcial, case-insensitive)
    if (queryDto.getName() != null && !queryDto.getName().trim().isEmpty()) {
        criteriaList.add(Criteria.where("name").regex(queryDto.getName(), "i"));
    }

    // Filtro por email (exacto)
    if (queryDto.getEmail() != null && !queryDto.getEmail().trim().isEmpty()) {
        criteriaList.add(Criteria.where("email").is(queryDto.getEmail()));
    }

    // Filtro por departamento (exacto)
    if (queryDto.getDepartment() != null && !queryDto.getDepartment().trim().isEmpty()) {
        criteriaList.add(Criteria.where("department").is(queryDto.getDepartment()));
    }

    // Filtro por rol (exacto)
    if (queryDto.getRole() != null && !queryDto.getRole().trim().isEmpty()) {
        criteriaList.add(Criteria.where("role").is(queryDto.getRole()));
    }

    // Filtro por active (solo si se especifica)
    if (queryDto.getActive() != null) {
        criteriaList.add(Criteria.where("active").is(queryDto.getActive()));
    }

    // Construir query
    Query query = new Query();

    // Combinar criterios con AND si hay alguno
    if (!criteriaList.isEmpty()) {
        query.addCriteria(new Criteria().andOperator(
            criteriaList.toArray(new Criteria[0])
        ));
    }

    // Paginación
    int offset = queryDto.getOffset() != null ? queryDto.getOffset() : 0;
    int limit = queryDto.getLimit() != null ? queryDto.getLimit() : 20;
    query.skip(offset);
    query.limit(limit);

    // Ordenamiento
    String sortBy = queryDto.getSortBy() != null ? queryDto.getSortBy() : "name";
    Sort.Direction direction = "desc".equalsIgnoreCase(queryDto.getSortDirection())
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
    query.with(Sort.by(direction, sortBy));

    log.debug("Query construido: {}", query);

    List<User> results = mongoTemplate.find(query, User.class);
    log.debug("Encontrados {} usuarios", results.size());

    return results;
}
```

#### Pruebas en Swagger (15 min)

Probar diferentes combinaciones:

1. **Sin filtros:** Debe devolver todos (hasta el límite)
2. **Solo departamento:** `{ "department": "IT" }`
3. **Nombre parcial:** `{ "name": "ar" }` → Debería encontrar "García", "Carlos", "María"
4. **Combinación:** `{ "department": "IT", "active": true }`
5. **Paginación:** `{ "offset": 2, "limit": 3 }`
6. **Ordenamiento:** `{ "sortBy": "email", "sortDirection": "desc" }`

---

### Bloque 6: Tests y Cierre (45 min)

#### Ejecutar Tests (20 min)

```bash
./gradlew test --tests "*SpringData*"
```

Verificar que el test de searchUsers pasa:
```
SpringDataUserServiceTest > searchUsersTest() PASSED
```

Si falla, revisar:
- ¿Se combinan bien los criterios?
- ¿El regex tiene la sintaxis correcta?
- ¿Los valores por defecto son correctos?

#### Discusión: Complejidad vs Flexibilidad (15 min)

Comparar con los métodos simples:
- `findByDepartment()`: 1 línea, un solo filtro
- `searchUsers()`: ~30 líneas, filtros dinámicos

**Preguntas para reflexionar:**
- ¿Cuándo vale la pena esta complejidad?
- ¿Cómo sería esto en SQL puro?
- ¿Qué alternativas existen? (Specification, QueryDSL...)

#### Preparación para la Próxima Semana (10 min)

La semana 5:
- Defensa de `searchUsers()` Spring Data
- Implementar `searchUsers()` con API Nativa

Preparar para la defensa:
- Poder explicar cómo se construyen los criterios dinámicos
- Entender el rol de `andOperator()`
- Saber justificar el uso de regex para nombre

---

## Solución Completa con Comentarios

```java
@Override
public List<User> searchUsers(UserQueryDto queryDto) {
    // 1. Logging para debugging
    log.debug("Buscando usuarios con filtros: {}", queryDto);

    // 2. Lista para acumular criterios (filtros)
    List<Criteria> criteriaList = new ArrayList<>();

    // 3. FILTRO POR NOMBRE (búsqueda parcial)
    // Usamos regex para que "gar" encuentre "García"
    // La "i" hace la búsqueda case-insensitive
    if (queryDto.getName() != null && !queryDto.getName().trim().isEmpty()) {
        criteriaList.add(Criteria.where("name").regex(queryDto.getName(), "i"));
    }

    // 4. FILTRO POR EMAIL (búsqueda exacta)
    if (queryDto.getEmail() != null && !queryDto.getEmail().trim().isEmpty()) {
        criteriaList.add(Criteria.where("email").is(queryDto.getEmail()));
    }

    // 5. FILTRO POR DEPARTAMENTO (búsqueda exacta)
    if (queryDto.getDepartment() != null && !queryDto.getDepartment().trim().isEmpty()) {
        criteriaList.add(Criteria.where("department").is(queryDto.getDepartment()));
    }

    // 6. FILTRO POR ROL (búsqueda exacta)
    if (queryDto.getRole() != null && !queryDto.getRole().trim().isEmpty()) {
        criteriaList.add(Criteria.where("role").is(queryDto.getRole()));
    }

    // 7. FILTRO POR ACTIVE
    // Solo filtramos si el valor no es null (puede ser true o false)
    if (queryDto.getActive() != null) {
        criteriaList.add(Criteria.where("active").is(queryDto.getActive()));
    }

    // 8. CONSTRUIR QUERY
    Query query = new Query();

    // 9. COMBINAR CRITERIOS CON AND
    // Solo si hay al menos un criterio
    if (!criteriaList.isEmpty()) {
        // andOperator combina todos con AND lógico
        query.addCriteria(new Criteria().andOperator(
            criteriaList.toArray(new Criteria[0])
        ));
    }

    // 10. PAGINACIÓN
    // offset = cuántos documentos saltar
    // limit = cuántos documentos devolver como máximo
    int offset = queryDto.getOffset() != null ? queryDto.getOffset() : 0;
    int limit = queryDto.getLimit() != null ? queryDto.getLimit() : 20;
    query.skip(offset);
    query.limit(limit);

    // 11. ORDENAMIENTO
    String sortBy = queryDto.getSortBy() != null ? queryDto.getSortBy() : "name";
    Sort.Direction direction = "desc".equalsIgnoreCase(queryDto.getSortDirection())
        ? Sort.Direction.DESC
        : Sort.Direction.ASC;
    query.with(Sort.by(direction, sortBy));

    // 12. EJECUTAR CONSULTA
    log.debug("Query MongoDB: {}", query);
    List<User> results = mongoTemplate.find(query, User.class);

    log.debug("Encontrados {} usuarios", results.size());
    return results;
}
```

---

## Imports Necesarios

```java
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
```

---

## Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| `IllegalArgumentException: Invalid regex` | Caracteres especiales en el patrón | Escapar caracteres o usar `Pattern.quote()` |
| Devuelve lista vacía | Criterios mal combinados | Verificar que se usa `andOperator` correctamente |
| `NullPointerException` | No se verifica null antes de usar | Añadir comprobación `!= null` |
| Ordenamiento no funciona | Campo inexistente | Verificar nombre exacto del campo en User |
| Paginación incorrecta | offset y limit invertidos | offset = skip, limit = max resultados |

---

## Ejercicios de Ampliación

### Ejercicio 1: Búsqueda OR
Modificar para que nombre y email se busquen con OR:
```java
// Si especifican nombre O email, encontrar coincidencias en cualquiera
```

### Ejercicio 2: Rango de Fechas
Añadir filtro por fecha de creación:
```java
// createdAfter y createdBefore en UserQueryDto
Criteria.where("createdAt").gte(createdAfter).lte(createdBefore)
```

### Ejercicio 3: Contar Resultados
Añadir método que devuelva el total de resultados (sin paginación):
```java
public long countSearchResults(UserQueryDto queryDto)
```

---

## Checklist de Fin de Semana

- [ ] He defendido los 3 métodos de API Nativa
- [ ] searchUsers() está implementado completamente
- [ ] Todos los tests de Spring Data pasan
- [ ] He probado diferentes combinaciones en Swagger
- [ ] Entiendo cómo funciona Criteria y Query
- [ ] Tengo notas para defender searchUsers() la próxima semana
