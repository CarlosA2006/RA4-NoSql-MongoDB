---
marp: true
theme: default
paginate: true
backgroundColor: #fff
color: #333
header: 'Spring Data MongoDB - Repositories'
footer: 'Semana 2 - Acceso a Datos'
---

<!-- _class: lead -->
<!-- _paginate: false -->

# Spring Data MongoDB

## Repositories y Query Methods

### Semana 2 - Acceso a Datos

---

## El Problema

### Sin Spring Data: Mucho Código Repetitivo

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
        // ... más campos
        users.add(user);
    }
    return users;
}
```

**15+ líneas para una consulta simple** 😰

---

## La Solución

### Con Spring Data: Una Línea

```java
public interface UserRepository extends MongoRepository<User, String> {

    List<User> findByDepartment(String department);

}
```

**Uso:**
```java
List<User> users = userRepository.findByDepartment("IT");
```

### ¡Spring genera la implementación automáticamente! ✨

---

## ¿Qué es Spring Data?

### Capa de Abstracción

```
┌─────────────────────────────────────────────────────────┐
│                    Tu Código                            │
│              userRepository.findAll()                   │
└───────────────────────────┬─────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────┐
│                  Spring Data MongoDB                    │
│   • Genera implementaciones automáticamente             │
│   • Traduce métodos a consultas MongoDB                 │
│   • Mapea documentos ↔ objetos Java                     │
└───────────────────────────┬─────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────┐
│                   MongoDB Driver                        │
└───────────────────────────┬─────────────────────────────┘
                            │
┌───────────────────────────▼─────────────────────────────┐
│                      MongoDB                            │
└─────────────────────────────────────────────────────────┘
```

---

## MongoRepository

### Jerarquía de Interfaces

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

---

## Métodos que Obtienes Gratis

| Categoría | Métodos |
|-----------|---------|
| **Crear** | `save(entity)`, `saveAll(entities)`, `insert(entity)` |
| **Leer** | `findById(id)`, `findAll()`, `findAllById(ids)` |
| **Actualizar** | `save(entity)` (si ya existe) |
| **Eliminar** | `deleteById(id)`, `delete(entity)`, `deleteAll()` |
| **Utilidad** | `count()`, `existsById(id)` |

### Ejemplo:
```java
Optional<User> user = userRepository.findById("123");
long total = userRepository.count();
boolean exists = userRepository.existsById("123");
```

---

## Query Methods

### La Magia de los Nombres

> Spring Data analiza el **nombre del método** y genera automáticamente la consulta MongoDB correspondiente.

```java
// El nombre del método define la consulta
List<User> findByDepartment(String department);

// Spring genera automáticamente:
// db.users.find({ "department": department })
```

**Regla:** `findBy` + NombreCampo = Consulta por ese campo

---

## Anatomía de un Query Method

### Desglosando el Nombre

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

---

## Prefijos Disponibles

| Prefijo | Uso | Retorno |
|---------|-----|---------|
| `findBy...` | Buscar documentos | `List<T>`, `Optional<T>`, `T` |
| `countBy...` | Contar documentos | `long` |
| `existsBy...` | Verificar existencia | `boolean` |
| `deleteBy...` | Eliminar documentos | `void`, `long` |

### Ejemplos:
```java
List<User> findByDepartment(String dept);
long countByDepartment(String dept);
boolean existsByEmail(String email);
void deleteByActiveFalse();
```

---

## Operadores de Comparación

| Operador | Ejemplo | Consulta MongoDB |
|----------|---------|------------------|
| (ninguno) | `findByName(name)` | `{ name: name }` |
| `Not` | `findByNameNot(name)` | `{ name: { $ne: name } }` |
| `GreaterThan` | `findByAgeGreaterThan(n)` | `{ age: { $gt: n } }` |
| `LessThan` | `findByAgeLessThan(n)` | `{ age: { $lt: n } }` |
| `Between` | `findByAgeBetween(a,b)` | `{ age: { $gte: a, $lte: b } }` |
| `Like` | `findByNameLike(n)` | `{ name: { $regex: n } }` |
| `In` | `findByDeptIn(list)` | `{ dept: { $in: list } }` |

---

## Combinando Condiciones

### AND, OR y Ordenamiento

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

---

## Nuestro UserRepository

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

---

## La Entidad User

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

---

## Flujo Completo

### ¿Qué Pasa Cuando Llamas a findByDepartment?

```
Tu Código              Spring Data            MongoDB
   │                        │                     │
   │ findByDepartment("IT") │                     │
   │───────────────────────▶│                     │
   │                        │                     │
   │                        │ find({dept:"IT"})   │
   │                        │────────────────────▶│
   │                        │                     │
   │                        │  [doc1, doc2...]    │
   │                        │◀────────────────────│
   │                        │                     │
   │                        │ Mapea docs → Users  │
   │                        │                     │
   │    List<User>          │                     │
   │◀───────────────────────│                     │
```

---

## Usando el Repository

### Inyección y Uso

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
        return userRepository.findByDepartment(dept);
    }

    public long countInDepartment(String dept) {
        return userRepository.countByDepartment(dept);
    }
}
```

---

## Los TODOs de Hoy

| Método | Líneas | Dificultad |
|--------|--------|------------|
| `findAll()` | 1 | ⭐ Fácil |
| `findUsersByDepartment()` | 1 | ⭐ Fácil |
| `countByDepartment()` | 1 | ⭐ Fácil |

### Pistas:
```java
// findAll()
return userRepository.findAll();

// findUsersByDepartment()
return userRepository.findByDepartment(department);

// countByDepartment()
return userRepository.countByDepartment(department);
```

---

## Errores Comunes

### ⚠️ Nombre de campo incorrecto:
```java
// ❌ Error: el campo es "department", no "dept"
List<User> findByDept(String dept);

// ✅ Correcto
List<User> findByDepartment(String department);
```

### ⚠️ Tipo de retorno incorrecto:
```java
// ❌ Error: findById devuelve Optional
User findById(String id);

// ✅ Correcto
Optional<User> findById(String id);
```

---

## Ventajas de Spring Data

- ✅ **Menos código:** Operaciones CRUD en 1 línea
- ✅ **Menos errores:** No hay mapeo manual
- ✅ **Consistencia:** Mismo patrón para todas las entidades
- ✅ **Productividad:** Más tiempo para lógica de negocio
- ✅ **Mantenibilidad:** Código más limpio y legible
- ✅ **Testing:** Fácil de mockear
- ✅ **Portable:** Mismo código para JPA, MongoDB, etc.

---

## Limitaciones

### ¿Cuándo NO es Suficiente?

- ⚠️ Consultas muy complejas con múltiples condiciones dinámicas
- ⚠️ Agregaciones y estadísticas
- ⚠️ Operaciones de actualización parcial específicas
- ⚠️ Control fino sobre índices y hints
- ⚠️ Proyecciones complejas

### Solución: MongoTemplate

```java
// Lo veremos en la semana 4
Query query = new Query(Criteria.where("name").regex("Ana"));
query.with(Sort.by("createdAt").descending());
List<User> users = mongoTemplate.find(query, User.class);
```

---

## Resumen

### Conceptos Clave

**MongoRepository**
Interfaz que extiende tu repository para heredar métodos CRUD automáticos

**Query Methods**
Métodos cuyo nombre define la consulta
`findBy` + Campo + Operador

**Sin Implementación**
Spring genera todo el código automáticamente
Solo definimos la interfaz

---

<!-- _class: lead -->

# ¡A Practicar!

## Pasos:
1. Abrir `SpringDataUserServiceImpl.java`
2. Localizar los métodos TODO (líneas 129, 137, 158)
3. Implementar cada método (1 línea cada uno)
4. Ejecutar tests: `./gradlew test --tests "*SpringData*"`
5. Verificar que pasan ✅

**Tiempo:** 30 minutos

---

## Recursos

### Documentación

📚 [Spring Data MongoDB Reference](https://docs.spring.io/spring-data/mongodb/reference/)
📖 [Query Methods](https://docs.spring.io/spring-data/mongodb/reference/mongodb/repositories/query-methods.html)
🔍 [MongoRepository JavaDoc](https://docs.spring.io/spring-data/mongodb/docs/current/api/)

### En el proyecto:
- `README.md` - Sección "Spring Data"
- `UserRepository.java` - Ver métodos disponibles
- `User.java` - Ver campos para query methods

---

<!-- _class: lead -->
<!-- _paginate: false -->

# ¿Preguntas?

## 🍃

> "La mejor línea de código es la que no tienes que escribir"
