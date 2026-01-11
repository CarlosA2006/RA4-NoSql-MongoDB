---
marp: true
theme: default
paginate: true
backgroundColor: #fff
color: #333
header: 'Aggregation Framework'
footer: 'Semana 6 - Acceso a Datos'
---

<!-- _class: lead -->
<!-- _paginate: false -->

# Aggregation Framework

## Procesamiento y Análisis de Datos

### Semana 6 - Acceso a Datos

---

## El Problema

### ¿Cómo Obtener Estadísticas?

**Necesidad:** Dashboard de recursos humanos

```
┌─────────────────────────────────────────────────────────┐
│              📊 Dashboard de Empleados                  │
├─────────────────────────────────────────────────────────┤
│                                                         │
│   Departamento    Total    Activos    % Activos        │
│   ───────────────────────────────────────────          │
│   IT              15       14         93%              │
│   HR               8        8        100%              │
│   Finance          5        4         80%              │
│   Marketing        3        3        100%              │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**¿Cómo calcular esto eficientemente?**

---

## Enfoque Tradicional (Malo)

### ❌ No Hagas Esto

```java
// Traer TODOS los usuarios a memoria
List<User> allUsers = userRepository.findAll();

// Agrupar en Java
Map<String, Long> countByDept = allUsers.stream()
    .collect(Collectors.groupingBy(
        User::getDepartment,
        Collectors.counting()
    ));
```

### Problemas:
- ⚠️ Transfiere TODOS los documentos (red, memoria)
- ⚠️ Procesamiento en cliente (CPU del servidor de app)
- ⚠️ No escala con millones de registros

> Con 10 usuarios funciona. Con 1 millón, la aplicación se cae.

---

## La Solución: Aggregation

### Aggregation Framework

> Sistema de procesamiento de datos que ejecuta operaciones **en el servidor MongoDB**, transformando y analizando documentos mediante un **pipeline de etapas**.

```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│ Colección│──▶│  Stage 1 │──▶│  Stage 2 │──▶│  Stage 3 │──▶ Resultado
│ Original │   │ ($match) │   │ ($group) │   │  ($sort) │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
```

### Ventajas:
- ✅ Procesamiento en el servidor
- ✅ Sin transferencia de datos masiva
- ✅ Optimizado con índices
- ✅ Operaciones complejas en una query

---

## Concepto de Pipeline

```
    Documentos Originales
    ┌────────────────────────────────────────────────┐
    │ { name: "Ana", dept: "IT", active: true }     │
    │ { name: "Carlos", dept: "IT", active: true }  │
    │ { name: "María", dept: "HR", active: true }   │
    │ { name: "Pedro", dept: "IT", active: false }  │
    └────────────────────────────────────────────────┘
                      │
                      ▼
            ┌─────────────────┐
            │   $match        │  ← Filtrar (opcional)
            │ {active: true}  │
            └─────────────────┘
                      │
                      ▼
            ┌─────────────────────┐
            │   $group            │  ← Agrupar y calcular
            │ { _id: "$dept",     │
            │   count: {$sum:1}}  │
            └─────────────────────┘
                      │
                      ▼
            ┌─────────────────┐
            │   $sort         │  ← Ordenar resultado
            │ { count: -1 }   │
            └─────────────────┘
                      │
                      ▼
    Resultado Final
    ┌────────────────────────────┐
    │ { _id: "IT", count: 2 }   │
    │ { _id: "HR", count: 1 }   │
    └────────────────────────────┘
```

---

## Etapas Principales

| Stage | Función | Equivalente SQL |
|-------|---------|-----------------|
| `$match` | Filtrar documentos | WHERE |
| `$group` | Agrupar y agregar | GROUP BY |
| `$sort` | Ordenar resultados | ORDER BY |
| `$project` | Seleccionar/transformar campos | SELECT |
| `$limit` | Limitar cantidad | LIMIT |
| `$skip` | Saltar documentos | OFFSET |
| `$unwind` | Expandir arrays | (no existe) |
| `$lookup` | Join con otra colección | JOIN |

> Hay más de 30 stages disponibles, pero estos cubren el 90% de los casos.

---

## $match - Filtrar

```javascript
// MongoDB Shell
db.users.aggregate([
    { $match: { active: true } }
])

// Resultado: Solo documentos donde active = true
```

```java
// Java - API Nativa
List<Bson> pipeline = Arrays.asList(
    Aggregates.match(Filters.eq("active", true))
);
```

### Buenas prácticas:
- ✅ Poner $match al principio (usa índices)
- ✅ Filtra temprano para reducir datos en etapas posteriores
- ❌ $match al final procesa documentos innecesarios

---

## $group - Agrupar

### El Corazón de la Agregación

```javascript
{
  $group: {
    _id: "$department",           // Campo para agrupar
    totalUsers: { $sum: 1 },      // Contar documentos
    avgAge: { $avg: "$age" },     // Promedio
    maxSalary: { $max: "$salary" },
    minSalary: { $min: "$salary" },
    allNames: { $push: "$name" }  // Array con todos los nombres
  }
}
```

### Equivalente SQL:
```sql
SELECT
    department,
    COUNT(*) as totalUsers,
    AVG(age) as avgAge,
    MAX(salary) as maxSalary
FROM users
GROUP BY department
```

---

## Operadores de Acumulación

| Operador | Descripción | Ejemplo |
|----------|-------------|---------|
| `$sum` | Sumar valores | `{ $sum: "$salary" }` |
| `$avg` | Promedio | `{ $avg: "$age" }` |
| `$min` | Valor mínimo | `{ $min: "$createdAt" }` |
| `$max` | Valor máximo | `{ $max: "$salary" }` |
| `$first` | Primer valor del grupo | `{ $first: "$name" }` |
| `$last` | Último valor del grupo | `{ $last: "$name" }` |
| `$push` | Array con todos los valores | `{ $push: "$email" }` |
| `$addToSet` | Array sin duplicados | `{ $addToSet: "$role" }` |

### Contar documentos:
```javascript
{ $sum: 1 }  // Suma 1 por cada documento = contar
```

---

## $sort y $limit

### Ordenar:
```javascript
{ $sort: { totalUsers: -1 } }  // Descendente (más primero)
{ $sort: { totalUsers: 1 } }   // Ascendente (menos primero)

// Múltiples campos
{ $sort: { department: 1, totalUsers: -1 } }
```

### Limitar cantidad:
```javascript
{ $limit: 5 }  // Solo los primeros 5 resultados
```

### Saltar documentos:
```javascript
{ $skip: 10 }  // Saltar los primeros 10
```

### Paginación:
```javascript
[
    { $skip: 20 },   // Página 3 (20 = 10 * 2)
    { $limit: 10 }   // 10 por página
]
```

---

## $project - Transformar

```javascript
{
  $project: {
    _id: 0,                    // Excluir _id
    name: 1,                   // Incluir name
    email: 1,                  // Incluir email

    // Renombrar campo
    fullName: "$name",

    // Calcular nuevo campo
    isActive: { $eq: ["$active", true] },

    // Concatenar
    display: { $concat: ["$name", " - ", "$department"] }
  }
}
```

### Resultado:
```javascript
{
  name: "Ana García",
  email: "ana@empresa.com",
  fullName: "Ana García",
  isActive: true,
  display: "Ana García - IT"
}
```

---

## Ejemplo Completo - Estadísticas

### Estadísticas por Departamento

```javascript
db.users.aggregate([
    // 1. Agrupar por departamento
    {
        $group: {
            _id: "$department",
            totalUsers: { $sum: 1 },
            activeUsers: {
                $sum: {
                    $cond: [{ $eq: ["$active", true] }, 1, 0]
                }
            }
        }
    },
    // 2. Ordenar por total descendente
    {
        $sort: { totalUsers: -1 }
    },
    // 3. Renombrar _id a department
    {
        $project: {
            _id: 0,
            department: "$_id",
            totalUsers: 1,
            activeUsers: 1
        }
    }
])
```

---

## El Operador $cond

### Condicional IF-THEN-ELSE

```javascript
{
    $cond: [
        <condición>,    // IF
        <valor_si_true>, // THEN
        <valor_si_false> // ELSE
    ]
}
```

### Ejemplo: Contar solo activos
```javascript
{
    $sum: {
        $cond: [
            { $eq: ["$active", true] },  // IF active == true
            1,                            // THEN sumar 1
            0                             // ELSE sumar 0
        ]
    }
}
```

**Equivalente SQL:**
```sql
SUM(CASE WHEN active = true THEN 1 ELSE 0 END)
```

---

## Java - API Nativa

```java
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Accumulators.*;

public List<DepartmentStats> getStatsByDepartment() {
    List<Bson> pipeline = Arrays.asList(
        // Stage 1: Group
        group(
            "$department",  // _id = campo department
            sum("totalUsers", 1),
            sum("activeUsers", new Document("$cond",
                Arrays.asList(
                    new Document("$eq",
                        Arrays.asList("$active", true)),
                    1,
                    0
                )
            ))
        ),
        // Stage 2: Sort
        sort(Sorts.descending("totalUsers"))
    );

    AggregateIterable<Document> results =
        collection.aggregate(pipeline);

    // Mapear resultados...
}
```

---

## Java - Spring Data

```java
import org.springframework.data.mongodb.core.aggregation.*;

public List<DepartmentStats> getStatsByDepartment() {
    Aggregation aggregation = Aggregation.newAggregation(
        // Stage 1: Group
        Aggregation.group("department")
            .count().as("totalUsers")
            .sum(ConditionalOperators
                .when(Criteria.where("active").is(true))
                .then(1).otherwise(0))
            .as("activeUsers"),

        // Stage 2: Sort
        Aggregation.sort(Sort.Direction.DESC, "totalUsers"),

        // Stage 3: Project (renombrar _id)
        Aggregation.project()
            .and("_id").as("department")
            .andInclude("totalUsers", "activeUsers")
    );

    return mongoTemplate
        .aggregate(aggregation, "users", DepartmentStats.class)
        .getMappedResults();
}
```

---

## Comparación de Sintaxis

### La Misma Agregación en 3 Formas

**MongoDB Shell:**
```javascript
db.users.aggregate([
  { $group: {
      _id: "$department",
      total: { $sum: 1 }
  }},
  { $sort: { total: -1 }}
])
```

**Java API Nativa:**
```java
Arrays.asList(
  group("$department", sum("total", 1)),
  sort(descending("total"))
)
```

**Spring Data:**
```java
Aggregation.newAggregation(
  group("department").count().as("total"),
  sort(DESC, "total")
)
```

---

## Casos de Uso Reales

### ¿Cuándo Usar Aggregation?

**Informes y Dashboards:**
- Ventas por mes/región/producto
- Usuarios activos por día
- Tiempo promedio de respuesta

**Análisis de Datos:**
- Top 10 productos más vendidos
- Clientes con mayor gasto
- Tendencias de uso

**Transformación de Datos:**
- Denormalizar documentos
- Calcular campos derivados
- Preparar datos para exportación

**ETL (Extract, Transform, Load):**
- Migración entre colecciones
- Limpieza de datos
- Enriquecimiento de documentos

---

## Optimización

### Rendimiento en Agregaciones

### ✅ Buenas Prácticas:

1. **$match primero:** Filtra temprano para reducir documentos
```javascript
[{ $match: {...} }, { $group: {...} }]  // ✅ Bien
[{ $group: {...} }, { $match: {...} }]  // ❌ Malo
```

2. **Usa índices:** $match y $sort usan índices si están primero

3. **Limita campos con $project:** Reduce memoria

4. **$limit temprano si es posible:** Menos datos a procesar

### ⚠️ Precauciones:
- Pipeline muy largos pueden ser lentos
- $unwind multiplica documentos exponencialmente
- $lookup es costoso (usa con cuidado)

---

## getStatsByDepartment() en el Proyecto

```java
// NativeMongoUserServiceImpl.java

public List<DepartmentStatsDto> getStatsByDepartment() {
    List<Bson> pipeline = Arrays.asList(
        Aggregates.group(
            "$department",
            Accumulators.sum("totalUsers", 1),
            Accumulators.sum("activeUsers",
                new Document("$cond", Arrays.asList(
                    new Document("$eq",
                        Arrays.asList("$active", true)),
                    1, 0
                ))
            )
        ),
        Aggregates.sort(Sorts.descending("totalUsers"))
    );

    AggregateIterable<Document> results =
        collection.aggregate(pipeline);

    List<DepartmentStatsDto> stats = new ArrayList<>();
    for (Document doc : results) {
        stats.add(new DepartmentStatsDto(
            doc.getString("_id"),
            doc.getInteger("totalUsers"),
            doc.getInteger("activeUsers")
        ));
    }
    return stats;
}
```

---

## Probando la Agregación

### Resultado en Swagger

**Endpoint:** `GET /api/native/users/stats`

```json
[
    {
        "department": "IT",
        "totalUsers": 3,
        "activeUsers": 3
    },
    {
        "department": "HR",
        "totalUsers": 2,
        "activeUsers": 2
    },
    {
        "department": "Finance",
        "totalUsers": 1,
        "activeUsers": 1
    },
    {
        "department": "Sales",
        "totalUsers": 1,
        "activeUsers": 0
    }
]
```

---

## Resumen del Módulo

### Lo Que Hemos Aprendido

| Semana | Tema | Herramientas |
|--------|------|--------------|
| 1 | NoSQL vs SQL | Conceptos, MongoDB |
| 2 | CRUD Básico | Spring Data, Query Methods |
| 3 | CRUD Nativo | API Nativa, Filters, Document |
| 4 | Consultas Dinámicas | MongoTemplate, Criteria |
| 5 | Búsqueda Avanzada | Paginación, Ordenamiento |
| 6 | Agregación | Pipeline, $group, $match |

### Competencias adquiridas:
- ✅ Operaciones CRUD en MongoDB
- ✅ Dos paradigmas de acceso (Spring Data + Nativo)
- ✅ Consultas con filtros dinámicos
- ✅ Agregaciones básicas
- ✅ Defensa técnica oral

---

## Próximos Pasos

### Para Seguir Aprendiendo

**Temas Avanzados:**
- 🔐 Transacciones multi-documento
- 📍 Datos geoespaciales
- 🔄 Change Streams (tiempo real)
- 📊 Índices de texto completo
- ☁️ MongoDB Atlas (cloud)
- 🔀 Sharding y Replicación

**Recursos:**
- [MongoDB University](https://university.mongodb.com) - Cursos gratuitos
- [MongoDB Manual](https://docs.mongodb.com/manual)
- [Aggregation Pipeline Builder](https://www.mongodb.com/docs/compass/current/aggregation-pipeline-builder/)

**Herramientas:**
- MongoDB Compass (GUI visual)
- mongosh (shell moderno)

---

<!-- _class: lead -->

## Reflexión Final

### Después de estas 6 semanas...

¿Cuándo elegirías MongoDB sobre SQL en un proyecto real?

¿Y cuándo NO lo elegirías?

---

<!-- _class: lead -->
<!-- _paginate: false -->

# 🎉 ¡Módulo Completado! 🎉

## 🍃

### MongoDB: Acceso a Datos NoSQL

**Gracias por vuestra participación**

¿Preguntas finales?
