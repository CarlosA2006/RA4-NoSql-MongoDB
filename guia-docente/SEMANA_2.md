# Semana 2: Implementación de TODOs Sencillos - Spring Data

## Información de la Sesión

| Aspecto | Detalle |
|---------|---------|
| **Duración** | 5 horas |
| **Defensa oral** | No hay (primera semana de implementación) |
| **Objetivo principal** | Implementar 3 métodos TODO en Spring Data |
| **Entregable** | `findAll()`, `findByDepartment()`, `countByDepartment()` funcionando |

---

## Objetivos de Aprendizaje

Al finalizar esta semana, el alumnado será capaz de:

1. Explicar cómo funcionan los repositorios de Spring Data MongoDB.
2. Utilizar query methods derivados del nombre del método.
3. Implementar operaciones de lectura básicas con Spring Data.
4. Ejecutar tests unitarios para verificar la implementación.
5. Preparar una defensa oral técnica de su código.

---

## Métodos a Implementar

| Método | Complejidad | Líneas de código | Tiempo estimado |
|--------|-------------|------------------|-----------------|
| `findAll()` | Muy baja | 1 línea | 5-10 min |
| `findByDepartment()` | Muy baja | 1 línea | 5-10 min |
| `countByDepartment()` | Muy baja | 1 línea | 5-10 min |

---

## Temporización Detallada

### Bloque 1: Repaso y Dudas (30 min)

#### Repaso Rápido (15 min)

Preguntas para activar conocimientos previos:

1. ¿Qué diferencia hay entre una tabla SQL y una colección MongoDB?
2. ¿Qué es un documento BSON?
3. ¿Dónde están los archivos que vamos a modificar hoy?
4. ¿Qué hace el método `createUser()` en Spring Data?

#### Resolución de Dudas (15 min)

- Problemas técnicos pendientes de la semana anterior
- Dudas sobre la estructura del proyecto
- Cualquier concepto no comprendido

---

### Bloque 2: Teoría - Spring Data Repositories (45 min)

#### ¿Qué es Spring Data MongoDB? (15 min)

Spring Data MongoDB es una abstracción que simplifica el acceso a MongoDB:

```
┌─────────────────────────────────────────────────────────┐
│                    Tu Código                            │
│         userRepository.findAll()                        │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│              Spring Data MongoDB                        │
│   - Genera implementación automáticamente               │
│   - Traduce a operaciones MongoDB                       │
│   - Mapea documentos a objetos Java                     │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│               MongoDB Driver                            │
│         collection.find().into(list)                    │
└───────────────────────┬─────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────┐
│                   MongoDB                               │
│              Colección "users"                          │
└─────────────────────────────────────────────────────────┘
```

#### Interfaces de Repositorio (15 min)

Spring Data proporciona interfaces base:

```java
// CrudRepository - Operaciones CRUD básicas
public interface CrudRepository<T, ID> {
    <S extends T> S save(S entity);
    Optional<T> findById(ID id);
    Iterable<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
    long count();
    // ... más métodos
}

// MongoRepository - Extiende con funcionalidad específica de Mongo
public interface MongoRepository<T, ID> extends CrudRepository<T, ID> {
    List<T> findAll();
    List<T> findAll(Sort sort);
    <S extends T> List<S> insert(Iterable<S> entities);
    // ... más métodos
}
```

Nuestro repositorio:
```java
public interface UserRepository extends MongoRepository<User, String> {
    // Hereda todos los métodos de MongoRepository
    // + Podemos añadir query methods personalizados
}
```

#### Query Methods Derivados (15 min)

Spring Data genera consultas automáticamente basándose en el nombre del método:

| Nombre del Método | Consulta Generada |
|-------------------|-------------------|
| `findByName(String name)` | `{ "name": name }` |
| `findByDepartment(String dept)` | `{ "department": dept }` |
| `findByActiveTrue()` | `{ "active": true }` |
| `findByNameContaining(String s)` | `{ "name": { $regex: s } }` |
| `countByDepartment(String dept)` | `count({ "department": dept })` |
| `existsByEmail(String email)` | Devuelve true/false |

**Convención de nombres:**
- `findBy` + NombreCampo = Buscar por campo
- `countBy` + NombreCampo = Contar por campo
- `deleteBy` + NombreCampo = Eliminar por campo
- `existsBy` + NombreCampo = Verificar existencia

---

### Bloque 3: Demostración - UserRepository (30 min)

#### Abrir UserRepository.java

Ubicación: `src/main/java/com/dam/accesodatos/mongodb/springdata/UserRepository.java`

```java
package com.dam.accesodatos.mongodb.springdata;

import com.dam.accesodatos.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Query method derivado: busca usuarios por departamento
    List<User> findByDepartment(String department);

    // Query method derivado: cuenta usuarios por departamento
    long countByDepartment(String department);

    // Query method derivado: busca por email (útil para validaciones)
    User findByEmail(String email);
}
```

#### Explicación Paso a Paso

1. **`extends MongoRepository<User, String>`**
   - `User`: Tipo de entidad que gestiona
   - `String`: Tipo del campo ID (el `_id` de MongoDB)

2. **No hay implementación**
   - Spring Data genera la implementación en tiempo de ejecución
   - No necesitamos escribir código SQL/MongoDB

3. **Query methods ya definidos**
   - `findByDepartment()` y `countByDepartment()` ya existen
   - Solo tenemos que usarlos en el servicio

#### Demostración en Vivo

Mostrar cómo se usa en el código existente (método `findUserById`):

```java
// En SpringDataUserServiceImpl.java, línea 77
public User findUserById(String id) {
    User user = userRepository.findById(id).orElse(null);
    if (user == null) {
        throw new UserNotFoundException(id);
    }
    return user;
}
```

Notar: `findById()` viene heredado de `MongoRepository`, no lo definimos nosotros.

---

### Bloque 4: Práctica - Implementar los 3 TODOs (2 horas)

#### Preparación (10 min)

1. Abrir `SpringDataUserServiceImpl.java` en el IDE
2. Localizar los métodos TODO (líneas 129, 137, 158)
3. Abrir `UserRepository.java` en otra pestaña para referencia

#### TODO 1: findAll() (20 min)

**Ubicación:** Línea 129

**Código actual:**
```java
@Override
public List<User> findAll() {
    // TODO: Implementar findAll() - Los estudiantes deben completar este método
    // PISTAS:
    // 1. Usar userRepository.findAll()
    // 2. Es una sola línea de código
    throw new UnsupportedOperationException("TODO: Implementar findAll()");
}
```

**Solución:**
```java
@Override
public List<User> findAll() {
    log.debug("Obteniendo todos los usuarios con Spring Data");
    return userRepository.findAll();
}
```

**Explicación para la defensa:**
- `userRepository.findAll()` es un método heredado de `MongoRepository`
- Devuelve una `List<User>` con todos los documentos de la colección
- Spring Data se encarga de convertir cada documento BSON a objeto User
- El logging es opcional pero recomendado para debugging

#### TODO 2: findUsersByDepartment() (20 min)

**Ubicación:** Línea 137

**Código actual:**
```java
@Override
public List<User> findUsersByDepartment(String department) {
    // TODO: Implementar findUsersByDepartment()
    // PISTAS:
    // 1. Usar userRepository.findByDepartment(department)
    // 2. El método ya está definido en UserRepository
    throw new UnsupportedOperationException("TODO: Implementar findUsersByDepartment()");
}
```

**Solución:**
```java
@Override
public List<User> findUsersByDepartment(String department) {
    log.debug("Buscando usuarios del departamento: {}", department);
    return userRepository.findByDepartment(department);
}
```

**Explicación para la defensa:**
- `findByDepartment()` es un query method que definimos en `UserRepository`
- Spring Data genera automáticamente la consulta `{ "department": department }`
- El nombre del método sigue la convención: `findBy` + nombre del campo
- Devuelve lista vacía si no hay usuarios en ese departamento (no null)

#### TODO 3: countByDepartment() (20 min)

**Ubicación:** Línea 158

**Código actual:**
```java
@Override
public long countByDepartment(String department) {
    // TODO: Implementar countByDepartment()
    // PISTAS:
    // 1. Usar userRepository.countByDepartment(department)
    // 2. El método ya está definido en UserRepository
    throw new UnsupportedOperationException("TODO: Implementar countByDepartment()");
}
```

**Solución:**
```java
@Override
public long countByDepartment(String department) {
    log.debug("Contando usuarios del departamento: {}", department);
    return userRepository.countByDepartment(department);
}
```

**Explicación para la defensa:**
- Similar a `findByDepartment()`, pero con prefijo `countBy`
- Devuelve un `long` con el número de documentos que coinciden
- Más eficiente que `findByDepartment().size()` porque no carga los documentos

#### Tiempo de Implementación Individual (50 min)

Los alumnos implementan los métodos por su cuenta:

1. **Minutos 1-20:** Intentar implementar sin ayuda
2. **Minutos 20-35:** El docente pasa por las mesas resolviendo dudas
3. **Minutos 35-50:** Compartir pantalla con soluciones y explicar

**Consejos durante la práctica:**
- "Mirad las pistas en los comentarios TODO"
- "El método ya existe en UserRepository, solo hay que llamarlo"
- "No olvidéis el logging para poder debuggear"

---

### Bloque 5: Ejecutar Tests (45 min)

#### Ejecutar Tests de Spring Data (20 min)

```bash
./gradlew test --tests "*SpringData*"
```

**Salida esperada (antes de implementar):**
```
SpringDataUserServiceTest > findAllTest() FAILED
SpringDataUserServiceTest > findUsersByDepartmentTest() FAILED
SpringDataUserServiceTest > countByDepartmentTest() FAILED

3 tests FAILED
```

**Salida esperada (después de implementar):**
```
SpringDataUserServiceTest > testConnectionTest() PASSED
SpringDataUserServiceTest > createUserTest() PASSED
SpringDataUserServiceTest > findUserByIdTest() PASSED
SpringDataUserServiceTest > updateUserTest() PASSED
SpringDataUserServiceTest > deleteUserTest() PASSED
SpringDataUserServiceTest > findAllTest() PASSED
SpringDataUserServiceTest > findUsersByDepartmentTest() PASSED
SpringDataUserServiceTest > countByDepartmentTest() PASSED

8 tests PASSED
```

#### Analizar Tests Fallidos (15 min)

Si algún test falla, analizar el error:

**Error común 1: NullPointerException**
```
java.lang.NullPointerException: Cannot invoke method on null
```
→ Olvidaste llamar al repositorio o hay un typo en el nombre del método

**Error común 2: UnsupportedOperationException**
```
java.lang.UnsupportedOperationException: TODO: Implementar...
```
→ No has modificado el método, sigue lanzando la excepción original

**Error común 3: Compilation error**
```
error: cannot find symbol
```
→ El nombre del método en UserRepository no coincide con el que llamas

#### Probar con Swagger (10 min)

Ejecutar la aplicación y probar los nuevos endpoints:

```bash
./gradlew bootRun
```

1. Abrir `http://localhost:8080/swagger-ui.html`
2. Probar `GET /api/spring/users` → Debe devolver 8 usuarios
3. Probar `GET /api/spring/users/department/IT` → Debe devolver 3 usuarios
4. Verificar en los logs del terminal que aparecen los mensajes de debug

---

### Bloque 6: Preparación para la Defensa (30 min)

#### Qué se Espera en la Defensa (10 min)

La próxima semana (semana 3) defenderéis estos 3 métodos. Debéis poder:

1. **Mostrar el código funcionando** (tests en verde o demo en Swagger)
2. **Explicar qué hace cada línea** (no leer, explicar)
3. **Responder preguntas como:**
   - ¿De dónde viene el método `findAll()`?
   - ¿Qué consulta MongoDB genera `findByDepartment()`?
   - ¿Por qué `countByDepartment()` es más eficiente que contar en Java?
   - ¿Qué pasa si el departamento no existe?

#### Preparar Notas (15 min)

Cada alumno prepara un resumen de:

**findAll():**
- Heredado de MongoRepository
- Devuelve List<User> con todos los documentos
- Equivale a `db.users.find({})` en MongoDB shell

**findByDepartment():**
- Query method definido en UserRepository
- Spring genera la consulta automáticamente
- Equivale a `db.users.find({ department: "IT" })`

**countByDepartment():**
- Query method con prefijo `countBy`
- Devuelve long, no carga documentos en memoria
- Equivale a `db.users.countDocuments({ department: "IT" })`

#### Dudas Finales (5 min)

Resolver cualquier duda pendiente antes de cerrar.

---

## Soluciones Completas

### findAll()
```java
@Override
public List<User> findAll() {
    log.debug("Obteniendo todos los usuarios con Spring Data");
    return userRepository.findAll();
}
```

### findUsersByDepartment()
```java
@Override
public List<User> findUsersByDepartment(String department) {
    log.debug("Buscando usuarios del departamento: {}", department);
    return userRepository.findByDepartment(department);
}
```

### countByDepartment()
```java
@Override
public long countByDepartment(String department) {
    log.debug("Contando usuarios del departamento: {}", department);
    return userRepository.countByDepartment(department);
}
```

---

## Ejercicios de Ampliación

Para alumnos que terminen antes:

### Ejercicio 1: Nuevo Query Method
Añadir a `UserRepository`:
```java
List<User> findByActiveTrue();
```
Y usarlo en un nuevo método del servicio.

### Ejercicio 2: Query Method con Ordenación
```java
List<User> findByDepartmentOrderByNameAsc(String department);
```

### Ejercicio 3: Explorar MongoRepository
Investigar qué otros métodos ofrece `MongoRepository` y probar alguno:
- `findAll(Sort sort)`
- `count()`
- `existsById(String id)`

---

## Errores Comunes y Soluciones

| Error | Causa | Solución |
|-------|-------|----------|
| "Method not found" | Typo en nombre del método | Verificar que coincide con UserRepository |
| Tests siguen fallando | No se guardaron los cambios | Ctrl+S y recompilar |
| findByDepartment devuelve vacío | Departamento mal escrito | Los nombres son case-sensitive ("IT" ≠ "it") |
| NullPointerException | userRepository es null | Verificar @Autowired en constructor |

---

## Recursos Adicionales

- [Spring Data MongoDB - Query Methods](https://docs.spring.io/spring-data/mongodb/reference/mongodb/repositories/query-methods.html)
- [MongoRepository JavaDoc](https://docs.spring.io/spring-data/mongodb/docs/current/api/org/springframework/data/mongodb/repository/MongoRepository.html)

---

## Checklist de Fin de Semana

Antes de marcharse, cada alumno debe verificar:

- [ ] Los 3 métodos están implementados
- [ ] `./gradlew test --tests "*SpringData*"` pasa todos los tests
- [ ] Puedo explicar qué hace cada método
- [ ] Tengo notas preparadas para la defensa
- [ ] He probado los endpoints en Swagger
