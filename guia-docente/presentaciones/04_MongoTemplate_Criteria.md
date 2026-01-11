# Presentación: MongoTemplate y Criteria

> **Duración:** 45 minutos
> **Semana:** 4
> **Bloque:** Teoría - Consultas Dinámicas con Spring Data

---

## DIAPOSITIVA 1: Portada

### Contenido:

```
╔════════════════════════════════════════════════════════════╗
║                                                            ║
║            MongoTemplate & Criteria                        ║
║                                                            ║
║         Consultas Dinámicas Avanzadas                      ║
║                                                            ║
║     ─────────────────────────────────────                  ║
║                                                            ║
║         Semana 4 - Acceso a Datos                          ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Notas del presentador:**
Hoy veremos cómo manejar consultas complejas donde los query methods de Spring Data no son suficientes. Introducimos MongoTemplate y la API Criteria.

---

## DIAPOSITIVA 2: El Problema

**Diseño:** Escenario con pregunta

### Contenido:

**Título:** ¿Qué Pasa Cuando los Query Methods No Bastan?

**Escenario:** Búsqueda avanzada de usuarios

```
┌─────────────────────────────────────────────────────────────┐
│                  Formulario de Búsqueda                     │
├─────────────────────────────────────────────────────────────┤
│  Nombre:      [_______________] (opcional)                  │
│  Email:       [_______________] (opcional)                  │
│  Departamento: [▼ Seleccionar ] (opcional)                  │
│  Activo:      ○ Sí  ○ No  ● Todos                          │
│  Ordenar por: [▼ Nombre      ] [▼ Asc ▼]                   │
│  Página:      [1] de 5    Resultados: [10]                  │
│                                                             │
│                    [ 🔍 Buscar ]                            │
└─────────────────────────────────────────────────────────────┘
```

**¿Cómo creamos UN query method para todas las combinaciones posibles?**

**Notas del presentador:**
Con 5 campos opcionales hay 32 combinaciones posibles. No podemos crear 32 query methods diferentes. Necesitamos algo más flexible.

---

## DIAPOSITIVA 3: El Límite de Query Methods

**Diseño:** Ejemplo de lo que NO funciona

### Contenido:

**Título:** Esto NO Escala

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

**Problemas:**
- ❌ Explosión combinatoria
- ❌ Código duplicado
- ❌ Difícil de mantener
- ❌ ¿Cómo manejar paginación dinámica?

**Notas del presentador:**
Además, los query methods no soportan bien parámetros null. Si pasas null, busca documentos donde el campo ES null, no lo ignora.

---

## DIAPOSITIVA 4: La Solución: MongoTemplate

**Diseño:** Definición con diagrama

### Contenido:

**Título:** MongoTemplate: Control Programático

```
┌─────────────────────────────────────────────────────────────┐
│                    MongoRepository                          │
│           Query methods automáticos                         │
│           Operaciones CRUD simples                          │
└────────────────────────┬────────────────────────────────────┘
                         │
          "Cuando necesitas más control..."
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    MongoTemplate                            │
│           Consultas construidas programáticamente           │
│           Filtros dinámicos                                 │
│           Control total sobre la query                      │
└─────────────────────────────────────────────────────────────┘
```

**MongoTemplate permite:**
- ✅ Construir consultas en tiempo de ejecución
- ✅ Filtros opcionales (ignorar si es null)
- ✅ Paginación y ordenamiento dinámicos
- ✅ Proyecciones personalizadas

**Notas del presentador:**
MongoTemplate es el puente entre la comodidad de Spring Data y el control de la API nativa. Lo mejor de ambos mundos.

---

## DIAPOSITIVA 5: Anatomía de una Consulta

**Diseño:** Diagrama de flujo

### Contenido:

**Título:** Construyendo una Consulta

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Criteria   │────▶│    Query     │────▶│ MongoTemplate│
│  (filtros)   │     │  (consulta)  │     │  (ejecutar)  │
└──────────────┘     └──────────────┘     └──────────────┘

Criteria criteria = Criteria.where("department").is("IT");
         │
         ▼
Query query = new Query(criteria);
         │
         ▼
List<User> results = mongoTemplate.find(query, User.class);
```

**3 pasos:**
1. **Criteria:** Define las condiciones (WHERE)
2. **Query:** Empaqueta criterios + paginación + orden
3. **MongoTemplate:** Ejecuta y devuelve resultados

**Notas del presentador:**
Es como construir una consulta SQL paso a paso: primero el WHERE, luego ORDER BY, LIMIT, y finalmente ejecutar.

---

## DIAPOSITIVA 6: La Clase Criteria

**Diseño:** Ejemplos de código

### Contenido:

**Título:** Criteria: Definiendo Condiciones

```java
import org.springframework.data.mongodb.core.query.Criteria;

// Igualdad
Criteria c1 = Criteria.where("department").is("IT");
// → { "department": "IT" }

// Comparación
Criteria c2 = Criteria.where("age").gt(25);
// → { "age": { "$gt": 25 } }

// Regex (búsqueda parcial)
Criteria c3 = Criteria.where("name").regex("garcía", "i");
// → { "name": { "$regex": "garcía", "$options": "i" } }

// Nulo / No nulo
Criteria c4 = Criteria.where("email").ne(null);
// → { "email": { "$ne": null } }
```

**Notas del presentador:**
Criteria es el equivalente Spring Data de la clase Filters de la API nativa. Mismos conceptos, diferente sintaxis.

---

## DIAPOSITIVA 7: Operadores de Criteria

**Diseño:** Tabla de referencia

### Contenido:

**Título:** Operadores Disponibles

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

**Notas del presentador:**
La mayoría de operadores MongoDB tienen su equivalente en Criteria. Para casos muy especiales se puede usar `Criteria.where().raw(new Document(...))`.

---

## DIAPOSITIVA 8: Combinando Criterios

**Diseño:** Código con diagrama visual

### Contenido:

**Título:** AND y OR con Criteria

**AND (por defecto al encadenar):**
```java
Criteria criteria = new Criteria()
    .andOperator(
        Criteria.where("department").is("IT"),
        Criteria.where("active").is(true)
    );
// → { "$and": [{ "department": "IT" }, { "active": true }] }
```

**OR:**
```java
Criteria criteria = new Criteria()
    .orOperator(
        Criteria.where("department").is("IT"),
        Criteria.where("department").is("HR")
    );
// → { "$or": [{ "department": "IT" }, { "department": "HR" }] }
```

**Notas del presentador:**
andOperator y orOperator aceptan varargs o arrays de Criteria. Muy útil para construir filtros dinámicamente.

---

## DIAPOSITIVA 9: La Clase Query

**Diseño:** Código progresivo

### Contenido:

**Título:** Query: Empaquetando Todo

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

**Notas del presentador:**
Query es inmutable después de construirse, pero los métodos devuelven la misma instancia para encadenamiento fluido.

---

## DIAPOSITIVA 10: Ordenamiento con Sort

**Diseño:** Ejemplos de Sort

### Contenido:

**Título:** Ordenando Resultados

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

**Notas del presentador:**
Sort.Direction es un enum. Podemos construirlo dinámicamente desde parámetros de la petición HTTP.

---

## DIAPOSITIVA 11: MongoTemplate - Ejecutar

**Diseño:** Métodos principales

### Contenido:

**Título:** Métodos de MongoTemplate

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

**Notas del presentador:**
MongoTemplate usa la configuración de mapeo de Spring Data. Las anotaciones @Document, @Id, @Field funcionan automáticamente.

---

## DIAPOSITIVA 12: El Patrón de Filtros Dinámicos

**Diseño:** Código completo paso a paso

### Contenido:

**Título:** Construyendo Filtros Opcionales

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

**Notas del presentador:**
Este patrón es la base de searchUsers(). La clave es verificar null ANTES de añadir el criterio, no después.

---

## DIAPOSITIVA 13: UserQueryDto

**Diseño:** Clase DTO con campos

### Contenido:

**Título:** DTO para Parámetros de Búsqueda

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

**Notas del presentador:**
Este DTO encapsula todos los parámetros posibles. Los valores por defecto evitan NullPointerException en paginación.

---

## DIAPOSITIVA 14: searchUsers() Paso a Paso

**Diseño:** Código dividido en secciones

### Contenido:

**Título:** Implementación Completa (Parte 1: Filtros)

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
    if (dto.getDepartment() != null && !dto.getDepartment().trim().isEmpty()) {
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

    // ... continúa en siguiente slide
```

**Notas del presentador:**
Notar: para String verificamos null Y isEmpty. Para Boolean solo null (porque false es un valor válido).

---

## DIAPOSITIVA 15: searchUsers() Parte 2

**Diseño:** Continuación del código

### Contenido:

**Título:** Implementación Completa (Parte 2: Query)

```java
    // ... viene del slide anterior

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

**Notas del presentador:**
El código es largo pero cada parte es simple. Filtros → Combinar → Paginar → Ordenar → Ejecutar.

---

## DIAPOSITIVA 16: Visualización del Flujo

**Diseño:** Diagrama de flujo visual

### Contenido:

**Título:** ¿Qué Pasa Internamente?

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

         │
         ▼

Resultados: List<User> con máximo 5 usuarios
```

**Notas del presentador:**
department no aparece en la query porque era null. Este es el comportamiento deseado: campos null = no filtrar por ese campo.

---

## DIAPOSITIVA 17: Regex para Búsqueda Parcial

**Diseño:** Ejemplos de regex

### Contenido:

**Título:** Búsqueda de Texto con Regex

```java
// Contiene "garcía" (cualquier posición)
Criteria.where("name").regex("garcía", "i")
// Encuentra: "Ana García", "María García López", "garcía pedro"

// Empieza por "Ana"
Criteria.where("name").regex("^Ana", "i")
// Encuentra: "Ana García", "Ana María", pero NO "Mariana"

// Termina en "@gmail.com"
Criteria.where("email").regex("@gmail\\.com$", "i")
// El \\ escapa el punto (. en regex significa "cualquier carácter")
```

**Opciones de regex:**
- `"i"` = case insensitive (ignora mayúsculas)
- `"m"` = multiline
- `"s"` = dotall (. incluye newline)

**Notas del presentador:**
Para búsquedas de texto intensivas, MongoDB tiene índices de texto completo ($text) que son más eficientes que regex.

---

## DIAPOSITIVA 18: Errores Comunes

**Diseño:** Lista de problemas y soluciones

### Contenido:

**Título:** Problemas Típicos

**⚠️ andOperator con lista vacía:**
```java
// ❌ Error si criteriaList está vacía
new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))

// ✅ Verificar primero
if (!criteriaList.isEmpty()) {
    query.addCriteria(new Criteria().andOperator(...));
}
```

**⚠️ String vacío vs null:**
```java
// ❌ Solo verifica null
if (name != null) { ... }  // "" pasa la verificación

// ✅ Verificar ambos
if (name != null && !name.trim().isEmpty()) { ... }
```

**⚠️ Valor por defecto faltante:**
```java
// ❌ NullPointerException si dto.getLimit() es null
query.limit(dto.getLimit());

// ✅ Con valor por defecto
query.limit(dto.getLimit() != null ? dto.getLimit() : 20);
```

**Notas del presentador:**
Estos tres errores cubren el 90% de los problemas que veréis. Siempre verificar null y empty, y usar valores por defecto.

---

## DIAPOSITIVA 19: MongoTemplate vs API Nativa

**Diseño:** Tabla comparativa

### Contenido:

**Título:** Comparación

| Aspecto | MongoTemplate | API Nativa |
|---------|---------------|------------|
| Mapeo | Automático | Manual |
| Sintaxis | Spring Criteria | Filters/Document |
| Integración | Spring DI | Manual |
| Flexibilidad | Alta | Total |
| Curva aprendizaje | Media | Media-Alta |
| Código | Menos | Más |

**¿Cuándo usar cada uno?**
- **MongoTemplate:** Aplicaciones Spring, mayoría de casos
- **API Nativa:** Máximo control, operaciones muy específicas

**Notas del presentador:**
En nuestro proyecto usamos ambos para que veáis las diferencias. En producción, probablemente usaríais uno u otro según el proyecto.

---

## DIAPOSITIVA 20: El TODO de Hoy

**Diseño:** Instrucciones claras

### Contenido:

**Título:** Implementar searchUsers()

**Archivo:** `SpringDataUserServiceImpl.java`

**Estructura:**
```java
@Override
public List<User> searchUsers(UserQueryDto queryDto) {
    // 1. Crear lista de criterios
    // 2. Añadir criterios condicionalmente
    // 3. Construir Query
    // 4. Añadir paginación
    // 5. Añadir ordenamiento
    // 6. Ejecutar con mongoTemplate
    return mongoTemplate.find(query, User.class);
}
```

**Campos a filtrar:**
- name (regex)
- email (exacto)
- department (exacto)
- role (exacto)
- active (booleano)

**Notas del presentador:**
Es el método más complejo del proyecto. Tomad vuestro tiempo, seguid el patrón paso a paso, y probad con Swagger.

---

## DIAPOSITIVA 21: Probar con Swagger

**Diseño:** Ejemplo de request JSON

### Contenido:

**Título:** Ejemplos para Probar

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

**Notas del presentador:**
Probar diferentes combinaciones. Verificar que los campos null realmente se ignoran y no causan errores.

---

## DIAPOSITIVA 22: Resumen

**Diseño:** Puntos clave

### Contenido:

**Título:** Conceptos Clave

```
┌────────────────────────────────────────────────────────────┐
│  Criteria                                                  │
│  Define condiciones de filtrado                            │
│  .where("campo").is(valor) / .regex() / .gt() ...         │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│  Query                                                     │
│  Empaqueta criterios + paginación + ordenamiento           │
│  .skip() / .limit() / .with(Sort)                         │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│  MongoTemplate                                             │
│  Ejecuta queries, mapeo automático                         │
│  .find(query, User.class) → List<User>                    │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│  Patrón Filtros Dinámicos                                  │
│  Lista de criterios + verificar null + andOperator         │
└────────────────────────────────────────────────────────────┘
```

**Notas del presentador:**
Con estos conceptos podéis implementar cualquier búsqueda dinámica en Spring Data MongoDB.

---

## DIAPOSITIVA 23: Preguntas

**Diseño:** Slide de cierre

### Contenido:

```
╔════════════════════════════════════════════════════════════╗
║                                                            ║
║                    ¿Preguntas?                             ║
║                                                            ║
║                       🍃                                   ║
║                                                            ║
║     "La flexibilidad de las consultas dinámicas           ║
║      es clave en aplicaciones reales"                      ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Notas del presentador:**
Este es el método más complejo que implementaréis. La defensa de la próxima semana será más exigente. Preparad bien las explicaciones.
