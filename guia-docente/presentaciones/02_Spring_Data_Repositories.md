# Presentación: Spring Data MongoDB - Repositories

> **Duración:** 45 minutos
> **Semana:** 2
> **Bloque:** Teoría - Spring Data Repositories

---

## DIAPOSITIVA 1: Portada

### Contenido:

```
╔════════════════════════════════════════════════════════════╗
║                                                            ║
║           Spring Data MongoDB                              ║
║                                                            ║
║      Repositories y Query Methods                          ║
║                                                            ║
║     ─────────────────────────────────────                  ║
║                                                            ║
║         Semana 2 - Acceso a Datos                          ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Notas del presentador:**
Esta sesión explica cómo Spring Data simplifica enormemente el acceso a MongoDB. Veremos cómo escribir menos código y ser más productivos.

---

## DIAPOSITIVA 2: El Problema

**Diseño:** Bloque de código largo y complejo

### Contenido:

**Título:** Sin Spring Data: Mucho Código Repetitivo

```java
public List<User> findByDepartment(String dept) {
    MongoClient client = MongoClients.create("mongodb://...");
    MongoDatabase db = client.getDatabase("mydb");
    MongoCollection<Document> col = db.getCollection("users");

    Bson filter = Filters.eq("department", dept);
    FindIterable<Document> results = col.find(filter);

    List<User> users = new ArrayList<>();
    for (Document doc : results) {
        User user = new User();
        user.setId(doc.getObjectId("_id").toString());
        user.setName(doc.getString("name"));
        user.setEmail(doc.getString("email"));
        // ... más campos
        users.add(user);
    }
    return users;
}
```

**Destacar:** 15+ líneas para una consulta simple 😰

**Notas del presentador:**
Cada operación CRUD requiere: obtener conexión, colección, crear filtros, ejecutar, mapear resultados manualmente. Muy propenso a errores y código duplicado.

---

## DIAPOSITIVA 3: La Solución

**Diseño:** Código corto y limpio, fondo verde suave

### Contenido:

**Título:** Con Spring Data: Una Línea

```java
public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByDepartment(String department);

}
```

**Uso:**
```java
List<User> users = userRepository.findByDepartment("IT");
```

**Destacar:** ¡Spring genera la implementación automáticamente! ✨

**Notas del presentador:**
No hay clase de implementación. Solo definimos la interfaz y Spring Data crea todo el código necesario en tiempo de ejecución. Magia que funciona.

---

## DIAPOSITIVA 4: ¿Qué es Spring Data?

**Diseño:** Diagrama de capas

### Contenido:

**Título:** Spring Data: Capa de Abstracción

```
┌─────────────────────────────────────────────────────────────┐
│                    Tu Código                                │
│              userRepository.findAll()                       │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                  Spring Data MongoDB                        │
│   • Genera implementaciones automáticamente                 │
│   • Traduce métodos a consultas MongoDB                     │
│   • Mapea documentos ↔ objetos Java                         │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                   MongoDB Driver                            │
└───────────────────────────┬─────────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────────┐
│                      MongoDB                                │
└─────────────────────────────────────────────────────────────┘
```

**Notas del presentador:**
Spring Data es un proyecto paraguas que soporta múltiples bases de datos: JPA, MongoDB, Redis, Elasticsearch... Aprender uno facilita aprender los demás.

---

## DIAPOSITIVA 5: MongoRepository

**Diseño:** Diagrama de herencia de interfaces

### Contenido:

**Título:** Jerarquía de Interfaces

```
                    Repository<T, ID>
                          │
                          ▼
                 CrudRepository<T, ID>
                    save(), findById(), delete()...
                          │
                          ▼
               PagingAndSortingRepository<T, ID>
                    findAll(Pageable), findAll(Sort)
                          │
                          ▼
               MongoRepository<User, String>
                    insert(), findAll()...
                          │
                          ▼
                   UserRepository
                    (nuestra interfaz)
```

**Notas del presentador:**
Al extender MongoRepository heredamos ~15 métodos ya implementados. No escribimos ninguna implementación.

---

## DIAPOSITIVA 6: Métodos Heredados

**Diseño:** Tabla con métodos organizados por categoría

### Contenido:

**Título:** Métodos que Obtienes Gratis

| Categoría | Métodos |
|-----------|---------|
| **Crear** | `save(entity)`, `saveAll(entities)`, `insert(entity)` |
| **Leer** | `findById(id)`, `findAll()`, `findAllById(ids)` |
| **Actualizar** | `save(entity)` (si ya existe) |
| **Eliminar** | `deleteById(id)`, `delete(entity)`, `deleteAll()` |
| **Utilidad** | `count()`, `existsById(id)` |

**Ejemplo:**
```java
// Todo esto funciona sin escribir implementación
Optional<User> user = userRepository.findById("123");
long total = userRepository.count();
boolean exists = userRepository.existsById("123");
```

**Notas del presentador:**
El método save() es inteligente: si el documento tiene _id y existe, actualiza. Si no tiene _id o no existe, inserta.

---

## DIAPOSITIVA 7: Query Methods

**Diseño:** Título grande con definición

### Contenido:

**Título:** Query Methods: La Magia de los Nombres

> Spring Data analiza el **nombre del método** y genera automáticamente la consulta MongoDB correspondiente.

```java
// El nombre del método define la consulta
List<User> findByDepartment(String department);

// Spring genera automáticamente:
// db.users.find({ "department": department })
```

**Regla:** `findBy` + NombreCampo = Consulta por ese campo

**Notas del presentador:**
Spring parsea el nombre del método siguiendo convenciones. Si el nombre no sigue las reglas, da error de compilación. Es muy estricto pero muy útil.

---

## DIAPOSITIVA 8: Anatomía de un Query Method

**Diseño:** Método desglosado con flechas explicativas

### Contenido:

**Título:** Desglosando el Nombre

```
    findByDepartmentAndActiveTrue
    ├──┤├────────┤├──┤├────┤├───┤
      │      │      │    │    │
      │      │      │    │    └── Valor fijo: true
      │      │      │    └─────── Campo: active
      │      │      └──────────── Operador: AND
      │      └─────────────────── Campo: department
      └────────────────────────── Prefijo: findBy
```

**Resultado MongoDB:**
```javascript
db.users.find({
    "department": <parámetro>,
    "active": true
})
```

**Notas del presentador:**
Los campos deben coincidir exactamente con los nombres en la clase User. Es case-sensitive. "Department" no es igual a "department".

---

## DIAPOSITIVA 9: Prefijos Disponibles

**Diseño:** Tabla con ejemplos

### Contenido:

**Título:** Prefijos de Query Methods

| Prefijo | Uso | Retorno |
|---------|-----|---------|
| `findBy...` | Buscar documentos | `List<T>`, `Optional<T>`, `T` |
| `countBy...` | Contar documentos | `long` |
| `existsBy...` | Verificar existencia | `boolean` |
| `deleteBy...` | Eliminar documentos | `void`, `long` |

**Ejemplos:**
```java
List<User> findByDepartment(String dept);
long countByDepartment(String dept);
boolean existsByEmail(String email);
void deleteByActivefalse();
```

**Notas del presentador:**
countBy es más eficiente que findBy().size() porque no carga los documentos en memoria, solo cuenta en la base de datos.

---

## DIAPOSITIVA 10: Operadores de Comparación

**Diseño:** Tabla con operadores y ejemplos

### Contenido:

**Título:** Operadores en Query Methods

| Operador | Ejemplo | Consulta MongoDB |
|----------|---------|------------------|
| (ninguno) | `findByName(name)` | `{ name: name }` |
| `Not` | `findByNameNot(name)` | `{ name: { $ne: name } }` |
| `GreaterThan` | `findByAgeGreaterThan(n)` | `{ age: { $gt: n } }` |
| `LessThan` | `findByAgeLessThan(n)` | `{ age: { $lt: n } }` |
| `Between` | `findByAgeBetween(a,b)` | `{ age: { $gte: a, $lte: b } }` |
| `Like` | `findByNameLike(n)` | `{ name: { $regex: n } }` |
| `In` | `findByDeptIn(list)` | `{ dept: { $in: list } }` |

**Notas del presentador:**
Hay muchos más operadores: IsNull, IsNotNull, True, False, StartingWith, EndingWith, Containing, etc. Ver documentación oficial.

---

## DIAPOSITIVA 11: Combinando Condiciones

**Diseño:** Ejemplos de código con resultados

### Contenido:

**Título:** And, Or y Más

**AND (ambas condiciones):**
```java
List<User> findByDepartmentAndActive(String dept, boolean active);
// { $and: [{ department: dept }, { active: active }] }
```

**OR (cualquier condición):**
```java
List<User> findByDepartmentOrRole(String dept, String role);
// { $or: [{ department: dept }, { role: role }] }
```

**Ordenamiento:**
```java
List<User> findByDepartmentOrderByNameAsc(String dept);
List<User> findByDepartmentOrderByCreatedAtDesc(String dept);
```

**Notas del presentador:**
Se pueden combinar múltiples condiciones, pero si se vuelve muy complejo es mejor usar @Query o MongoTemplate.

---

## DIAPOSITIVA 12: Nuestro UserRepository

**Diseño:** Código con anotaciones explicativas

### Contenido:

**Título:** UserRepository del Proyecto

```java
@Repository
public interface UserRepository
        extends MongoRepository<User, String> {

    // Query method: buscar por departamento
    List<User> findByDepartment(String department);

    // Query method: contar por departamento
    long countByDepartment(String department);

    // Query method: buscar por email (único)
    User findByEmail(String email);

    // ¡Heredamos findAll(), save(), deleteById(), etc.!
}
```

**Puntos clave:**
- `@Repository` marca como componente Spring
- `<User, String>` = Entidad y tipo de ID
- No hay clase de implementación

**Notas del presentador:**
Estos son los métodos que usaremos en los TODOs de hoy. findByDepartment y countByDepartment ya están definidos, solo hay que llamarlos.

---

## DIAPOSITIVA 13: La Entidad User

**Diseño:** Código con anotaciones destacadas

### Contenido:

**Título:** Mapeo de la Entidad

```java
@Document(collection = "users")  // Nombre de la colección
public class User {

    @Id                          // Campo _id de MongoDB
    private String id;

    @Indexed                     // Crear índice para búsquedas
    private String name;

    @Indexed(unique = true)      // Índice único (no duplicados)
    private String email;

    private String department;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;

    // getters y setters...
}
```

**Notas del presentador:**
@Document indica que es una entidad MongoDB. @Indexed mejora el rendimiento de búsquedas. @Id marca el identificador único.

---

## DIAPOSITIVA 14: Flujo Completo

**Diseño:** Diagrama de secuencia simplificado

### Contenido:

**Título:** ¿Qué Pasa Cuando Llamas a findByDepartment?

```
   Tu Código                Spring Data              MongoDB
      │                          │                      │
      │ findByDepartment("IT")   │                      │
      │─────────────────────────▶│                      │
      │                          │                      │
      │                          │ find({department:"IT"})
      │                          │─────────────────────▶│
      │                          │                      │
      │                          │    [doc1, doc2...]   │
      │                          │◀─────────────────────│
      │                          │                      │
      │                          │ Mapea docs → Users   │
      │                          │                      │
      │    List<User>            │                      │
      │◀─────────────────────────│                      │
```

**Notas del presentador:**
Todo este proceso es automático. Spring parsea el nombre, genera la consulta, ejecuta, mapea resultados. Nosotros solo escribimos una línea.

---

## DIAPOSITIVA 15: Usando el Repository

**Diseño:** Código de ejemplo con inyección

### Contenido:

**Título:** Inyección y Uso

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired  // Inyección de dependencias
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();  // ¡Una línea!
    }

    public List<User> getByDepartment(String dept) {
        return userRepository.findByDepartment(dept);  // ¡Una línea!
    }

    public long countInDepartment(String dept) {
        return userRepository.countByDepartment(dept);  // ¡Una línea!
    }
}
```

**Notas del presentador:**
Spring crea la instancia del repository e inyecta automáticamente. Nosotros solo lo usamos. Esto es lo que haréis en los TODOs.

---

## DIAPOSITIVA 16: Los TODOs de Hoy

**Diseño:** Lista de tareas con indicadores de dificultad

### Contenido:

**Título:** Métodos a Implementar

| Método | Líneas | Dificultad |
|--------|--------|------------|
| `findAll()` | 1 | ⭐ Fácil |
| `findUsersByDepartment()` | 1 | ⭐ Fácil |
| `countByDepartment()` | 1 | ⭐ Fácil |

**Pistas:**
```java
// findAll()
return userRepository.findAll();

// findUsersByDepartment()
return userRepository.findByDepartment(department);

// countByDepartment()
return userRepository.countByDepartment(department);
```

**Notas del presentador:**
Sí, son literalmente una línea cada uno. El objetivo es entender cómo funciona Spring Data, no escribir mucho código.

---

## DIAPOSITIVA 17: Errores Comunes

**Diseño:** Lista con iconos de warning

### Contenido:

**Título:** Errores a Evitar

**⚠️ Nombre de campo incorrecto:**
```java
// ❌ Error: el campo es "department", no "dept"
List<User> findByDept(String dept);

// ✅ Correcto
List<User> findByDepartment(String department);
```

**⚠️ Tipo de retorno incorrecto:**
```java
// ❌ Error: findById devuelve Optional
User findById(String id);

// ✅ Correcto
Optional<User> findById(String id);
```

**⚠️ Olvidar @Repository:**
```java
// ❌ Spring no lo detecta
public interface UserRepository extends MongoRepository...

// ✅ Correcto (aunque a veces funciona sin él)
@Repository
public interface UserRepository extends MongoRepository...
```

**Notas del presentador:**
El IDE suele detectar estos errores. Si algo no funciona, revisar que los nombres coincidan exactamente con la clase User.

---

## DIAPOSITIVA 18: Ventajas de Spring Data

**Diseño:** Lista con iconos verdes

### Contenido:

**Título:** ¿Por Qué Usar Spring Data?

- ✅ **Menos código:** Operaciones CRUD en 1 línea
- ✅ **Menos errores:** No hay mapeo manual
- ✅ **Consistencia:** Mismo patrón para todas las entidades
- ✅ **Productividad:** Más tiempo para lógica de negocio
- ✅ **Mantenibilidad:** Código más limpio y legible
- ✅ **Testing:** Fácil de mockear
- ✅ **Portable:** Mismo código para JPA, MongoDB, etc.

**Notas del presentador:**
En un proyecto real, estas ventajas se multiplican. Menos código = menos bugs = menos mantenimiento.

---

## DIAPOSITIVA 19: Limitaciones

**Diseño:** Lista con iconos naranjas de advertencia

### Contenido:

**Título:** ¿Cuándo NO es Suficiente?

- ⚠️ Consultas muy complejas con múltiples condiciones dinámicas
- ⚠️ Agregaciones y estadísticas
- ⚠️ Operaciones de actualización parcial específicas
- ⚠️ Control fino sobre índices y hints
- ⚠️ Proyecciones complejas

**Solución:** Para estos casos usamos `MongoTemplate`

```java
// Lo veremos en la semana 4
Query query = new Query(Criteria.where("name").regex("Ana"));
query.with(Sort.by("createdAt").descending());
List<User> users = mongoTemplate.find(query, User.class);
```

**Notas del presentador:**
Spring Data no es magia infinita. Para casos complejos existe MongoTemplate que da más control. Lo veremos cuando implementemos searchUsers().

---

## DIAPOSITIVA 20: Resumen

**Diseño:** Puntos clave en recuadros

### Contenido:

**Título:** Conceptos Clave

```
┌────────────────────────────────────────────────────────────┐
│  MongoRepository                                           │
│  Interfaz que extiende tu repository para heredar          │
│  métodos CRUD automáticos                                  │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│  Query Methods                                             │
│  Métodos cuyo nombre define la consulta                    │
│  findBy + Campo + Operador                                 │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│  Sin Implementación                                        │
│  Spring genera todo el código automáticamente              │
│  Solo definimos la interfaz                                │
└────────────────────────────────────────────────────────────┘
```

**Notas del presentador:**
Con estos tres conceptos ya podéis implementar los TODOs de hoy. Son solo 3 líneas de código en total.

---

## DIAPOSITIVA 21: Manos a la Obra

**Diseño:** Instrucciones paso a paso

### Contenido:

**Título:** ¡A Practicar!

**Pasos:**
1. Abrir `SpringDataUserServiceImpl.java`
2. Localizar los métodos TODO (líneas 129, 137, 158)
3. Implementar cada método (1 línea cada uno)
4. Ejecutar tests: `./gradlew test --tests "*SpringData*"`
5. Verificar que pasan ✅

**Tiempo:** 30 minutos

**Objetivo:** Todos los tests de Spring Data en verde

**Notas del presentador:**
Dejar que trabajen de forma autónoma. Pasar por las mesas resolviendo dudas. Si alguien termina antes, que ayude a un compañero o explore otros métodos de MongoRepository.

---

## DIAPOSITIVA 22: Recursos

**Diseño:** Lista de enlaces

### Contenido:

**Título:** Documentación

- 📚 [Spring Data MongoDB Reference](https://docs.spring.io/spring-data/mongodb/reference/)
- 📖 [Query Methods](https://docs.spring.io/spring-data/mongodb/reference/mongodb/repositories/query-methods.html)
- 🔍 [MongoRepository JavaDoc](https://docs.spring.io/spring-data/mongodb/docs/current/api/)

**En el proyecto:**
- `README.md` - Sección "Spring Data"
- `UserRepository.java` - Ver métodos disponibles
- `User.java` - Ver campos para query methods

**Notas del presentador:**
La documentación de Spring es excelente. Ante cualquier duda, consultar la referencia oficial.

---

## DIAPOSITIVA 23: Preguntas

**Diseño:** Slide de cierre con pregunta

### Contenido:

```
╔════════════════════════════════════════════════════════════╗
║                                                            ║
║                    ¿Preguntas?                             ║
║                                                            ║
║                       🍃                                   ║
║                                                            ║
║     "La mejor línea de código es la que no                 ║
║      tienes que escribir"                                  ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Notas del presentador:**
Resolver dudas antes de comenzar la práctica. Recordar que la próxima semana defenderán estos métodos oralmente.
