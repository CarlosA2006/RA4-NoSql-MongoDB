# Presentación: API Nativa de MongoDB

> **Duración:** 45 minutos
> **Semana:** 3
> **Bloque:** Teoría - API Nativa MongoDB

---

## DIAPOSITIVA 1: Portada

### Contenido:

```
╔════════════════════════════════════════════════════════════╗
║                                                            ║
║           API Nativa de MongoDB                            ║
║                                                            ║
║      Driver Java y Operaciones Directas                    ║
║                                                            ║
║     ─────────────────────────────────────                  ║
║                                                            ║
║         Semana 3 - Acceso a Datos                          ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Notas del presentador:**
Hoy bajamos un nivel de abstracción. Veremos cómo funciona MongoDB "por debajo" de Spring Data. Esto os dará una comprensión más profunda y control total.

---

## DIAPOSITIVA 2: ¿Por Qué Aprender la API Nativa?

**Diseño:** Lista con iconos explicativos

### Contenido:

**Título:** ¿Si Spring Data es Tan Fácil, Por Qué Esto?

- 🔍 **Comprensión profunda:** Entender qué hace Spring Data internamente
- 🛠️ **Control total:** Operaciones que Spring Data no soporta fácilmente
- 🐛 **Debugging:** Saber qué buscar cuando algo falla
- ⚡ **Optimización:** Ajustes finos de rendimiento
- 🌍 **Portabilidad:** Mismo conocimiento aplica a otros lenguajes
- 📚 **Fundamentos:** Base para entender cualquier ODM/ORM

**Notas del presentador:**
Es como aprender a conducir con cambio manual antes del automático. Aunque uses el automático después, sabes qué está pasando.

---

## DIAPOSITIVA 3: Arquitectura del Driver

**Diseño:** Diagrama de componentes jerárquico

### Contenido:

**Título:** Componentes del Driver MongoDB Java

```
┌─────────────────────────────────────────────────────────────┐
│                      MongoClient                            │
│            Conexión a la instancia MongoDB                  │
└───────────────────────────┬─────────────────────────────────┘
                            │ getDatabase("nombre")
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     MongoDatabase                           │
│              Base de datos específica                       │
└───────────────────────────┬─────────────────────────────────┘
                            │ getCollection("users")
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              MongoCollection<Document>                      │
│         Colección de documentos (≈ tabla SQL)               │
└───────────────────────────┬─────────────────────────────────┘
                            │ find(), insertOne(), etc.
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      Document                               │
│           Documento BSON (≈ fila SQL)                       │
└─────────────────────────────────────────────────────────────┘
```

**Notas del presentador:**
Es una jerarquía lógica: Cliente → Base de datos → Colección → Documentos. Cada nivel nos da acceso al siguiente.

---

## DIAPOSITIVA 4: MongoClient

**Diseño:** Código con explicaciones

### Contenido:

**Título:** MongoClient: La Conexión

```java
// Crear conexión (normalmente una por aplicación)
MongoClient mongoClient = MongoClients.create(
    "mongodb://localhost:27017"
);

// Con autenticación
MongoClient mongoClient = MongoClients.create(
    "mongodb://usuario:password@localhost:27017/mydb"
);

// En nuestro proyecto: Spring lo configura automáticamente
@Autowired
private MongoClient mongoClient;
```

**Importante:**
- ⚠️ MongoClient es thread-safe
- ⚠️ Crear solo UNO por aplicación
- ⚠️ Reutilizar, no crear y cerrar constantemente

**Notas del presentador:**
MongoClient mantiene un pool de conexiones internamente. Crear múltiples instancias desperdicia recursos y puede causar problemas.

---

## DIAPOSITIVA 5: MongoDatabase y MongoCollection

**Diseño:** Código progresivo

### Contenido:

**Título:** Navegando la Jerarquía

```java
// 1. Obtener la base de datos
MongoDatabase database = mongoClient.getDatabase("accesodatos");

// 2. Obtener la colección
MongoCollection<Document> collection = database.getCollection("users");

// 3. Ahora podemos operar
long count = collection.countDocuments();
```

**En nuestro proyecto:**
```java
// Ya tenemos un método helper
private MongoCollection<Document> getCollection() {
    return mongoClient
        .getDatabase(databaseName)
        .getCollection("users");
}
```

**Notas del presentador:**
getCollection no falla si la colección no existe; MongoDB la crea automáticamente al insertar el primer documento.

---

## DIAPOSITIVA 6: La Clase Document

**Diseño:** Código con múltiples ejemplos

### Contenido:

**Título:** Document: Representando BSON en Java

```java
// Crear un documento vacío
Document doc = new Document();

// Crear con datos (builder pattern)
Document doc = new Document()
    .append("name", "Ana García")
    .append("email", "ana@empresa.com")
    .append("department", "IT")
    .append("active", true)
    .append("age", 28);

// Crear desde mapa
Document doc = new Document(Map.of(
    "name", "Ana",
    "email", "ana@test.com"
));
```

**Notas del presentador:**
Document es similar a un Map<String, Object>. Puede contener cualquier tipo: String, Integer, Boolean, Date, arrays, otros Documents anidados...

---

## DIAPOSITIVA 7: Leer Datos de un Document

**Diseño:** Tabla de métodos con ejemplos

### Contenido:

**Título:** Extrayendo Valores

| Método | Tipo Retorno | Ejemplo |
|--------|--------------|---------|
| `getString("campo")` | String | `doc.getString("name")` |
| `getInteger("campo")` | Integer | `doc.getInteger("age")` |
| `getBoolean("campo")` | Boolean | `doc.getBoolean("active")` |
| `getDouble("campo")` | Double | `doc.getDouble("salary")` |
| `getDate("campo")` | Date | `doc.getDate("createdAt")` |
| `getObjectId("campo")` | ObjectId | `doc.getObjectId("_id")` |
| `get("campo")` | Object | `doc.get("cualquierCosa")` |

**Con valor por defecto:**
```java
// Si "active" no existe, devuelve false
boolean active = doc.getBoolean("active", false);
```

**Notas del presentador:**
Usar los métodos tipados (getString, getInteger) es más seguro que get() + casting. Si el campo no existe, devuelven null.

---

## DIAPOSITIVA 8: Clase Filters

**Diseño:** Título prominente con explicación

### Contenido:

**Título:** Filters: Construyendo Consultas

```java
import static com.mongodb.client.model.Filters.*;
```

> La clase `Filters` proporciona métodos estáticos para crear condiciones de búsqueda de forma segura y legible.

**Analogía SQL:**
```
SQL:    WHERE department = 'IT'

MongoDB Shell:    { "department": "IT" }

Java Driver:      Filters.eq("department", "IT")
```

**Notas del presentador:**
Filters es una clase de utilidad que genera objetos Bson. Bson es la representación binaria de JSON que MongoDB usa internamente.

---

## DIAPOSITIVA 9: Filtros de Igualdad

**Diseño:** Ejemplos con equivalencias

### Contenido:

**Título:** Comparaciones Básicas

```java
// Igualdad
Bson f1 = Filters.eq("department", "IT");
// → { "department": "IT" }

// Desigualdad
Bson f2 = Filters.ne("status", "deleted");
// → { "status": { "$ne": "deleted" } }

// Mayor que
Bson f3 = Filters.gt("age", 25);
// → { "age": { "$gt": 25 } }

// Mayor o igual
Bson f4 = Filters.gte("salary", 30000);
// → { "salary": { "$gte": 30000 } }

// Menor que / menor o igual
Bson f5 = Filters.lt("age", 60);
Bson f6 = Filters.lte("priority", 5);
```

**Notas del presentador:**
gt = greater than, gte = greater than or equal, lt = less than, lte = less than or equal. Mismo patrón que en MongoDB shell pero con sintaxis Java.

---

## DIAPOSITIVA 10: Filtros de Conjunto

**Diseño:** Ejemplos con listas

### Contenido:

**Título:** Búsqueda en Conjuntos

**IN - Dentro de una lista:**
```java
Bson filter = Filters.in("department", "IT", "HR", "Finance");
// → { "department": { "$in": ["IT", "HR", "Finance"] } }

// Con lista Java
List<String> depts = Arrays.asList("IT", "HR");
Bson filter = Filters.in("department", depts);
```

**NIN - NO en la lista:**
```java
Bson filter = Filters.nin("status", "deleted", "archived");
// → { "status": { "$nin": ["deleted", "archived"] } }
```

**Notas del presentador:**
IN es muy útil para filtrar por múltiples valores posibles. Equivale a múltiples OR en SQL: WHERE dept IN ('IT', 'HR').

---

## DIAPOSITIVA 11: Combinando Filtros

**Diseño:** Diagrama visual de AND/OR

### Contenido:

**Título:** AND y OR

**AND - Todas las condiciones:**
```java
Bson filter = Filters.and(
    Filters.eq("department", "IT"),
    Filters.eq("active", true),
    Filters.gt("age", 25)
);
// → { "$and": [{...}, {...}, {...}] }
```

**OR - Cualquier condición:**
```java
Bson filter = Filters.or(
    Filters.eq("department", "IT"),
    Filters.eq("department", "HR")
);
// → { "$or": [{...}, {...}] }
```

**Combinados:**
```java
Bson filter = Filters.and(
    Filters.eq("active", true),
    Filters.or(
        Filters.eq("department", "IT"),
        Filters.eq("department", "HR")
    )
);
```

**Notas del presentador:**
Se pueden anidar and() y or() para crear condiciones complejas. El equivalente SQL sería: WHERE active = true AND (dept = 'IT' OR dept = 'HR').

---

## DIAPOSITIVA 12: Filtro Regex

**Diseño:** Ejemplos de búsqueda de texto

### Contenido:

**Título:** Búsqueda con Expresiones Regulares

```java
// Buscar nombres que contengan "garcía" (case insensitive)
Bson filter = Filters.regex("name", "garcía", "i");
// → { "name": { "$regex": "garcía", "$options": "i" } }

// Buscar emails que terminen en "@empresa.com"
Bson filter = Filters.regex("email", "@empresa\\.com$");

// Buscar nombres que empiecen por "A"
Bson filter = Filters.regex("name", "^A");
```

**Opciones comunes:**
- `"i"` = case insensitive (ignora mayúsculas/minúsculas)
- `"m"` = multilínea
- `"s"` = permite . para coincidir con newline

**Notas del presentador:**
Regex es potente pero puede ser lento en colecciones grandes sin índices. Para búsqueda de texto completa, MongoDB tiene índices de texto especiales.

---

## DIAPOSITIVA 13: Ejecutando Consultas - find()

**Diseño:** Flujo de código con resultado

### Contenido:

**Título:** El Método find()

```java
MongoCollection<Document> collection = getCollection();

// find() sin filtro → todos los documentos
FindIterable<Document> todos = collection.find();

// find() con filtro
Bson filter = Filters.eq("department", "IT");
FindIterable<Document> filtrados = collection.find(filter);

// Obtener solo el primero
Document primero = collection.find(filter).first();

// Iterar resultados
for (Document doc : collection.find(filter)) {
    System.out.println(doc.getString("name"));
}
```

**Importante:** `find()` devuelve `FindIterable`, no una lista directa.

**Notas del presentador:**
FindIterable es "lazy" - no ejecuta la consulta hasta que iteramos. Esto permite añadir skip(), limit(), sort() antes de ejecutar.

---

## DIAPOSITIVA 14: FindIterable - Más Operaciones

**Diseño:** Cadena de métodos

### Contenido:

**Título:** Encadenando Operaciones

```java
FindIterable<Document> results = collection.find(filter)
    .sort(Sorts.ascending("name"))     // Ordenar
    .skip(10)                          // Saltar 10 primeros
    .limit(5)                          // Máximo 5 resultados
    .projection(Projections.include("name", "email")); // Solo estos campos

// Convertir a lista
List<Document> lista = new ArrayList<>();
results.into(lista);

// O más compacto
List<Document> lista = collection.find(filter).into(new ArrayList<>());
```

**Notas del presentador:**
El orden de skip/limit/sort en el código no importa; MongoDB los ejecuta en el orden lógico correcto. Pero ponerlos en orden lógico mejora la legibilidad.

---

## DIAPOSITIVA 15: Mapeo Manual

**Diseño:** Código de transformación

### Contenido:

**Título:** De Document a Objeto Java

```java
private User mapDocumentToUser(Document doc) {
    User user = new User();

    // Campos simples
    user.setId(doc.getObjectId("_id").toString());
    user.setName(doc.getString("name"));
    user.setEmail(doc.getString("email"));
    user.setDepartment(doc.getString("department"));
    user.setRole(doc.getString("role"));

    // Boolean con valor por defecto
    user.setActive(doc.getBoolean("active", false));

    // Fecha (requiere conversión)
    Date createdAt = doc.getDate("createdAt");
    if (createdAt != null) {
        user.setCreatedAt(createdAt.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime());
    }

    return user;
}
```

**Notas del presentador:**
Este mapeo manual es lo que Spring Data hace automáticamente. Es tedioso pero te da control total sobre cómo se transforman los datos.

---

## DIAPOSITIVA 16: Comparación Side by Side

**Diseño:** Dos columnas de código

### Contenido:

**Título:** findByDepartment: Spring Data vs API Nativa

**Spring Data:**
```java
public List<User> findByDepartment(String dept) {
    return userRepository.findByDepartment(dept);
}
```
**1 línea**

---

**API Nativa:**
```java
public List<User> findByDepartment(String dept) {
    MongoCollection<Document> col = getCollection();
    List<User> users = new ArrayList<>();

    Bson filter = Filters.eq("department", dept);

    for (Document doc : col.find(filter)) {
        users.add(mapDocumentToUser(doc));
    }

    return users;
}
```
**8+ líneas**

**Notas del presentador:**
Mismo resultado, diferente nivel de abstracción. Spring Data es más productivo; API Nativa da más control y comprensión.

---

## DIAPOSITIVA 17: countDocuments

**Diseño:** Comparación simple

### Contenido:

**Título:** Contando Documentos

```java
// Sin filtro - total de documentos
long total = collection.countDocuments();

// Con filtro
Bson filter = Filters.eq("department", "IT");
long countIT = collection.countDocuments(filter);
```

**¿Por qué no `find().size()`?**
- ❌ `find(filter).into(list).size()` → Carga TODOS los documentos
- ✅ `countDocuments(filter)` → Solo cuenta en el servidor

**Rendimiento:**
- 1 millón de documentos:
  - into().size() → Segundos/minutos, mucha memoria
  - countDocuments() → Milisegundos, sin memoria

**Notas del presentador:**
Siempre usar countDocuments() para contar. Es una operación optimizada del servidor que no transfiere datos.

---

## DIAPOSITIVA 18: Los TODOs de Hoy

**Diseño:** Lista de tareas con código

### Contenido:

**Título:** Métodos a Implementar

| Método | Patrón |
|--------|--------|
| `findAll()` | `collection.find()` + iterar + mapear |
| `findByDepartment()` | `Filters.eq()` + find + iterar + mapear |
| `countByDepartment()` | `Filters.eq()` + `countDocuments()` |

**Estructura común:**
```java
public List<User> findXxx(...) {
    MongoCollection<Document> collection = getCollection();
    List<User> users = new ArrayList<>();

    // 1. Crear filtro (si aplica)
    // 2. Ejecutar find()
    // 3. Iterar y mapear

    for (Document doc : collection.find(filtro)) {
        users.add(mapDocumentToUser(doc));
    }

    return users;
}
```

**Notas del presentador:**
El patrón es siempre el mismo: obtener colección, crear filtro, ejecutar, mapear. Una vez entendido, es mecánico.

---

## DIAPOSITIVA 19: Imports Necesarios

**Diseño:** Lista de imports organizada

### Contenido:

**Título:** No Olvides los Imports

```java
// Driver MongoDB
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoClient;
import com.mongodb.client.FindIterable;

// Filtros y tipos
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

// Java estándar
import java.util.ArrayList;
import java.util.List;
```

**Tip del IDE:**
- IntelliJ: `Alt + Enter` para importar automáticamente
- VS Code: `Ctrl + .` para sugerencias

**Notas del presentador:**
Hay varias clases con nombres similares (ej: Document de diferentes paquetes). Aseguraos de importar las correctas del driver MongoDB.

---

## DIAPOSITIVA 20: Errores Comunes

**Diseño:** Lista con soluciones

### Contenido:

**Título:** Problemas Típicos

**⚠️ ClassCastException al leer:**
```java
// ❌ Error si el campo es Integer
String value = doc.getString("age");

// ✅ Usar el tipo correcto
Integer value = doc.getInteger("age");
```

**⚠️ NullPointerException en ObjectId:**
```java
// ❌ Si _id no existe, falla
String id = doc.getObjectId("_id").toString();

// ✅ Verificar primero
ObjectId oid = doc.getObjectId("_id");
String id = (oid != null) ? oid.toString() : null;
```

**⚠️ Nombre de campo incorrecto:**
```java
// ❌ MongoDB es case-sensitive
Filters.eq("Department", "IT")  // No encuentra nada

// ✅ Nombre exacto
Filters.eq("department", "IT")
```

**Notas del presentador:**
La mayoría de errores vienen de no verificar tipos o nombres de campos. Revisar la clase User para confirmar nombres exactos.

---

## DIAPOSITIVA 21: Resumen

**Diseño:** Puntos clave en recuadros

### Contenido:

**Título:** Conceptos Clave

```
┌────────────────────────────────────────────────────────────┐
│  MongoCollection<Document>                                 │
│  Representa una colección, permite CRUD                    │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│  Document                                                  │
│  Documento BSON, similar a Map<String, Object>             │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│  Filters                                                   │
│  Clase helper para crear condiciones de búsqueda           │
│  eq(), and(), or(), regex(), gt(), lt()...                 │
└────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────┐
│  Mapeo Manual                                              │
│  Convertir Document ↔ Objeto Java campo a campo            │
└────────────────────────────────────────────────────────────┘
```

**Notas del presentador:**
Con estos conceptos podéis implementar cualquier operación de lectura. Las escrituras (insert, update, delete) siguen patrones similares.

---

## DIAPOSITIVA 22: Manos a la Obra

**Diseño:** Instrucciones claras

### Contenido:

**Título:** ¡A Practicar!

**Pasos:**
1. Abrir `NativeMongoUserServiceImpl.java`
2. Localizar los métodos TODO
3. Usar el patrón: colección → filtro → find → mapear
4. Ejecutar tests: `./gradlew test --tests "*Native*"`
5. Comparar tu código con la versión Spring Data

**Tiempo:** 1.5 horas

**Recuerda:**
- `getCollection()` ya existe
- `mapDocumentToUser()` ya existe
- Solo tienes que crear el filtro y el bucle

**Notas del presentador:**
El método de mapeo ya está implementado, no hay que escribirlo. Concentrarse en la lógica de filtrado e iteración.

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
║     "Entender la base te hace mejor en                     ║
║      cualquier nivel de abstracción"                       ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Notas del presentador:**
La próxima semana se defienden estos métodos. Asegurarse de entender bien la diferencia con Spring Data para poder explicarla.
