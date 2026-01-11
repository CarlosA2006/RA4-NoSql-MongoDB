# Semana 1: Introducción a MongoDB y Puesta en Marcha

## Información de la Sesión

| Aspecto | Detalle |
|---------|---------|
| **Duración** | 5 horas |
| **Defensa oral** | No hay (primera semana) |
| **Objetivo principal** | Familiarizarse con MongoDB y el proyecto |
| **Entregable** | Ninguno |

---

## Objetivos de Aprendizaje

Al finalizar esta semana, el alumnado será capaz de:

1. Explicar las diferencias fundamentales entre bases de datos SQL y NoSQL.
2. Describir la estructura de documentos BSON y colecciones en MongoDB.
3. Ejecutar el proyecto y verificar su correcto funcionamiento.
4. Navegar por la estructura del proyecto identificando los componentes principales.
5. Utilizar Swagger UI para probar los endpoints REST implementados.
6. Comparar visualmente el código de ambos módulos (API Nativa vs Spring Data).

---

## Temporización Detallada

### Bloque 1: Teoría - NoSQL vs SQL (1 hora)

#### Contenidos Teóricos

**¿Qué es NoSQL?**
- "Not Only SQL" - Bases de datos no relacionales
- Diseñadas para escalabilidad horizontal
- Flexibilidad en el esquema de datos
- Diferentes tipos: documentos, clave-valor, grafos, columnas

**MongoDB como Base de Datos Documental**
- Almacena datos en documentos BSON (Binary JSON)
- Colecciones en lugar de tablas
- Sin esquema fijo (schema-less)
- Anidación de documentos

**Comparativa SQL vs MongoDB**

| Concepto SQL | Equivalente MongoDB |
|--------------|---------------------|
| Base de datos | Base de datos |
| Tabla | Colección |
| Fila | Documento |
| Columna | Campo |
| JOIN | Documentos embebidos / $lookup |
| PRIMARY KEY | _id (ObjectId) |

**Cuándo usar MongoDB**
- Datos con estructura variable
- Alta velocidad de escritura
- Escalabilidad horizontal necesaria
- Prototipado rápido

**Cuándo NO usar MongoDB**
- Transacciones complejas multi-documento
- Relaciones muy complejas entre entidades
- Requisitos estrictos de integridad referencial

#### Actividad Práctica (10 min)

Mostrar ejemplos de documentos JSON/BSON:

```json
// Documento de usuario en MongoDB
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "name": "Ana García",
  "email": "ana.garcia@empresa.com",
  "department": "IT",
  "role": "Developer",
  "active": true,
  "createdAt": ISODate("2024-01-15T10:30:00Z"),
  "skills": ["Java", "Spring", "MongoDB"],
  "address": {
    "city": "Madrid",
    "country": "España"
  }
}
```

Comparar con su equivalente en tablas SQL (necesitaría tablas adicionales para skills y address).

---

### Bloque 2: Puesta en Marcha del Proyecto (45 min)

#### Paso 1: Clonar o Copiar el Proyecto (10 min)

```bash
# Si se usa Git
git clone <url-del-repositorio>
cd proyecto-pedagogico-mongodb

# O copiar desde ubicación compartida
cp -r /ruta/compartida/proyecto-pedagogico-mongodb ~/
cd proyecto-pedagogico-mongodb
```

#### Paso 2: Verificar Requisitos (5 min)

```bash
# Verificar Java 21
java -version
# Debe mostrar: openjdk version "21.x.x"

# Verificar que Gradle wrapper funciona
./gradlew --version
```

#### Paso 3: Compilar el Proyecto (10 min)

```bash
# Compilar (descargará dependencias la primera vez)
./gradlew build

# Salida esperada:
# BUILD SUCCESSFUL in Xs
```

**Posibles errores:**
- "JAVA_HOME not set": Configurar variable de entorno
- "Could not resolve dependencies": Verificar conexión a Internet
- "Permission denied": Ejecutar `chmod +x gradlew`

#### Paso 4: Ejecutar la Aplicación (10 min)

```bash
./gradlew bootRun
```

**Salida esperada:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

MongoDB embebido iniciado en puerto 27017
Tomcat started on port(s): 8080
Started MongoDbTeachingApplication in X seconds
```

#### Paso 5: Verificar Funcionamiento (10 min)

Abrir navegador en: `http://localhost:8080/swagger-ui.html`

Verificar que aparecen dos grupos de endpoints:
- **native-mongo-controller**: Operaciones con API Nativa
- **spring-data-controller**: Operaciones con Spring Data

---

### Bloque 3: Explorar Swagger UI (1 hora)

#### Introducción a Swagger/OpenAPI (15 min)

- Swagger UI es una interfaz web para probar APIs REST
- Genera documentación automática desde el código
- Permite ejecutar peticiones sin usar curl o Postman

#### Práctica Guiada: Probar Endpoints (45 min)

**1. Test de Conexión**

Expandir `GET /api/native/test` y hacer clic en "Try it out" → "Execute"

Respuesta esperada:
```json
{
  "message": "Conexión exitosa | Colección: users | Documentos: 8"
}
```

Repetir con `GET /api/spring/test`

**2. Listar Usuarios (método implementado)**

Probar `GET /api/native/users/{id}` con un ID válido.

Primero, obtener un ID ejecutando el test de conexión y observando los logs de la consola, o probar con el endpoint de estadísticas.

**3. Crear un Usuario**

Probar `POST /api/spring/users` con el siguiente body:

```json
{
  "name": "Nuevo Usuario",
  "email": "nuevo@test.com",
  "department": "IT",
  "role": "Junior Developer"
}
```

**4. Actualizar un Usuario**

Usar el ID del usuario creado con `PUT /api/spring/users/{id}`:

```json
{
  "name": "Usuario Actualizado",
  "role": "Senior Developer"
}
```

**5. Eliminar un Usuario**

Probar `DELETE /api/native/users/{id}` con el ID del usuario creado.

#### Ejercicio Individual (15 min)

Cada alumno debe:
1. Crear un usuario con sus propios datos (nombre real, email ficticio)
2. Actualizar el rol del usuario
3. Verificar que aparece usando el endpoint de búsqueda por ID
4. Anotar cualquier error o comportamiento inesperado

---

### Bloque 4: Estructura del Proyecto (1 hora)

#### Navegación por el Código (30 min)

Abrir el proyecto en el IDE y explorar:

**1. Punto de Entrada**
```
src/main/java/com/dam/accesodatos/MongoDbTeachingApplication.java
```
- Clase principal con `@SpringBootApplication`
- Punto de arranque de la aplicación

**2. Modelo de Datos**
```
src/main/java/com/dam/accesodatos/model/User.java
```
- Entidad principal con anotaciones `@Document`, `@Id`, `@Indexed`
- Campos: id, name, email, department, role, active, createdAt, updatedAt

**3. Módulo Spring Data**
```
src/main/java/com/dam/accesodatos/mongodb/springdata/
├── UserRepository.java          ← Interface del repositorio
├── SpringDataUserService.java   ← Interface del servicio
└── SpringDataUserServiceImpl.java ← Implementación (AQUÍ ESTÁN LOS TODOs)
```

**4. Módulo API Nativa**
```
src/main/java/com/dam/accesodatos/mongodb/nativeapi/
├── NativeMongoUserService.java      ← Interface del servicio
└── NativeMongoUserServiceImpl.java  ← Implementación (AQUÍ ESTÁN LOS TODOs)
```

**5. Controllers REST**
```
src/main/java/com/dam/accesodatos/controller/
├── SpringDataController.java    ← Endpoints /api/spring/*
└── NativeMongoController.java   ← Endpoints /api/native/*
```

#### Lectura Guiada del README (30 min)

Abrir `README.md` y leer en grupo:

1. **Sección "Estructura del Proyecto"**: Entender la organización
2. **Sección "Métodos Implementados"**: Ver qué ya funciona
3. **Sección "Métodos TODO"**: Identificar lo que hay que implementar
4. **Sección "Comparativa"**: Entender las diferencias entre módulos

**Pregunta de reflexión:** ¿Por qué crees que el proyecto tiene dos formas de hacer lo mismo?

---

### Bloque 5: Análisis Comparativo (45 min)

#### Comparar createUser() en Ambos Módulos (30 min)

Abrir lado a lado:
- `SpringDataUserServiceImpl.java` → método `createUser()`
- `NativeMongoUserServiceImpl.java` → método `createUser()`

**Spring Data (líneas 55-74):**
```java
public User createUser(UserCreateDto dto) {
    User user = new User(dto.getName(), dto.getEmail(),
                         dto.getDepartment(), dto.getRole());
    user.setActive(true);
    user.setCreatedAt(LocalDateTime.now());
    user.setUpdatedAt(LocalDateTime.now());

    User savedUser = userRepository.save(user);  // ← Una línea para guardar
    return savedUser;
}
```

**API Nativa (resumen):**
```java
public User createUser(UserCreateDto dto) {
    // 1. Crear documento BSON manualmente
    Document doc = new Document()
        .append("name", dto.getName())
        .append("email", dto.getEmail())
        // ... más campos

    // 2. Insertar en la colección
    collection.insertOne(doc);

    // 3. Recuperar el ID generado
    ObjectId id = doc.getObjectId("_id");

    // 4. Mapear Document a User manualmente
    return mapDocumentToUser(doc);
}
```

**Discusión en Clase:**
- ¿Cuántas líneas tiene cada versión?
- ¿Cuál es más fácil de leer?
- ¿Cuál te da más control sobre lo que sucede?
- ¿Cuál preferirías usar en un proyecto real? ¿Por qué?

#### Identificar los Métodos TODO (15 min)

Buscar en ambos archivos los comentarios `// TODO`:

**Spring Data - 4 TODOs:**
1. `findAll()` - línea 129
2. `findUsersByDepartment()` - línea 137
3. `searchUsers()` - línea 146
4. `countByDepartment()` - línea 158

**API Nativa - 4 TODOs:**
1. `findAll()`
2. `findUsersByDepartment()`
3. `searchUsers()`
4. `countByDepartment()`

**Observar las pistas:** Cada TODO incluye comentarios con instrucciones paso a paso.

---

### Bloque 6: Cierre y Preparación (30 min)

#### Resumen de la Sesión (10 min)

Puntos clave aprendidos:
- MongoDB es una base de datos documental NoSQL
- El proyecto tiene dos módulos que hacen lo mismo de formas diferentes
- Spring Data abstrae la complejidad, API Nativa da más control
- Swagger UI permite probar la API sin escribir código cliente

#### Presentación de la Dinámica de Evaluación (15 min)

Explicar:
- **Evaluación continua:** Cada semana se defienden los métodos de la semana anterior
- **Defensa oral:** 3-5 minutos explicando qué hace el código y por qué
- **Sin trabajo en casa:** Todo se hace en clase

Calendario de defensas:
| Semana | Qué se defiende |
|--------|-----------------|
| 3 | 3 métodos Spring Data |
| 4 | 3 métodos API Nativa |
| 5 | searchUsers() Spring Data |
| 6 | searchUsers() API Nativa |

#### Dudas y Preguntas (5 min)

Resolver cualquier duda antes de la próxima sesión.

---

## Materiales de Apoyo

### Para el Docente
- Presentación de diapositivas sobre NoSQL (opcional)
- Proyecto funcionando para demostración
- Acceso a logs de la aplicación para debugging

### Para el Alumnado
- `README.md` del proyecto
- `ARQUITECTURA.md` para consulta
- `INSTRUCCIONES_USO.md` como referencia rápida

---

## Evaluación Formativa

Durante esta sesión, observar:

| Indicador | Qué observar |
|-----------|--------------|
| Participación | ¿Hacen preguntas? ¿Responden cuando se les pregunta? |
| Comprensión | ¿Logran ejecutar el proyecto sin ayuda? |
| Interés | ¿Exploran más allá de lo pedido? |
| Colaboración | ¿Ayudan a compañeros con dificultades? |

No hay calificación esta semana, pero estas observaciones informan el componente de "Participación y actitud" (30% de la nota final).

---

## Posibles Problemas y Soluciones

| Problema | Solución |
|----------|----------|
| Java no instalado | Instalar OpenJDK 21 |
| Puerto 8080 ocupado | `lsof -i :8080` y matar proceso |
| Gradle no descarga dependencias | Verificar proxy/firewall |
| IDE no reconoce el proyecto | Importar como proyecto Gradle |
| MongoDB embebido falla | Verificar espacio en disco (>500MB) |

---

## Notas para la Próxima Semana

La semana 2 comenzará directamente con la implementación de los 3 TODOs sencillos de Spring Data:
- `findAll()`
- `findByDepartment()`
- `countByDepartment()`

Recordar a los alumnos que lean las pistas en los comentarios TODO antes de la siguiente sesión (lectura voluntaria, no obligatoria).
