---
marp: true
theme: default
paginate: true
backgroundColor: #fff
color: #333
style: |
  section {
    font-family: 'Roboto', sans-serif;
  }
  h1 {
    color: #00684A;
  }
  h2 {
    color: #00684A;
  }
  code {
    background-color: #f5f5f5;
    font-family: 'Roboto Mono', monospace;
  }
  .positive { color: #34A853; }
  .negative { color: #EA4335; }
  .warning { color: #F9A825; }
---

<!-- _class: lead -->
<!-- _paginate: false -->

# **NoSQL vs SQL**

## Introducción a MongoDB

---

Acceso a Datos - 2º DAM

---

<!-- _class: lead -->

# ¿Todas las aplicaciones necesitan bases de datos relacionales?

🤔

---

## El mundo de los datos ha cambiado

- 📱 Millones de usuarios simultáneos
- 🌍 Datos distribuidos globalmente
- 📊 Volúmenes masivos (Big Data)
- 🔄 Estructuras de datos variables
- ⚡ Necesidad de respuesta inmediata

<!--
Las aplicaciones modernas tienen requisitos que las BD relacionales tradicionales no siempre pueden satisfacer eficientemente. Netflix, Uber, Amazon... manejan millones de peticiones por segundo.
-->

---

## Bases de Datos Relacionales (SQL)

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 40px;">

<div>

### Características
- Tablas con filas y columnas
- Esquema fijo y predefinido
- Relaciones con claves foráneas
- Transacciones ACID
- Lenguaje SQL estándar

</div>

<div>

### Ejemplos
- MySQL
- PostgreSQL
- Oracle
- SQL Server
- MariaDB

</div>

</div>

<!--
Repaso rápido de lo que ya conocen. ACID = Atomicidad, Consistencia, Aislamiento, Durabilidad. El esquema fijo significa que hay que definir la estructura antes de insertar datos.
-->

---

## Ejemplo: Tabla de Usuarios

```
┌─────────────────────────────────────────────────────────────────┐
│                        TABLA: usuarios                          │
├────────┬─────────────┬─────────────────────┬──────────┬─────────┤
│   id   │   nombre    │       email         │ dept_id  │  activo │
├────────┼─────────────┼─────────────────────┼──────────┼─────────┤
│   1    │ Ana García  │ ana@empresa.com     │    1     │  true   │
│   2    │ Carlos López│ carlos@empresa.com  │    1     │  true   │
│   3    │ María Ruiz  │ maria@empresa.com   │    2     │  false  │
└────────┴─────────────┴─────────────────────┴──────────┴─────────┘

          dept_id ──────────────▶ FOREIGN KEY a tabla departamentos
```

<!--
Estructura rígida. Si quisiéramos añadir un campo "teléfono secundario" solo para algunos usuarios, tendríamos que modificar la tabla y muchas filas tendrían NULL.
-->

---

## ¿Cuándo SQL puede ser limitante?

- ⚠️ Esquemas muy cambiantes → Migraciones constantes
- ⚠️ Datos heterogéneos → Muchos campos NULL
- ⚠️ Escalado horizontal → Complejo y costoso
- ⚠️ Datos anidados → Múltiples JOINs
- ⚠️ Alta velocidad de escritura → Bloqueos

<!--
No significa que SQL sea malo, sino que hay escenarios donde otras opciones son más adecuadas. SQL sigue siendo excelente para transacciones financieras, inventarios, etc.
-->

---

<!-- _class: lead -->

# NoSQL: "Not Only SQL"

> Familia de bases de datos diseñadas para casos de uso específicos donde las bases relacionales no son la mejor opción.

### No es un reemplazo, es un **complemento**

<!--
El nombre puede confundir. No significa "sin SQL" sino "no solo SQL". Muchas aplicaciones modernas usan AMBOS tipos según la necesidad.
-->

---

## Tipos de Bases de Datos NoSQL

<div style="display: grid; grid-template-columns: 1fr 1fr; grid-template-rows: 1fr 1fr; gap: 20px; text-align: center;">

<div style="border: 2px solid #00684A; padding: 20px; border-radius: 10px;">

### 📄 DOCUMENTOS
MongoDB
CouchDB

</div>

<div style="border: 2px solid #00684A; padding: 20px; border-radius: 10px;">

### 🔑 CLAVE-VALOR
Redis
DynamoDB
Memcached

</div>

<div style="border: 2px solid #00684A; padding: 20px; border-radius: 10px;">

### 📊 COLUMNAS
Cassandra
HBase

</div>

<div style="border: 2px solid #00684A; padding: 20px; border-radius: 10px;">

### 🕸️ GRAFOS
Neo4j
Amazon Neptune

</div>

</div>

<!--
- Documentos: datos semi-estructurados (JSON)
- Clave-valor: caché, sesiones (muy rápido)
- Columnas: big data, analytics
- Grafos: redes sociales, recomendaciones

Nosotros nos centraremos en DOCUMENTOS con MongoDB.
-->

---

## MongoDB - La BD Documental más Popular

### 🍃

### Ventajas:
- ✅ Líder del mercado en BD documentales
- ✅ Gran comunidad y documentación
- ✅ Fácil de aprender viniendo de JSON
- ✅ Escalable horizontalmente
- ✅ Flexible: sin esquema fijo
- ✅ Driver oficial para Java

<!--
MongoDB es la 5ª base de datos más popular del mundo (db-engines.com). Usado por empresas como Adobe, eBay, Forbes, Google, Uber...
-->

---

## Traduciendo Conceptos

| SQL | MongoDB |
|-----|---------|
| Base de datos | Base de datos |
| Tabla | Colección |
| Fila | Documento |
| Columna | Campo |
| PRIMARY KEY | _id |
| JOIN | Embedding/$lookup |

<!--
Los conceptos se mapean bastante bien. La diferencia principal está en cómo se estructuran los datos dentro de cada "fila" (documento).
-->

---

## Anatomía de un Documento MongoDB

```json
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),  // ← Identificador único automático
  "nombre": "Ana García",
  "email": "ana.garcia@empresa.com",
  "departamento": "IT",
  "activo": true,
  "fechaAlta": ISODate("2024-01-15"),
  "skills": ["Java", "Spring", "MongoDB"],      // ← ¡Array embebido!
  "direccion": {                                // ← ¡Objeto anidado!
    "ciudad": "Madrid",
    "pais": "España"
  }
}
```

<!--
Esto en SQL requeriría 3 tablas: usuarios, skills_usuario, direcciones. Aquí todo está junto en un solo documento. BSON = Binary JSON (más eficiente que JSON texto).
-->

---

## El Mismo Usuario: SQL vs MongoDB

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 40px;">

<div>

### SQL
```
TABLA usuarios:
id=1, nombre="Ana", dept_id=1

TABLA skills:
user_id=1, skill="Java"
user_id=1, skill="Spring"

TABLA direcciones:
user_id=1, ciudad="Madrid"
```
**3 tablas, 2 JOINs necesarios**

</div>

<div>

### MongoDB
```json
{
  "nombre": "Ana",
  "departamento": "IT",
  "skills": ["Java", "Spring"],
  "direccion": { 
    "ciudad": "Madrid" 
  }
}
```
**1 documento, 0 JOINs**

</div>

</div>

<!--
MongoDB favorece la desnormalización. Los datos relacionados se guardan juntos. Esto acelera las lecturas pero puede duplicar datos.
-->

---

## Flexibilidad de Esquema

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 40px;">

<div>

### Documento 1
```json
{
  "nombre": "Ana",
  "email": "ana@test.com"
}
```

</div>

<div>

### Documento 2
```json
{
  "nombre": "Carlos",
  "email": "carlos@test.com",
  "telefono": "612345678",
  "linkedin": "linkedin.com/carlos"
}
```

</div>

</div>

✅ Ambos documentos pueden estar en la **misma colección**
✅ No hay error por campos "extra"
✅ No hay NULLs innecesarios

<!--
En SQL, tendríamos que tener columnas telefono y linkedin con NULL para Ana. En MongoDB, simplemente no existen esos campos en su documento.
-->

---

## El Identificador _id (ObjectId)

```
        507f1f77bcf86cd799439011
        ├──────┤├──┤├──┤├──────┤
            │     │    │     │
            │     │    │     └── Contador (3 bytes)
            │     │    └──────── ID Proceso (2 bytes)
            │     └───────────── ID Máquina (3 bytes)
            └─────────────────── Timestamp (4 bytes)
```

### Puntos clave:
- 12 bytes, representado como 24 caracteres hex
- Generado automáticamente si no se especifica
- Ordenable cronológicamente
- Único globalmente

<!--
El timestamp permite ordenar por fecha de creación sin un campo extra. El ID de máquina y proceso evitan colisiones en sistemas distribuidos.
-->

---

## Operaciones CRUD

| Operación | SQL | MongoDB |
|-----------|-----|---------|
| **C**reate | `INSERT INTO...` | `db.users.insertOne({...})` |
| **R**ead | `SELECT * FROM...` | `db.users.find({...})` |
| **U**pdate | `UPDATE ... SET...` | `db.users.updateOne({...})` |
| **D**elete | `DELETE FROM...` | `db.users.deleteOne({...})` |

<!--
La sintaxis es diferente pero los conceptos son idénticos. En el proyecto usaremos tanto la API nativa de MongoDB como Spring Data que abstrae estas operaciones.
-->

---

## Buscar usuarios de IT activos

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 30px;">

<div>

### SQL
```sql
SELECT * FROM usuarios
WHERE departamento = 'IT'
  AND activo = true;
```

</div>

<div>

### MongoDB Shell
```javascript
db.usuarios.find({
  departamento: "IT",
  activo: true
})
```

</div>

</div>

### MongoDB Java
```java
collection.find(
  Filters.and(
    Filters.eq("departamento", "IT"),
    Filters.eq("activo", true)
  )
)
```

<!--
Los filtros en MongoDB son documentos JSON. En Java usamos clases helper como Filters para construirlos. Es más verboso pero muy explícito.
-->

---

## MongoDB es Ideal Para...

<div style="display: grid; grid-template-columns: 1fr 1fr; gap: 40px;">

<div>

### ✅ USAR
- Catálogos de productos
- Gestión de contenido (CMS)
- Datos de IoT / sensores
- Perfiles de usuario
- Logs y analytics
- Aplicaciones móviles
- Prototipado rápido

</div>

<div>

### ❌ EVITAR
- Transacciones bancarias complejas
- Sistemas con muchas relaciones
- Datos altamente normalizados
- Requisitos ACID estrictos
- Reporting complejo con JOINs

</div>

</div>

<!--
La clave es elegir la herramienta correcta para cada trabajo. Muchas empresas usan SQL para finanzas y MongoDB para el catálogo de productos en la misma aplicación.
-->

---

## Empresas que Usan MongoDB

<div style="display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; text-align: center; margin: 40px 0;">

**Netflix**  **Uber**  **Adobe**  **eBay**

**Forbes**  **Cisco**  **Bosch**  **SAP**

**EA**  **Verizon**  **Toyota**  **Expedia**

</div>

### 📊 Más de 46,000 empresas usan MongoDB

<!--
Estas empresas manejan millones de usuarios y peticiones. MongoDB les permite escalar horizontalmente añadiendo más servidores cuando es necesario.
-->

---

## SQL vs MongoDB - Resumen

| Aspecto | SQL | MongoDB |
|---------|-----|---------|
| Modelo | Relacional | Documental |
| Esquema | Rígido | Flexible |
| Escalado | Vertical | Horizontal |
| Relaciones | JOINs | Embedding |
| Transacciones | Nativas ACID | Limitadas* |
| Consultas | SQL | JSON/BSON |
| Ideal para | Datos estructurados | Datos semi-estructurados |

\*MongoDB soporta transacciones multi-documento desde v4.0, pero no es su punto fuerte.

<!--
No hay un ganador absoluto. Son herramientas complementarias. Un buen desarrollador sabe cuándo usar cada una.
-->

---

## Nuestro Proyecto: Gestión de Usuarios

```
┌─────────────────────────────────────────────────────────┐
│                    Spring Boot                          │
├──────────────────────┬──────────────────────────────────┤
│   API Nativa         │        Spring Data               │
│   (Driver MongoDB)   │        (Abstracción)             │
├──────────────────────┴──────────────────────────────────┤
│                     MongoDB                             │
│                  Colección: users                       │
└─────────────────────────────────────────────────────────┘
```

### Lo que aprenderemos:
- Operaciones CRUD de dos formas diferentes
- Consultas con filtros dinámicos
- Agregaciones básicas
- Comparar ambos enfoques

<!--
Durante las próximas 6 semanas implementaréis métodos en ambos módulos. Esto os dará perspectiva para elegir el enfoque adecuado en proyectos reales.
-->

---

<!-- _class: lead -->

# Reflexión

### Si tuvieras que desarrollar una app de e-commerce con millones de productos...

¿Usarías SQL, MongoDB, o ambos?

¿Para qué parte usarías cada uno?

<!--
Discusión abierta 5 minutos. Respuesta ideal:
- MongoDB para catálogo de productos (flexible, escalable)
- SQL para pedidos y pagos (transacciones ACID)
- Posiblemente Redis para carrito de compra (sesiones)
-->

---

## ¿Qué Viene Ahora?

1. 🔧 Poner en marcha el proyecto
2. 🌐 Explorar Swagger UI
3. 📁 Conocer la estructura del código
4. 🔍 Analizar los métodos ya implementados
5. 📝 Identificar los TODOs a completar

### ¡Manos a la obra! 🚀

<!--
Transición a la parte práctica de la sesión. Verificar que todos tienen el entorno preparado antes de continuar.
-->

---

## Para Saber Más

### Recursos:
- 📚 [docs.mongodb.com](https://docs.mongodb.com) - Documentación oficial
- 🎓 [university.mongodb.com](https://university.mongodb.com) - Cursos gratuitos
- 📊 [db-engines.com](https://db-engines.com) - Rankings de BD
- 🍃 [mongodb.com/try](https://mongodb.com/try) - MongoDB Atlas (cloud)

### En el proyecto:
- `README.md` - Guía completa
- `ARQUITECTURA.md` - Diseño técnico

<!--
MongoDB University tiene cursos gratuitos con certificación. Muy recomendables para profundizar.
-->

---

<!-- _class: lead -->
<!-- _paginate: false -->

# ¿Preguntas?

🍃

---

Acceso a Datos - 2º DAM

<!--
Resolver dudas antes de pasar a la práctica. Si no hay preguntas, continuar con la puesta en marcha del proyecto.
-->
