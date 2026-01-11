# Semana 6: Aggregation Pipeline + Defensa Final

## Información de la Sesión

| Aspecto | Detalle |
|---------|---------|
| **Duración** | 5 horas |
| **Defensa oral** | searchUsers() API Nativa (última defensa) |
| **Objetivo principal** | Comprender el Aggregation Framework |
| **Entregable** | Proyecto completo evaluado |

---

## Objetivos de Aprendizaje

Al finalizar esta semana, el alumnado será capaz de:

1. Defender el método searchUsers() de API Nativa con criterio técnico.
2. Explicar qué es el Aggregation Framework y cuándo usarlo.
3. Analizar un pipeline de agregación existente.
4. Identificar las etapas principales: $match, $group, $sort, $project.
5. Comparar las dos aproximaciones aprendidas (API Nativa vs Spring Data).
6. Reflexionar sobre el aprendizaje y aplicaciones futuras.

---

## Temporización Detallada

### Bloque 1: Defensa Final - searchUsers() API Nativa (1 hora)

#### Características de la Defensa Final

Esta es la última defensa y se espera el nivel más alto:
- Tiempo por alumno: 5-6 minutos
- Se evaluará la evolución desde las primeras defensas
- Preguntas comparativas entre ambos módulos

#### Puntos a Evaluar

**Implementación:**
- ¿Funcionan todos los filtros correctamente?
- ¿La paginación está bien implementada?
- ¿El ordenamiento es configurable?

**Comprensión Profunda:**
- ¿Puede explicar cada línea de código?
- ¿Entiende la diferencia con Spring Data?
- ¿Sabe cuándo usar cada enfoque?

**Madurez Técnica:**
- ¿Responde con seguridad?
- ¿Usa terminología correcta?
- ¿Reconoce limitaciones?

#### Preguntas Tipo (Nivel Avanzado)

- "Compara tu implementación con la de Spring Data: ¿cuáles son las diferencias principales?"
- "¿Por qué `Filters.and()` no funciona con lista vacía y cómo lo solucionaste?"
- "Si tuvieras que añadir un filtro para 'createdAt > fecha', ¿cómo lo harías?"
- "¿Qué ventajas tiene la API nativa sobre Spring Data para este método específico?"
- "¿Qué consulta MongoDB se ejecuta con estos parámetros: name='Ana', limit=5?"

#### Registro de Evaluación Final

| Alumno | Implementación | Explicación | Comparativa | Preguntas | Nota |
|--------|----------------|-------------|-------------|-----------|------|
| | /10 | /10 | /10 | /10 | |

#### Feedback Final Colectivo (15 min)

- Resumen de la evolución del grupo
- Errores más comunes y cómo evitarlos
- Reconocimiento de trabajos destacados

---

### Bloque 2: Cierre de Evaluación (30 min)

#### Cálculo de Notas Finales (15 min)

Recordar la distribución de pesos:

| Componente | Peso |
|------------|------|
| Defensa semana 3 (3 métodos SD) | 15% |
| Defensa semana 4 (3 métodos Nativa) | 15% |
| Defensa semana 5 (searchUsers SD) | 20% |
| Defensa semana 6 (searchUsers Nativa) | 20% |
| Participación y actitud | 30% |

#### Comunicación de Notas (15 min)

- Feedback individual breve
- Posibilidad de recuperación si aplica
- Resolver dudas sobre calificaciones

---

### Bloque 3: Teoría - Aggregation Framework (1 hora)

#### ¿Qué es el Aggregation Framework? (15 min)

El Aggregation Framework es un sistema de procesamiento de datos en MongoDB que permite:
- Transformar documentos
- Agrupar datos
- Calcular estadísticas
- Realizar operaciones complejas

**Analogía SQL:**
```sql
SELECT department, COUNT(*) as total, AVG(salary) as avgSalary
FROM users
WHERE active = true
GROUP BY department
ORDER BY total DESC;
```

**Equivalente MongoDB:**
```javascript
db.users.aggregate([
  { $match: { active: true } },
  { $group: {
      _id: "$department",
      total: { $sum: 1 },
      avgSalary: { $avg: "$salary" }
  }},
  { $sort: { total: -1 } }
])
```

#### Concepto de Pipeline (15 min)

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Documentos │ ──▶ │   $match    │ ──▶ │   $group    │ ──▶ │   $sort     │ ──▶ Resultado
│  Originales │     │  (filtrar)  │     │  (agrupar)  │     │  (ordenar)  │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
```

- Cada etapa (stage) transforma los documentos
- La salida de una etapa es la entrada de la siguiente
- El orden de las etapas importa

#### Etapas Principales (30 min)

**$match - Filtrar documentos:**
```javascript
{ $match: { active: true } }
{ $match: { department: "IT", role: { $in: ["Developer", "Lead"] } } }
```

**$group - Agrupar y calcular:**
```javascript
{
  $group: {
    _id: "$department",           // Campo para agrupar
    totalUsers: { $sum: 1 },      // Contar documentos
    avgAge: { $avg: "$age" },     // Promedio
    maxSalary: { $max: "$salary" }, // Máximo
    names: { $push: "$name" }     // Array con nombres
  }
}
```

**$sort - Ordenar:**
```javascript
{ $sort: { totalUsers: -1 } }     // Descendente
{ $sort: { department: 1 } }      // Ascendente
```

**$project - Seleccionar campos:**
```javascript
{
  $project: {
    name: 1,                      // Incluir
    email: 1,                     // Incluir
    _id: 0,                       // Excluir
    fullName: { $concat: ["$name", " - ", "$department"] } // Calcular
  }
}
```

**$limit y $skip:**
```javascript
{ $skip: 10 }
{ $limit: 5 }
```

---

### Bloque 4: Análisis de getStatsByDepartment() (1 hora)

#### Ubicar el Método (10 min)

El proyecto incluye un método de agregación ya implementado:
- `NativeMongoUserServiceImpl.java` → `getStatsByDepartment()`
- `SpringDataUserServiceImpl.java` → Usa MongoTemplate con Aggregation

#### Análisis Línea por Línea - API Nativa (25 min)

```java
public List<DepartmentStatsDto> getStatsByDepartment() {
    log.debug("Obteniendo estadísticas por departamento");

    MongoCollection<Document> collection = getCollection();
    List<DepartmentStatsDto> stats = new ArrayList<>();

    // Definir el pipeline de agregación
    List<Bson> pipeline = Arrays.asList(
        // Etapa 1: Agrupar por departamento
        Aggregates.group(
            "$department",  // Campo para agrupar (_id será el departamento)
            Accumulators.sum("totalUsers", 1),  // Contar usuarios
            Accumulators.sum("activeUsers",     // Contar activos condicionalmente
                new Document("$cond", Arrays.asList(
                    new Document("$eq", Arrays.asList("$active", true)),
                    1,
                    0
                ))
            )
        ),
        // Etapa 2: Ordenar por total descendente
        Aggregates.sort(Sorts.descending("totalUsers"))
    );

    // Ejecutar el pipeline
    AggregateIterable<Document> results = collection.aggregate(pipeline);

    // Mapear resultados
    for (Document doc : results) {
        DepartmentStatsDto dto = new DepartmentStatsDto();
        dto.setDepartment(doc.getString("_id"));
        dto.setTotalUsers(doc.getInteger("totalUsers"));
        dto.setActiveUsers(doc.getInteger("activeUsers"));
        stats.add(dto);
    }

    return stats;
}
```

**Desglose del Pipeline:**

```
Documentos originales:
┌──────────────────────────────────────────────────┐
│ { name: "Ana", department: "IT", active: true }  │
│ { name: "Carlos", department: "IT", active: true }│
│ { name: "María", department: "HR", active: true }│
│ { name: "Miguel", department: "IT", active: false}│
└──────────────────────────────────────────────────┘
                        │
                        ▼
               $group por department
                        │
                        ▼
┌──────────────────────────────────────────────────┐
│ { _id: "IT", totalUsers: 3, activeUsers: 2 }     │
│ { _id: "HR", totalUsers: 1, activeUsers: 1 }     │
└──────────────────────────────────────────────────┘
                        │
                        ▼
              $sort por totalUsers desc
                        │
                        ▼
┌──────────────────────────────────────────────────┐
│ { _id: "IT", totalUsers: 3, activeUsers: 2 }     │
│ { _id: "HR", totalUsers: 1, activeUsers: 1 }     │
└──────────────────────────────────────────────────┘
```

#### El Operador $cond (10 min)

```java
// $cond es un operador condicional: if-then-else
new Document("$cond", Arrays.asList(
    // Condición: ¿active es true?
    new Document("$eq", Arrays.asList("$active", true)),
    // Si es true: sumar 1
    1,
    // Si es false: sumar 0
    0
))
```

Equivale a SQL:
```sql
SUM(CASE WHEN active = true THEN 1 ELSE 0 END) as activeUsers
```

#### Probar el Endpoint (15 min)

Ejecutar la aplicación y probar:

```bash
curl http://localhost:8080/api/native/users/stats
```

Respuesta esperada:
```json
[
  { "department": "IT", "totalUsers": 3, "activeUsers": 3 },
  { "department": "HR", "totalUsers": 2, "activeUsers": 2 },
  { "department": "Finance", "totalUsers": 1, "activeUsers": 1 },
  { "department": "Marketing", "totalUsers": 1, "activeUsers": 1 },
  { "department": "Sales", "totalUsers": 1, "activeUsers": 0 }
]
```

---

### Bloque 5: Repaso Global y Comparativa (1 hora)

#### Cuándo Usar Cada Enfoque (20 min)

| Escenario | Recomendación |
|-----------|---------------|
| CRUD simple | Spring Data (query methods) |
| Consultas con filtros fijos | Spring Data (query methods) |
| Filtros dinámicos complejos | MongoTemplate / API Nativa |
| Agregaciones y estadísticas | Aggregation Framework |
| Máximo control y optimización | API Nativa |
| Desarrollo rápido | Spring Data |
| Equipo nuevo en MongoDB | Spring Data |
| Migración desde SQL | Spring Data |

#### Resumen de lo Aprendido (20 min)

**Semana 1:** Introducción
- Diferencias SQL vs NoSQL
- Estructura del proyecto
- Swagger UI

**Semana 2:** Spring Data Básico
- MongoRepository
- Query methods derivados
- findAll, findByDepartment, countByDepartment

**Semana 3:** API Nativa Básica
- MongoCollection y Document
- Filters para consultas
- Mapeo manual

**Semana 4:** Spring Data Avanzado
- MongoTemplate y Criteria
- Filtros dinámicos
- Paginación y ordenamiento

**Semana 5:** API Nativa Avanzada
- FindIterable
- Sorts para ordenamiento
- searchUsers completo

**Semana 6:** Aggregation
- Pipeline de agregación
- Etapas: $match, $group, $sort
- Estadísticas agregadas

#### Reflexión Guiada (20 min)

Preguntas para discutir en grupo:

1. **¿Qué fue lo más difícil de aprender?**
   - Probablemente: filtros dinámicos, mapeo manual

2. **¿Qué método preferís: Spring Data o API Nativa?**
   - Discutir trade-offs

3. **¿Dónde aplicaríais MongoDB en un proyecto real?**
   - Ejemplos: logs, catálogos, datos de sesión

4. **¿Qué mejoraríais del proyecto?**
   - Feedback constructivo

---

### Bloque 6: Cierre del Módulo (30 min)

#### Recursos para Seguir Aprendiendo (10 min)

**Documentación Oficial:**
- [MongoDB Manual](https://www.mongodb.com/docs/manual/)
- [MongoDB University](https://university.mongodb.com/) - Cursos gratuitos
- [Spring Data MongoDB Reference](https://docs.spring.io/spring-data/mongodb/reference/)

**Temas Avanzados para Explorar:**
- Transacciones multi-documento
- Índices compuestos y de texto
- Change Streams (datos en tiempo real)
- Sharding y replicación
- MongoDB Atlas (cloud)

**Herramientas Útiles:**
- MongoDB Compass (GUI)
- mongosh (shell moderno)
- Robo 3T / Studio 3T

#### Aplicación en el Mundo Real (10 min)

**Empresas que usan MongoDB:**
- Netflix (gestión de contenido)
- Uber (datos de geolocalización)
- Adobe (perfiles de usuario)
- EA Games (datos de jugadores)

**Casos de uso ideales:**
- Catálogos de productos
- Gestión de contenido (CMS)
- Datos de IoT
- Aplicaciones móviles
- Analytics y logging

#### Despedida y Agradecimientos (10 min)

- Reconocimiento del esfuerzo del grupo
- Invitación a seguir explorando
- Feedback sobre el módulo
- Cierre formal

---

## Ejercicio Opcional de Ampliación

Para alumnos interesados en profundizar:

### Crear una Nueva Agregación

Implementar `getUsersByRole()` que devuelva estadísticas por rol:

```java
// Resultado esperado:
[
  { "role": "Developer", "count": 2, "departments": ["IT"] },
  { "role": "Manager", "count": 1, "departments": ["HR"] },
  ...
]
```

**Pistas:**
```java
List<Bson> pipeline = Arrays.asList(
    Aggregates.group(
        "$role",
        Accumulators.sum("count", 1),
        Accumulators.addToSet("departments", "$department")
    ),
    Aggregates.sort(Sorts.descending("count"))
);
```

---

## Notas para la Evaluación de Participación

Durante las 6 semanas, observar:

| Criterio | Indicadores Positivos |
|----------|----------------------|
| Asistencia | Presente en todas las sesiones |
| Puntualidad | Llega a tiempo, aprovecha el tiempo |
| Participación | Hace preguntas, responde, colabora |
| Autonomía | Resuelve problemas antes de preguntar |
| Ayuda a compañeros | Explica a otros sin hacer el trabajo por ellos |
| Actitud | Positiva, receptiva a feedback |
| Progreso | Mejora visible desde semana 1 a 6 |

**Nota de participación (30%):**
- 10: Excepcional en todos los criterios
- 8-9: Muy bueno, destaca en varios
- 6-7: Adecuado, cumple expectativas
- 5: Mínimo aceptable
- <5: Insuficiente participación

---

## Checklist Final del Módulo

### Para el Alumno

- [ ] He defendido todos mis métodos
- [ ] Todos los tests pasan (18/18)
- [ ] Entiendo la diferencia entre Spring Data y API Nativa
- [ ] Puedo explicar qué es un pipeline de agregación
- [ ] Sé cuándo usar cada enfoque
- [ ] He revisado mi nota y entiendo la evaluación

### Para el Docente

- [ ] Todas las defensas completadas
- [ ] Notas calculadas y comunicadas
- [ ] Feedback individual dado
- [ ] Recuperaciones programadas si aplica
- [ ] Cierre formal del módulo realizado

---

## Resumen del Módulo

```
╔═══════════════════════════════════════════════════════════════════╗
║                    MÓDULO MONGODB COMPLETADO                      ║
╠═══════════════════════════════════════════════════════════════════╣
║  Duración: 6 semanas (30 horas)                                   ║
║  Métodos implementados: 8 (4 Spring Data + 4 API Nativa)          ║
║  Tests: 18 (100% cobertura de métodos)                            ║
║  Defensas orales: 4 rondas                                        ║
║                                                                   ║
║  Competencias adquiridas:                                         ║
║  ✓ Operaciones CRUD en MongoDB                                    ║
║  ✓ Consultas con filtros dinámicos                                ║
║  ✓ Paginación y ordenamiento                                      ║
║  ✓ Aggregation Framework básico                                   ║
║  ✓ Comparativa de paradigmas de acceso                            ║
║  ✓ Defensa técnica oral                                           ║
╚═══════════════════════════════════════════════════════════════════╝
```
