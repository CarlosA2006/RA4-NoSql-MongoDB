# Instrucciones de Uso - Proyecto Pedagógico MongoDB

## Estado del Proyecto

✅ **PROYECTO 100% FUNCIONAL**
- Compilación: ✅ Exitosa
- Tests: ✅ 10/10 pasando (100%)
- MongoDB Embebido: ✅ Configurado y funcionando (v7.0.2)
- Documentación: ✅ Completa en español

## Inicio Rápido

### 1. Compilar el Proyecto

```bash
cd proyecto-pedagogico-mongodb
./gradlew clean compileJava
```

### 2. Ejecutar Tests

```bash
./gradlew test
```

**Resultado esperado**: 10 tests ejecutados, 10 exitosos
- 5 tests para API Nativa MongoDB
- 5 tests para Spring Data MongoDB

### 3. Ejecutar la Aplicación

```bash
./gradlew bootRun
```

La aplicación iniciará en: `http://localhost:8083`

Al iniciar verás:
- Descarga de MongoDB 7.0.2 (solo la primera vez)
- Inicialización de MongoDB embebido en puerto 27017
- Carga de 8 usuarios de prueba
- Banner con información del proyecto

### 4. Probar los Endpoints

#### API Nativa MongoDB

```bash
# Test de conexión
curl http://localhost:8083/api/native/test-connection

# Crear usuario
curl -X POST http://localhost:8083/api/native/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo.usuario",
    "email": "nuevo@example.com",
    "fullName": "Usuario Nuevo",
    "department": "IT",
    "active": true
  }'

# Obtener usuario por ID
curl http://localhost:8083/api/native/users/{id}

# Actualizar usuario
curl -X PUT http://localhost:8083/api/native/users/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Nombre Actualizado",
    "active": false
  }'

# Eliminar usuario
curl -X DELETE http://localhost:8083/api/native/users/{id}
```

#### Spring Data MongoDB

```bash
# Test de conexión
curl http://localhost:8083/api/springdata/test-connection

# Crear usuario
curl -X POST http://localhost:8083/api/springdata/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevo.usuario2",
    "email": "nuevo2@example.com",
    "fullName": "Usuario Nuevo 2",
    "department": "HR",
    "active": true
  }'

# Los demás endpoints son equivalentes a los de API Nativa
```

## Estructura de Archivos

```
proyecto-pedagogico-mongodb/
├── src/
│   ├── main/
│   │   ├── java/com/dam/accesodatos/
│   │   │   ├── MongoDbTeachingApplication.java
│   │   │   ├── config/
│   │   │   │   ├── MongoConfig.java
│   │   │   │   └── DataInitializer.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── UserCreateDto.java
│   │   │   │   ├── UserUpdateDto.java
│   │   │   │   └── UserQueryDto.java
│   │   │   ├── mongodb/
│   │   │   │   ├── nativeapi/
│   │   │   │   │   ├── NativeMongoUserService.java
│   │   │   │   │   └── NativeMongoUserServiceImpl.java
│   │   │   │   └── springdata/
│   │   │   │       ├── UserRepository.java
│   │   │   │       ├── SpringDataUserService.java
│   │   │   │       └── SpringDataUserServiceImpl.java
│   │   │   └── controller/
│   │   │       ├── NativeMongoController.java
│   │   │       └── SpringDataController.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/dam/accesodatos/
│           └── mongodb/
│               ├── nativeapi/NativeMongoUserServiceTest.java
│               └── springdata/SpringDataUserServiceTest.java
├── build.gradle
├── settings.gradle
├── README.md
├── ARQUITECTURA.md
└── INSTRUCCIONES_USO.md (este archivo)
```

## Métodos Implementados (Ejemplos)

### API Nativa MongoDB (NativeMongoUserServiceImpl.java)

1. ✅ `testConnection()` - Verificar conexión y listar colecciones
2. ✅ `createUser()` - Insertar usuario con Document
3. ✅ `findUserById()` - Buscar por ID con Filters
4. ✅ `updateUser()` - Actualizar con Updates.combine()
5. ✅ `deleteUser()` - Eliminar con deleteOne()

### Spring Data MongoDB (SpringDataUserServiceImpl.java)

1. ✅ `testConnection()` - Verificar con MongoTemplate
2. ✅ `createUser()` - Guardar con repository.save()
3. ✅ `findUserById()` - Buscar con repository.findById()
4. ✅ `updateUser()` - Actualizar con save()
5. ✅ `deleteUser()` - Eliminar con repository.deleteById()

## Métodos TODO (Para Estudiantes)

### API Nativa MongoDB

1. ⚠️ `findAll()` - Obtener todos los usuarios
2. ⚠️ `findUsersByDepartment()` - Filtrar por departamento
3. ⚠️ `searchUsers()` - Búsqueda con múltiples filtros + paginación
4. ⚠️ `countByDepartment()` - Contar usuarios con aggregation

### Spring Data MongoDB

1. ⚠️ `findAll()` - Obtener todos los usuarios
2. ⚠️ `findUsersByDepartment()` - Filtrar por departamento
3. ⚠️ `searchUsers()` - Búsqueda con MongoTemplate + Criteria
4. ⚠️ `countByDepartment()` - Contar usuarios con query methods

## Tests

Los tests verifican:
- ✅ Métodos implementados funcionan correctamente
- ✅ Métodos TODO lanzan `UnsupportedOperationException`
- ✅ Conexión a MongoDB
- ✅ CRUD completo

Para ver reporte detallado después de ejecutar tests:
```bash
./gradlew test
# Abrir en navegador:
# build/reports/tests/test/index.html
```

## Configuración

### MongoDB Embebido (application.yml)

```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: pedagogico_db

de:
  flapdoodle:
    mongodb:
      embedded:
        version: 7.0.2
```

### Puerto del Servidor

```yaml
server:
  port: 8083
```

## Usuarios de Prueba

Al iniciar la aplicación se crean 8 usuarios:
- **IT**: 3 usuarios (1 inactivo: juan.perez)
- **HR**: 2 usuarios
- **Finance**: 1 usuario
- **Marketing**: 1 usuario
- **Sales**: 1 usuario

## Tecnologías Utilizadas

- **Spring Boot 3.2.0**
- **Spring Data MongoDB**
- **MongoDB Driver Sync 4.11.1** (API Nativa)
- **Flapdoodle Embedded MongoDB 4.11.0** (Desarrollo y Tests)
- **MongoDB 7.0.2** (Embebido)
- **Java 21**
- **Gradle 8.6**
- **JUnit 5**

## Resolución de Problemas

### MongoDB no inicia

**Problema**: Error al descargar MongoDB embebido

**Solución**: Verificar conexión a internet. MongoDB se descarga automáticamente de:
```
https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-ubuntu2204-7.0.2.tgz
```

### Puerto 8083 ya en uso

**Problema**: `Address already in use`

**Solución**: Cambiar puerto en `application.yml`:
```yaml
server:
  port: 8084  # o cualquier puerto disponible
```

### Tests fallan

**Problema**: Tests no pasan

**Verificar**:
1. MongoDB embebido inicia correctamente
2. Puerto 27017 está disponible
3. Logs en `build/test-results/`

### Compilación falla

**Problema**: Errores de compilación

**Solución**:
```bash
./gradlew clean
./gradlew compileJava
```

## Documentación Adicional

- **README.md**: Guía completa para estudiantes con instrucciones detalladas para implementar los métodos TODO
- **ARQUITECTURA.md**: Documentación técnica de la arquitectura del proyecto

## Próximos Pasos para Estudiantes

1. Leer **README.md** completo
2. Familiarizarse con los 5 métodos implementados en cada módulo
3. Implementar los 4 métodos TODO en **API Nativa**
4. Implementar los 4 métodos TODO en **Spring Data**
5. Ejecutar tests para verificar implementación
6. Comparar ambas aproximaciones (Nativa vs Spring Data)

## Verificación Final del Proyecto

```bash
# 1. Compilar
./gradlew clean compileJava

# 2. Tests
./gradlew test

# 3. Ejecutar
./gradlew bootRun

# 4. Probar endpoints (en otra terminal)
curl http://localhost:8083/api/native/test-connection
curl http://localhost:8083/api/springdata/test-connection
```

**Estado esperado**: Todo funciona correctamente ✅
