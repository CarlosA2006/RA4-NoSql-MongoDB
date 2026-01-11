# Presentación: Aggregation Framework

> **Duración:** 60 minutos
> **Semana:** 6
> **Bloque:** Teoría - Agregaciones y Estadísticas

---

## DIAPOSITIVA 1: Portada

### Contenido:

```
╔════════════════════════════════════════════════════════════╗
║                                                            ║
║           Aggregation Framework                            ║
║                                                            ║
║         Procesamiento y Análisis de Datos                  ║
║                                                            ║
║     ─────────────────────────────────────                  ║
║                                                            ║
║         Semana 6 - Acceso a Datos                          ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Notas del presentador:**
Última sesión teórica del módulo. Veremos cómo MongoDB puede hacer análisis de datos complejos directamente en la base de datos, sin traer todo a Java.

---

## DIAPOSITIVA 2: El Problema

**Diseño:** Escenario con pregunta

### Contenido:

**Título:** ¿Cómo Obtener Estadísticas?

**Necesidad:** Dashboard de recursos humanos

```
┌─────────────────────────────────────────────────────────────┐
│              📊 Dashboard de Empleados                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   Departamento    Total    Activos    % Activos             │
│   ─────────────────────────────────────────────             │
│   IT              15       14         93%                   │
│   HR               8        8        100%                   │
│   Finance          5        4         80%                   │
│   Marketing        3        3        100%                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**¿Cómo calcular esto eficientemente?**

**Notas del presentador:**
Podríamos traer todos los usuarios a Java y calcular en código, pero con millones de registros sería lento e ineficiente. MongoDB puede hacerlo en el servidor.

---

## DIAPOSITIVA 3: Enfoque Tradicional (Malo)

**Diseño:** Código con advertencias

### Contenido:

**Título:** ❌ No Hagas Esto

```java
// Traer TODOS los usuarios a memoria
List<User> allUsers = userRepository.findAll();

// Agrupar en Java
Map<String, Long> countByDept = allUsers.stream()
    .collect(Collectors.groupingBy(
        User::getDepartment,
        Collectors.counting()
    ));

// Contar activos por departamento
Map<String, Long> activeByDept = allUsers.stream()
    .filter(User::isActive)
    .collect(Collectors.groupingBy(
        User::getDepartment,
        Collectors.counting()
    ));
```

**Problemas:**
- ⚠️ Transfiere TODOS los documentos (red, memoria)
- ⚠️ Procesamiento en cliente (CPU del servidor de app)
- ⚠️ No escala con millones de registros

**Notas del presentador:**
Con 10 usuarios funciona. Con 1 millón, la aplicación se cae. MongoDB puede hacer este cálculo en milisegundos sin transferir datos.

---

## DIAPOSITIVA 4: La Solución: Aggregation

**Diseño:** Definición con concepto clave

### Contenido:

**Título:** Aggregation Framework

> Sistema de procesamiento de datos que ejecuta operaciones **en el servidor MongoDB**, transformando y analizando documentos mediante un **pipeline de etapas**.

```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│ Colección│──▶│  Stage 1 │──▶│  Stage 2 │──▶│  Stage 3 │──▶ Resultado
│ Original │   │ ($match) │   │ ($group) │   │  ($sort) │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
```

**Ventajas:**
- ✅ Procesamiento en el servidor
- ✅ Sin transferencia de datos masiva
- ✅ Optimizado con índices
- ✅ Operaciones complejas en una query

**Notas del presentador:**
Piensa en una línea de montaje: cada etapa transforma los datos y pasa el resultado a la siguiente. Al final obtienes el resultado procesado.

---

## DIAPOSITIVA 5: Concepto de Pipeline

**Diseño:** Diagrama de flujo detallado

### Contenido:

**Título:** El Pipeline de Agregación

```
    Documentos Originales
    ┌────────────────────┐
    │ { name: "Ana", dept: "IT", active: true }    │
    │ { name: "Carlos", dept: "IT", active: true } │
    │ { name: "María", dept: "HR", active: true }  │
    │ { name: "Pedro", dept: "IT", active: false } │
    └────────────────────┘
              │
              ▼
    ┌─────────────────────┐
    │   $match            │  ← Filtrar (opcional)
    │   { active: true }  │
    └─────────────────────┘
              │
              ▼
    ┌─────────────────────────┐
    │   $group                │  ← Agrupar y calcular
    │   { _id: "$dept",       │
    │     count: { $sum: 1 }} │
    └─────────────────────────┘
              │
              ▼
    ┌─────────────────────┐
    │   $sort             │  ← Ordenar resultado
    │   { count: -1 }     │
    └─────────────────────┘
              │
              ▼
    Resultado Final
    ┌────────────────────┐
    │ { _id: "IT", count: 2 }  │
    │ { _id: "HR", count: 1 }  │
    └────────────────────┘
```

**Notas del presentador:**
Cada etapa recibe documentos, los procesa, y pasa los resultados modificados a la siguiente etapa. El orden importa.

---

## DIAPOSITIVA 6: Etapas Principales

**Diseño:** Tabla con iconos

### Contenido:

**Título:** Stages Más Comunes

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

**Notas del presentador:**
Hay más de 30 stages disponibles, pero estos cubren el 90% de los casos. $group es el más potente y complejo.

---

## DIAPOSITIVA 7: $match - Filtrar

**Diseño:** Código con ejemplo

### Contenido:

**Título:** $match: Filtrar Documentos

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

**Buenas prácticas:**
- ✅ Poner $match al principio (usa índices)
- ✅ Filtra temprano para reducir datos en etapas posteriores
- ❌ $match al final procesa documentos innecesarios

**Notas del presentador:**
$match al principio es como WHERE en SQL: reduce el conjunto de datos antes de operar. Es la optimización más importante.

---

## DIAPOSITIVA 8: $group - Agrupar

**Diseño:** Anatomía del $group

### Contenido:

**Título:** $group: El Corazón de la Agregación

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

**Equivalente SQL:**
```sql
SELECT
    department,
    COUNT(*) as totalUsers,
    AVG(age) as avgAge,
    MAX(salary) as maxSalary,
    MIN(salary) as minSalary
FROM users
GROUP BY department
```

**Notas del presentador:**
El _id define cómo agrupar. Puede ser un campo, una expresión, o null (agrupa todo en un solo resultado).

---

## DIAPOSITIVA 9: Operadores de Acumulación

**Diseño:** Tabla de operadores

### Contenido:

**Título:** Operadores para $group

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

**Contar documentos:**
```javascript
{ $sum: 1 }  // Suma 1 por cada documento = contar
```

**Notas del presentador:**
$sum: 1 es el patrón para contar. Cada documento suma 1 al total del grupo.

---

## DIAPOSITIVA 10: $sort y $limit

**Diseño:** Ejemplos simples

### Contenido:

**Título:** Ordenar y Limitar Resultados

**$sort - Ordenar:**
```javascript
{ $sort: { totalUsers: -1 } }  // Descendente (más primero)
{ $sort: { totalUsers: 1 } }   // Ascendente (menos primero)

// Múltiples campos
{ $sort: { department: 1, totalUsers: -1 } }
```

**$limit - Limitar cantidad:**
```javascript
{ $limit: 5 }  // Solo los primeros 5 resultados
```

**$skip - Saltar documentos:**
```javascript
{ $skip: 10 }  // Saltar los primeros 10
```

**Paginación:**
```javascript
[
    { $skip: 20 },   // Página 3 (20 = 10 * 2)
    { $limit: 10 }   // 10 por página
]
```

**Notas del presentador:**
En agregaciones, 1 = ascendente, -1 = descendente (diferente de Sort.Direction de Spring).

---

## DIAPOSITIVA 11: $project - Transformar

**Diseño:** Ejemplos de proyección

### Contenido:

**Título:** $project: Seleccionar y Transformar Campos

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

**Resultado:**
```javascript
{
  name: "Ana García",
  email: "ana@empresa.com",
  fullName: "Ana García",
  isActive: true,
  display: "Ana García - IT"
}
```

**Notas del presentador:**
$project permite crear campos calculados, renombrar, y seleccionar solo lo necesario. Reduce el tamaño de los documentos en memoria.

---

## DIAPOSITIVA 12: Ejemplo Completo - Estadísticas

**Diseño:** Pipeline completo paso a paso

### Contenido:

**Título:** Estadísticas por Departamento

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

**Notas del presentador:**
Este es exactamente el pipeline que está implementado en getStatsByDepartment() del proyecto. Vamos a verlo en Java.

---

## DIAPOSITIVA 13: El Operador $cond

**Diseño:** Explicación detallada

### Contenido:

**Título:** $cond: Condicional IF-THEN-ELSE

```javascript
{
    $cond: [
        <condición>,    // IF
        <valor_si_true>, // THEN
        <valor_si_false> // ELSE
    ]
}
```

**Ejemplo: Contar solo activos**
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

**Notas del presentador:**
$cond es muy potente para agregaciones condicionales. Permite contar, sumar, o calcular solo cuando se cumple una condición.

---

## DIAPOSITIVA 14: Java - API Nativa

**Diseño:** Código Java completo

### Contenido:

**Título:** Agregación en Java (API Nativa)

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
                    new Document("$eq", Arrays.asList("$active", true)),
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

**Notas del presentador:**
La clase Aggregates proporciona métodos para cada stage. Accumulators tiene los operadores de acumulación ($sum, $avg, etc.).

---

## DIAPOSITIVA 15: Java - Spring Data

**Diseño:** Código Spring alternativo

### Contenido:

**Título:** Agregación con Spring Data

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

**Notas del presentador:**
Spring Data tiene su propia API fluida para agregaciones. El mapeo a DepartmentStats es automático.

---

## DIAPOSITIVA 16: Comparación de Sintaxis

**Diseño:** Tres columnas comparativas

### Contenido:

**Título:** La Misma Agregación en 3 Formas

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
  group("$department",
    sum("total", 1)),
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

**Notas del presentador:**
Los conceptos son idénticos, la sintaxis varía. Una vez entendido el concepto en shell, traducirlo a Java es mecánico.

---

## DIAPOSITIVA 17: Casos de Uso Reales

**Diseño:** Lista con ejemplos prácticos

### Contenido:

**Título:** ¿Cuándo Usar Aggregation?

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

**Notas del presentador:**
Aggregation es la herramienta para análisis. Si necesitas "resumir" o "agrupar" datos, probablemente necesitas aggregation.

---

## DIAPOSITIVA 18: Optimización

**Diseño:** Lista de buenas prácticas

### Contenido:

**Título:** Rendimiento en Agregaciones

**✅ Buenas Prácticas:**

1. **$match primero:** Filtra temprano para reducir documentos
```javascript
[{ $match: {...} }, { $group: {...} }]  // ✅ Bien
[{ $group: {...} }, { $match: {...} }]  // ❌ Malo
```

2. **Usa índices:** $match y $sort usan índices si están primero

3. **Limita campos con $project:** Reduce memoria

4. **$limit temprano si es posible:** Menos datos a procesar

**⚠️ Precauciones:**
- Pipeline muy largos pueden ser lentos
- $unwind multiplica documentos exponencialmente
- $lookup es costoso (usa con cuidado)

**Notas del presentador:**
MongoDB tiene un límite de 100MB de memoria por stage. Para operaciones grandes, usar allowDiskUse: true.

---

## DIAPOSITIVA 19: getStatsByDepartment() en el Proyecto

**Diseño:** Código del proyecto real

### Contenido:

**Título:** El Método Implementado

```java
// NativeMongoUserServiceImpl.java

public List<DepartmentStatsDto> getStatsByDepartment() {
    List<Bson> pipeline = Arrays.asList(
        Aggregates.group(
            "$department",
            Accumulators.sum("totalUsers", 1),
            Accumulators.sum("activeUsers",
                new Document("$cond", Arrays.asList(
                    new Document("$eq", Arrays.asList("$active", true)),
                    1, 0
                ))
            )
        ),
        Aggregates.sort(Sorts.descending("totalUsers"))
    );

    AggregateIterable<Document> results = collection.aggregate(pipeline);

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

**Notas del presentador:**
Este código ya está implementado en el proyecto. Analizarlo línea por línea para entender cada parte.

---

## DIAPOSITIVA 20: Probando la Agregación

**Diseño:** Ejemplo de respuesta

### Contenido:

**Título:** Resultado en Swagger

**Endpoint:** `GET /api/native/users/stats`

**Respuesta:**
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
        "department": "Marketing",
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

**Notas del presentador:**
Sales tiene 1 total pero 0 activos porque Miguel Torres está marcado como inactive. El $cond funciona correctamente.

---

## DIAPOSITIVA 21: Resumen del Módulo

**Diseño:** Repaso de las 6 semanas

### Contenido:

**Título:** Lo Que Hemos Aprendido

| Semana | Tema | Herramientas |
|--------|------|--------------|
| 1 | NoSQL vs SQL | Conceptos, MongoDB |
| 2 | CRUD Básico | Spring Data, Query Methods |
| 3 | CRUD Nativo | API Nativa, Filters, Document |
| 4 | Consultas Dinámicas | MongoTemplate, Criteria |
| 5 | Búsqueda Avanzada | Paginación, Ordenamiento |
| 6 | Agregación | Pipeline, $group, $match |

**Competencias adquiridas:**
- ✅ Operaciones CRUD en MongoDB
- ✅ Dos paradigmas de acceso (Spring Data + Nativo)
- ✅ Consultas con filtros dinámicos
- ✅ Agregaciones básicas
- ✅ Defensa técnica oral

**Notas del presentador:**
Repaso rápido de todo el módulo. Los alumnos ahora tienen base sólida para trabajar con MongoDB en proyectos reales.

---

## DIAPOSITIVA 22: Próximos Pasos

**Diseño:** Recursos para seguir aprendiendo

### Contenido:

**Título:** Para Seguir Aprendiendo

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

**Notas del presentador:**
MongoDB University tiene certificaciones gratuitas muy valoradas en la industria. Recomendado para el CV.

---

## DIAPOSITIVA 23: Reflexión Final

**Diseño:** Pregunta de cierre

### Contenido:

**Título:** Pregunta para Reflexionar

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│     Después de estas 6 semanas...                          │
│                                                             │
│     ¿Cuándo elegirías MongoDB sobre SQL                    │
│      en un proyecto real?                                   │
│                                                             │
│     ¿Y cuándo NO lo elegirías?                             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Discusión abierta:** 5 minutos

**Notas del presentador:**
Fomentar debate. No hay respuesta única. La clave es elegir la herramienta correcta para cada caso.

---

## DIAPOSITIVA 24: Final

**Diseño:** Slide de cierre del módulo

### Contenido:

```
╔════════════════════════════════════════════════════════════╗
║                                                            ║
║               🎉 ¡Módulo Completado! 🎉                    ║
║                                                            ║
║                         🍃                                 ║
║                                                            ║
║         MongoDB: Acceso a Datos NoSQL                      ║
║                                                            ║
║     ─────────────────────────────────────                  ║
║                                                            ║
║              Gracias por vuestra participación             ║
║                                                            ║
║                  ¿Preguntas finales?                       ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Notas del presentador:**
Agradecer la participación. Resolver últimas dudas. Despedida del módulo.
