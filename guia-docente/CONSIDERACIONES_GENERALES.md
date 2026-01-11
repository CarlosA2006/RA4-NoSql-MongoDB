# Consideraciones Generales - Unidad Didáctica MongoDB

## Información del Módulo

| Aspecto | Detalle |
|---------|---------|
| **Ciclo Formativo** | Desarrollo de Aplicaciones Multiplataforma (DAM) |
| **Curso** | 2º |
| **Módulo** | Acceso a Datos |
| **Unidad Didáctica** | Acceso a Bases de Datos NoSQL con MongoDB |
| **Duración** | 6 semanas (30 horas) |
| **Horas semanales** | 5 horas |

---

## Resultados de Aprendizaje

Esta unidad contribuye a los siguientes resultados de aprendizaje del módulo:

1. **RA4:** Desarrolla aplicaciones que gestionan información almacenada en bases de datos relacionales y no relacionales, identificando y aplicando mecanismos de acceso.

### Criterios de Evaluación Asociados

- CE4.a) Se han identificado las características de las bases de datos NoSQL y sus diferencias con las relacionales.
- CE4.b) Se han utilizado APIs específicas para el acceso a bases de datos NoSQL.
- CE4.c) Se han implementado operaciones CRUD utilizando diferentes paradigmas de acceso.
- CE4.d) Se han desarrollado consultas complejas utilizando filtros, agregaciones y proyecciones.
- CE4.e) Se han comparado diferentes estrategias de acceso evaluando rendimiento y complejidad.

---

## Objetivos Específicos de la Unidad

Al finalizar esta unidad, el alumnado será capaz de:

1. **Comprender** las diferencias fundamentales entre bases de datos SQL y NoSQL.
2. **Configurar** un entorno de desarrollo con MongoDB y Spring Boot.
3. **Implementar** operaciones CRUD utilizando la API nativa de MongoDB.
4. **Implementar** operaciones CRUD utilizando Spring Data MongoDB.
5. **Comparar** ambos enfoques identificando ventajas e inconvenientes.
6. **Desarrollar** consultas con filtros dinámicos, paginación y ordenamiento.
7. **Comprender** el funcionamiento básico del Aggregation Framework.
8. **Defender oralmente** las soluciones implementadas con criterio técnico.

---

## Metodología

### Enfoque Pedagógico

La unidad sigue un enfoque **práctico-comparativo**:

- **Aprendizaje basado en proyectos:** Todo el trabajo se realiza sobre un proyecto funcional.
- **Metodología dual:** Cada concepto se implementa de dos formas (API Nativa y Spring Data).
- **Evaluación continua:** Defensa oral semanal de los métodos implementados.
- **Sin trabajo en casa:** Todo el trabajo se realiza en el aula.

### Estructura de las Sesiones

Cada semana de 5 horas se estructura típicamente así:

```
┌─────────────────────────────────────────────────────────────┐
│  DEFENSA ORAL (semanas 3-6)                    │  ~1h      │
├─────────────────────────────────────────────────────────────┤
│  TEORÍA + DEMOSTRACIÓN                         │  ~1h      │
├─────────────────────────────────────────────────────────────┤
│  PRÁCTICA GUIADA/AUTÓNOMA                      │  ~2h      │
├─────────────────────────────────────────────────────────────┤
│  TESTS + RESOLUCIÓN DE DUDAS                   │  ~1h      │
└─────────────────────────────────────────────────────────────┘
```

### Atención a la Diversidad

- **Alumnado avanzado:** Ejercicios de ampliación en cada semana.
- **Alumnado con dificultades:** Práctica guiada con apoyo directo del docente.
- **Trabajo colaborativo:** Se permite (y recomienda) consultar entre compañeros durante la práctica.

---

## Sistema de Evaluación

### Instrumento Único: Defensa Oral Continua

La evaluación se basa exclusivamente en la **defensa oral** de los métodos implementados:

| Semana | Métodos a Defender | Peso |
|--------|-------------------|------|
| 3 | `findAll()`, `findByDepartment()`, `countByDepartment()` (Spring Data) | 15% |
| 4 | `findAll()`, `findByDepartment()`, `countByDepartment()` (API Nativa) | 15% |
| 5 | `searchUsers()` (Spring Data) | 20% |
| 6 | `searchUsers()` (API Nativa) | 20% |
| — | Participación y actitud | 30% |

### Rúbrica de Evaluación por Defensa

| Criterio | Excelente (9-10) | Notable (7-8) | Suficiente (5-6) | Insuficiente (0-4) |
|----------|------------------|---------------|------------------|-------------------|
| **Funcionalidad** | Tests pasan completamente | Funciona con warnings menores | Funciona parcialmente | No funciona o no compila |
| **Comprensión del código** | Explica el "por qué" de cada decisión | Explica correctamente el flujo | Explica con ayuda del docente | No sabe explicar su código |
| **Uso de la API** | Uso idiomático y eficiente | Uso correcto pero mejorable | Uso básico funcional | Uso incorrecto |
| **Comparativa** | Relaciona y compara ambos módulos | Menciona diferencias | Solo describe su solución | No compara |
| **Comunicación** | Clara, estructurada, técnica | Clara pero poco estructurada | Confusa pero comprensible | Incoherente |

### Calificación por Método

Cada método se califica de 0 a 10 según la rúbrica. La nota final se calcula como media ponderada.

### Recuperación

- **Métodos no defendidos:** Se pueden defender en la semana siguiente con penalización de -1 punto.
- **Métodos suspensos:** Se pueden volver a defender en la última semana (semana 6) tras el cierre.
- **No presentados:** Deben presentarse en convocatoria ordinaria/extraordinaria.

---

## Recursos Necesarios

### Hardware
- Ordenador con mínimo 8GB RAM (recomendado 16GB)
- Espacio en disco: 2GB libres

### Software
- **JDK 21** (OpenJDK o similar)
- **IDE:** IntelliJ IDEA (recomendado) o VS Code con extensiones Java
- **Git** (para clonar el proyecto)
- **Navegador web** (para Swagger UI)

### No es necesario instalar
- MongoDB (se usa versión embebida)
- Herramientas de base de datos externas

### Documentación de Referencia
- [MongoDB Manual](https://www.mongodb.com/docs/manual/)
- [Spring Data MongoDB Reference](https://docs.spring.io/spring-data/mongodb/reference/)
- [MongoDB Java Driver](https://www.mongodb.com/docs/drivers/java/sync/current/)

---

## Estructura del Proyecto

```
proyecto-pedagogico-mongodb/
├── src/main/java/com/dam/accesodatos/
│   ├── mongodb/
│   │   ├── nativeapi/          ← API Nativa MongoDB
│   │   │   ├── NativeMongoUserService.java
│   │   │   └── NativeMongoUserServiceImpl.java  ★ 4 TODOs
│   │   └── springdata/         ← Spring Data MongoDB
│   │       ├── UserRepository.java
│   │       ├── SpringDataUserService.java
│   │       └── SpringDataUserServiceImpl.java   ★ 4 TODOs
│   ├── model/                  ← Entidades y DTOs
│   ├── controller/             ← REST Controllers
│   ├── exception/              ← Excepciones personalizadas
│   └── config/                 ← Configuración
├── src/test/java/              ← Tests de integración
├── README.md                   ← Documentación técnica
├── ARQUITECTURA.md             ← Diseño del sistema
└── INSTRUCCIONES_USO.md        ← Guía rápida
```

---

## Métodos a Implementar (TODOs)

### Módulo Spring Data

| Método | Archivo | Complejidad | Semana |
|--------|---------|-------------|--------|
| `findAll()` | SpringDataUserServiceImpl.java:129 | Baja (1 línea) | 2 |
| `findByDepartment()` | SpringDataUserServiceImpl.java:137 | Baja (1 línea) | 2 |
| `countByDepartment()` | SpringDataUserServiceImpl.java:158 | Baja (1 línea) | 2 |
| `searchUsers()` | SpringDataUserServiceImpl.java:146 | Alta (~20 líneas) | 4 |

### Módulo API Nativa

| Método | Archivo | Complejidad | Semana |
|--------|---------|-------------|--------|
| `findAll()` | NativeMongoUserServiceImpl.java | Media (~10 líneas) | 3 |
| `findByDepartment()` | NativeMongoUserServiceImpl.java | Media (~10 líneas) | 3 |
| `countByDepartment()` | NativeMongoUserServiceImpl.java | Baja (~3 líneas) | 3 |
| `searchUsers()` | NativeMongoUserServiceImpl.java | Alta (~30 líneas) | 5 |

---

## Comandos Útiles

### Compilar y Ejecutar
```bash
# Compilar el proyecto
./gradlew build

# Ejecutar la aplicación
./gradlew bootRun

# La aplicación estará disponible en http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Ejecutar Tests
```bash
# Todos los tests
./gradlew test

# Solo tests de Spring Data
./gradlew test --tests "*SpringData*"

# Solo tests de API Nativa
./gradlew test --tests "*Native*"

# Test específico
./gradlew test --tests "SpringDataUserServiceTest.findAllTest"
```

### Verificar Estado
```bash
# Ver estado de compilación
./gradlew build --info

# Limpiar y recompilar
./gradlew clean build
```

---

## Datos de Prueba

El proyecto carga automáticamente 8 usuarios al iniciar:

| Nombre | Email | Departamento | Rol | Activo |
|--------|-------|--------------|-----|--------|
| Ana García | ana.garcia@empresa.com | IT | Developer | Sí |
| Carlos López | carlos.lopez@empresa.com | IT | Senior Developer | Sí |
| María Rodríguez | maria.rodriguez@empresa.com | HR | Manager | Sí |
| Pedro Martínez | pedro.martinez@empresa.com | Finance | Analyst | Sí |
| Laura Sánchez | laura.sanchez@empresa.com | IT | Tech Lead | Sí |
| Roberto Fernández | roberto.fernandez@empresa.com | HR | Recruiter | Sí |
| Carmen Díaz | carmen.diaz@empresa.com | Marketing | Designer | Sí |
| Miguel Torres | miguel.torres@empresa.com | Sales | Representative | **No** |

---

## Errores Comunes y Soluciones

### Error: "Port 8080 already in use"
```bash
# Buscar proceso usando el puerto
lsof -i :8080
# Matar el proceso
kill -9 <PID>
```

### Error: Tests fallan con "UnsupportedOperationException"
Es el comportamiento esperado para métodos TODO no implementados. Los tests pasarán cuando implementes el método.

### Error: "duplicate key error" al crear usuario
El email debe ser único. Usa un email diferente o reinicia la aplicación (los datos se borran al parar).

### Error: MongoDB no arranca
Verifica que tienes suficiente espacio en disco (mínimo 500MB libres) y que no hay otra instancia de MongoDB ejecutándose.

---

## Planificación Semanal

| Semana | Contenido Principal | Defensa |
|--------|--------------------| --------|
| 1 | Introducción a MongoDB y puesta en marcha | — |
| 2 | Implementar 3 TODOs Spring Data | — |
| 3 | Implementar 3 TODOs API Nativa | 3 métodos SD |
| 4 | Implementar searchUsers() Spring Data | 3 métodos Nativa |
| 5 | Implementar searchUsers() API Nativa | searchUsers() SD |
| 6 | Aggregation Pipeline + Cierre | searchUsers() Nativa |

---

## Contacto y Soporte

Durante las sesiones, el docente está disponible para:
- Resolver dudas técnicas
- Guiar en la implementación
- Preparar las defensas orales
- Proporcionar feedback continuo

**Recuerda:** Todo el trabajo se realiza en clase. No hay tareas para casa.
