---
marp: true
theme: default
paginate: true
backgroundColor: #fff
color: #333
header: 'MongoTemplate & Criteria'
footer: 'Semana 4 - Acceso a Datos'
---

<!-- _class: lead -->
<!-- _paginate: false -->

# MongoTemplate & Criteria

## Consultas Dinámicas Avanzadas

### Semana 4 - Acceso a Datos

---

## El Problema

### ¿Qué Pasa Cuando los Query Methods No Bastan?

**Escenario:** Búsqueda avanzada de usuarios

```
┌─────────────────────────────────────────────────────────┐
│              Formulario de Búsqueda                     │
├─────────────────────────────────────────────────────────┤
│  Nombre:      [_______________] (opcional)              │
│  Email:       [_______________] (opcional)              │
│  Departamento: [▼ Seleccionar] (opcional)              │
│  Activo:      ○ Sí  ○ No  ● Todos                      │
│  Ordenar por: [▼ Nombre     ] [▼ Asc ▼]                │
│  Página:      [1] de 5    Resultados: [10]             │
│                                                         │
│                    [ 🔍 Buscar ]                        │
└─────────────────────────────────────────────────────────┘
```

¿Cómo creamos **UN** query method para todas las combinaciones?

---

## El Límite de Query Methods

### Esto NO Escala

```java
// ¿Crear un método para cada combinación?
List<User> findByName(String name);
List<User> findByDepartment(String dept);
List<User> findByNameAndDepartment(String name, String dept);
List<User> findByNameAndDepartmentAndActive(String n, String d, boolean a);
List<User> findByDepartmentAndActive(String dept, boolean active);
// ... ¿32 métodos más?

// ¿Y si añadimos un campo nuevo?
// ¡Se duplica el número de combinaciones!
```

### Problemas:
- ❌ Explosión combinatoria
- ❌ Código duplicado
- ❌ Difícil de mantener
- ❌ ¿Cómo manejar paginación dinámica?

---

## La Solución: MongoTemplate

### Control Programático

```
┌─────────────────────────────────────────────────────────┐
│                    MongoRepository                      │
│           Query methods automáticos                     │
│           Operaciones CRUD simples                      │
└────────────────────────┬────────────────────────────────┘
                         │
          "Cuando necesitas más control..."
                         │
                         ▼
┌─────────────────────────────────────────────────────────┐
│                    MongoTemplate                        │
│           Consultas construidas programáticamente       │
│           Filtros dinámicos                             │
│           Control total sobre la query                  │
└─────────────────────────────────────────────────────────┘
```

**MongoTemplate permite:**
- ✅ Construir consultas en tiempo de ejecución
- ✅ Filtros opcionales (ignorar si es null)
- ✅ Paginación y ordenamiento dinámicos
- ✅ Proyecciones personalizadas

---

## Anatomía de una Consulta

### Construyendo una Consulta

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Criteria   │────▶│    Query     │────▶│ MongoTemplate│
│  (filtros)   │     │  (consulta)  │     │  (ejecutar)  │
└──────────────┘     └──────────────┘     └──────────────┘
```

```java
Criteria criteria = Criteria.where("department").is("IT");
         │
         ▼
Query query = new Query(criteria);
         │
         ▼
List<User> results = mongoTemplate.find(query, User.class);
```

### 3 pasos:
1. **Criteria:** Define las condiciones (WHERE)
2. **Query:** Empaqueta criterios + paginación + orden
3. **MongoTemplate:** Ejecuta y devuelve resultados

---

## La Clase Criteria

### Definiendo Condiciones

```java
import org.springframework.data.mongodb.core.query.Criteria;

// Igualdad
Criteria c1 = Criteria.where("department").is("IT");
// → { "department": "IT" }

// Comparación
Criteria c2 = Criteria.where("age").gt(25);
// → { "age": { "$gt": 25 } }

// Regex (búsqueda parcial, case-insensitive)
Criteria c3 = Criteria.where("name").regex("garcía", "i");
// → { "name": { "$regex": "garcía", "$options": "i" } }

// Nulo / No nulo
Criteria c4 = Criteria.where("email").ne(null);
// → { "email": { "$ne": null } }
```

---

## Operadores de Criteria

| Método Criteria | Operador MongoDB | Ejemplo |
|-----------------|------------------|---------|
| `.is(valor)` | `$eq` | Igualdad exacta |
| `.ne(valor)` | `$ne` | No igual |
| `.gt(valor)` | `$gt` | Mayor que |
| `.gte(valor)` | `$gte` | Mayor o igual |
| `.lt(valor)` | `$lt` | Menor que |
| `.lte(valor)` | `$lte` | Menor o igual |
| `.in(valores)` | `$in` | Dentro de lista |
| `.nin(valores)` | `$nin` | No en lista |
| `.regex(patron)` | `$regex` | Expresión regular |
| `.exists(bool)` | `$exists` | Campo existe |

---

## Combinando Criterios

### AND y OR

**AND (todas las condiciones):**
```java
Criteria criteria = new Criteria()
    .andOperator(
        Criteria.where("department").is("IT"),
        Criteria.where("active").is(true)
    );
// → { "$and": [{"department":"IT"}, {"active":true}] }
```

**OR (cualquier condición):**
```java
Criteria criteria = new Criteria()
    .orOperator(
        Criteria.where("department").is("IT"),
        Criteria.where("department").is("HR")
    );
// → { "$or": [{"department":"IT"}, {"department":"HR"}] }
```

---

## La Clase Query

### Empaquetando Todo

```java
import org.springframework.data.mongodb.core.query.Query;

// Query con criterios
Query query = new Query(criteria);

// Añadir paginación
query.skip(20);    // Saltar 20 documentos
query.limit(10);   // Máximo 10 resultados

// Añadir ordenamiento
query.with(Sort.by(Sort.Direction.ASC, "name"));

// Añadir proyección (solo ciertos campos)
query.fields().include("name", "email").exclude("_id");
```

**Query encapsula:**
- Criterios de filtrado
- Paginación (skip/limit)
- Ordenamiento (sort)
- Proyección (campos a incluir/excluir)

---

## Ordenamiento con Sort

```java
import org.springframework.data.domain.Sort;

// Ascendente por un campo
query.with(Sort.by(Sort.Direction.ASC, "name"));

// Descendente
query.with(Sort.by(Sort.Direction.DESC, "createdAt"));

// Múltiples campos
query.with(Sort.by(
    Sort.Order.asc("department"),
    Sort.Order.desc("name")
));

// Desde String (útil para parámetros dinámicos)
String sortBy = "name";
String direction = "desc";
Sort.Direction dir = "desc".equalsIgnoreCase(direction)
    ? Sort.Direction.DESC
    : Sort.Direction.ASC;
query.with(Sort.by(dir, sortBy));
```

---

## MongoTemplate - Ejecutar

### Métodos Principales

```java
@Autowired
private MongoTemplate mongoTemplate;

// Buscar múltiples documentos
List<User> users = mongoTemplate.find(query, User.class);

// Buscar uno (primero que coincida)
User user = mongoTemplate.findOne(query, User.class);

// Contar
long count = mongoTemplate.count(query, User.class);

// Existe alguno
boolean exists = mongoTemplate.exists(query, User.class);
```

**Ventaja:** El mapeo Document → User es **automático**

(No hay que hacer mapDocumentToUser manual como en API Nativa)

---

## El Patrón de Filtros Dinámicos

```java
public List<User> search(String name, String dept, Boolean active) {

    // 1. Lista para acumular criterios
    List<Criteria> criteriaList = new ArrayList<>();

    // 2. Añadir solo si el parámetro tiene valor
    if (name != null && !name.isEmpty()) {
        criteriaList.add(Criteria.where("name").regex(name, "i"));
    }

    if (dept != null && !dept.isEmpty()) {
        criteriaList.add(Criteria.where("department").is(dept));
    }

    if (active != null) {  // Boolean puede ser true, false, o null
        criteriaList.add(Criteria.where("active").is(active));
    }

    // 3. Construir query
    Query query = new Query();

    // 4. Combinar criterios solo si hay alguno
    if (!criteriaList.isEmpty()) {
        query.addCriteria(new Criteria().andOperator(
            criteriaList.toArray(new Criteria[0])
        ));
    }

    // 5. Ejecutar
    return mongoTemplate.find(query, User.class);
}
```

---

## UserQueryDto

### DTO para Parámetros de Búsqueda

```java
public class UserQueryDto {
    // Filtros (todos opcionales)
    private String name;        // Búsqueda parcial
    private String email;       // Exacto
    private String department;  // Exacto
    private String role;        // Exacto
    private Boolean active;     // true/false/null

    // Paginación
    private Integer offset = 0;     // Desde qué posición
    private Integer limit = 20;     // Cuántos resultados

    // Ordenamiento
    private String sortBy = "name";
    private String sortDirection = "asc";

    // getters y setters...
}
```

---

## searchUsers() - Parte 1: Filtros

```java
public List<User> searchUsers(UserQueryDto dto) {
    List<Criteria> criteriaList = new ArrayList<>();

    // Nombre: búsqueda parcial case-insensitive
    if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
        criteriaList.add(
            Criteria.where("name").regex(dto.getName(), "i")
        );
    }

    // Departamento: exacto
    if (dto.getDepartment() != null &&
        !dto.getDepartment().trim().isEmpty()) {
        criteriaList.add(
            Criteria.where("department").is(dto.getDepartment())
        );
    }

    // Active: solo si no es null
    if (dto.getActive() != null) {
        criteriaList.add(
            Criteria.where("active").is(dto.getActive())
        );
    }
    // ... continúa
```

---

## searchUsers() - Parte 2: Query

```java
    // ... viene de slide anterior

    // Construir Query
    Query query = new Query();

    // Combinar criterios con AND
    if (!criteriaList.isEmpty()) {
        query.addCriteria(
            new Criteria().andOperator(
                criteriaList.toArray(new Criteria[0])
            )
        );
    }

    // Paginación
    int offset = dto.getOffset() != null ? dto.getOffset() : 0;
    int limit = dto.getLimit() != null ? dto.getLimit() : 20;
    query.skip(offset);
    query.limit(limit);

    // Ordenamiento
    String sortBy = dto.getSortBy() != null ? dto.getSortBy() : "name";
    Sort.Direction dir = "desc".equalsIgnoreCase(dto.getSortDirection())
        ? Sort.Direction.DESC : Sort.Direction.ASC;
    query.with(Sort.by(dir, sortBy));

    // Ejecutar
    return mongoTemplate.find(query, User.class);
}
```

---

## Visualización del Flujo

```
Petición:
{ name: "Ana", department: null, active: true, limit: 5 }

         │
         ▼

Criterios generados:
[
  { "name": { "$regex": "Ana", "$options": "i" } },
  { "active": true }
]
// department se ignora porque es null

         │
         ▼

Query MongoDB:
{
  "$and": [
    { "name": { "$regex": "Ana", "$options": "i" } },
    { "active": true }
  ]
}
.sort({ "name": 1 })
.skip(0)
.limit(5)
```

---

## Regex para Búsqueda Parcial

```java
// Contiene "garcía" (cualquier posición)
Criteria.where("name").regex("garcía", "i")
// Encuentra: "Ana García", "María García López", "garcía pedro"

// Empieza por "Ana"
Criteria.where("name").regex("^Ana", "i")
// Encuentra: "Ana García", "Ana María", pero NO "Mariana"

// Termina en "@gmail.com"
Criteria.where("email").regex("@gmail\\.com$", "i")
// El \\ escapa el punto (. en regex = "cualquier carácter")
```

### Opciones de regex:
- `"i"` = case insensitive
- `"m"` = multiline
- `"s"` = dotall (. incluye newline)

---

## Errores Comunes

### ⚠️ andOperator con lista vacía:
```java
// ❌ Error si criteriaList está vacía
new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))

// ✅ Verificar primero
if (!criteriaList.isEmpty()) {
    query.addCriteria(new Criteria().andOperator(...));
}
```

### ⚠️ String vacío vs null:
```java
// ❌ Solo verifica null
if (name != null) { ... }  // "" pasa la verificación

// ✅ Verificar ambos
if (name != null && !name.trim().isEmpty()) { ... }
```

---

## MongoTemplate vs API Nativa

| Aspecto | MongoTemplate | API Nativa |
|---------|---------------|------------|
| Mapeo | Automático | Manual |
| Sintaxis | Spring Criteria | Filters/Document |
| Integración | Spring DI | Manual |
| Flexibilidad | Alta | Total |
| Curva aprendizaje | Media | Media-Alta |
| Código | Menos | Más |

### ¿Cuándo usar cada uno?
- **MongoTemplate:** Aplicaciones Spring, mayoría de casos
- **API Nativa:** Máximo control, operaciones muy específicas

---

## El TODO de Hoy

### Implementar searchUsers()

**Archivo:** `SpringDataUserServiceImpl.java`

**Campos a filtrar:**
- name (regex)
- email (exacto)
- department (exacto)
- role (exacto)
- active (booleano)

**Añadir:**
- Paginación (offset, limit)
- Ordenamiento dinámico (sortBy, sortDirection)

---

## Probar con Swagger

### Ejemplos de Request JSON

**Todos los usuarios:**
```json
{}
```

**Solo departamento IT:**
```json
{ "department": "IT" }
```

**Nombre parcial + activos:**
```json
{
  "name": "García",
  "active": true
}
```

**Paginado y ordenado:**
```json
{
  "offset": 0,
  "limit": 5,
  "sortBy": "email",
  "sortDirection": "desc"
}
```

---

## Resumen

### Conceptos Clave

**Criteria**
Define condiciones de filtrado
`.where("campo").is(valor)` / `.regex()` / `.gt()`...

**Query**
Empaqueta criterios + paginación + ordenamiento
`.skip()` / `.limit()` / `.with(Sort)`

**MongoTemplate**
Ejecuta queries, mapeo automático
`.find(query, User.class)` → `List<User>`

**Patrón Filtros Dinámicos**
Lista de criterios + verificar null + andOperator

---

<!-- _class: lead -->
<!-- _paginate: false -->

# ¿Preguntas?

## 🍃

> "La flexibilidad de las consultas dinámicas es clave en aplicaciones reales"
