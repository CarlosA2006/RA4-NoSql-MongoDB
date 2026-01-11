---
marp: true
theme: default
paginate: true
backgroundColor: #fff
color: #333
header: 'NoSQL vs SQL - Introducción a MongoDB'
footer: 'Acceso a Datos - 2º DAM'
---

<!-- _class: lead -->
<!-- _paginate: false -->

# NoSQL vs SQL

## Introducción a MongoDB

### Acceso a Datos - 2º DAM

---

<!-- _class: lead -->

# ¿Todas las aplicaciones necesitan bases de datos relacionales?

🤔

---

## El Mundo de los Datos ha Cambiado

- 📱 **Millones de usuarios simultáneos**
- 🌍 **Datos distribuidos globalmente**
- 📊 **Volúmenes masivos** (Big Data)
- 🔄 **Estructuras de datos variables**
- ⚡ **Necesidad de respuesta inmediata**

> Las aplicaciones modernas tienen requisitos que las BD relacionales tradicionales no siempre pueden satisfacer eficientemente.

---

## Bases de Datos Relacionales (SQL)

### Características
- Tablas con filas y columnas
- Esquema fijo y predefinido
- Relaciones con claves foráneas
- Transacciones ACID
- Lenguaje SQL estándar

### Ejemplos
MySQL • PostgreSQL • Oracle • SQL Server • MariaDB

---

## Ejemplo: Tabla de Usuarios

```sql
┌────┬──────────────┬─────────────────────┬─────────┬────────┐
│ id │   nombre     │       email         │ dept_id │ activo │
├────┼──────────────┼─────────────────────┼─────────┼────────┤
│  1 │ Ana García   │ ana@empresa.com     │    1    │  true  │
│  2 │ Carlos López │ carlos@empresa.com  │    1    │  true  │
│  3 │ María Ruiz   │ maria@empresa.com   │    2    │  false │
└────┴──────────────┴─────────────────────┴─────────┴────────┘

         dept_id ──────▶ FOREIGN KEY a tabla departamentos
```

---

## Limitaciones de SQL

⚠️ **Esquemas muy cambiantes** → Migraciones constantes
⚠️ **Datos heterogéneos** → Muchos campos NULL
⚠️ **Escalado horizontal** → Complejo y costoso
⚠️ **Datos anidados** → Múltiples JOINs
⚠️ **Alta velocidad de escritura** → Bloqueos

> No significa que SQL sea malo, sino que hay escenarios donde otras opciones son más adecuadas.

---

## NoSQL: "Not Only SQL"

> Familia de bases de datos diseñadas para casos de uso específicos donde las bases relacionales no son la mejor opción.

### No es un reemplazo, es un **complemento**

---

## Tipos de Bases de Datos NoSQL

```
┌─────────────────────┬─────────────────────┐
│  📄 DOCUMENTOS      │  🔑 CLAVE-VALOR     │
│                     │                     │
│  MongoDB            │  Redis              │
│  CouchDB            │  DynamoDB           │
│                     │  Memcached          │
├─────────────────────┼─────────────────────┤
│  📊 COLUMNAS        │  🕸️ GRAFOS          │
│                     │                     │
│  Cassandra          │  Neo4j              │
│  HBase              │  Amazon Neptune     │
└─────────────────────┴─────────────────────┘
```

**Nosotros nos centramos en: DOCUMENTOS con MongoDB** 🍃

---

## ¿Por Qué MongoDB?

### MongoDB - La BD Documental más Popular 🍃

✅ Líder del mercado en BD documentales
✅ Gran comunidad y documentación
✅ Fácil de aprender viniendo de JSON
✅ Escalable horizontalmente
✅ Flexible: sin esquema fijo
✅ Driver oficial para Java

> Usado por Adobe, eBay, Forbes, Google, Uber...

---

## SQL vs MongoDB - Terminología

| SQL | MongoDB |
|-----|---------|
| Base de datos | Base de datos |
| **Tabla** | **Colección** |
| **Fila** | **Documento** |
| **Columna** | **Campo** |
| PRIMARY KEY | _id |
| JOIN | Embedding/$lookup |

---

## Anatomía de un Documento MongoDB

```json
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "nombre": "Ana García",
  "email": "ana.garcia@empresa.com",
  "departamento": "IT",
  "activo": true,
  "fechaAlta": ISODate("2024-01-15"),
  "skills": ["Java", "Spring", "MongoDB"],
  "direccion": {
    "ciudad": "Madrid",
    "pais": "España"
  }
}
```

- `_id` → Identificador único automático
- `skills` → ¡Array embebido!
- `direccion` → ¡Objeto anidado!

---

## El Mismo Usuario: SQL vs MongoDB

### SQL
```
TABLA usuarios:     id=1, nombre="Ana", dept_id=1
TABLA skills:       user_id=1, skill="Java"
                    user_id=1, skill="Spring"
TABLA direcciones:  user_id=1, ciudad="Madrid"
```
**3 tablas, 2 JOINs necesarios**

### MongoDB
```json
{
  "nombre": "Ana",
  "departamento": "IT",
  "skills": ["Java", "Spring"],
  "direccion": { "ciudad": "Madrid" }
}
```
**1 documento, 0 JOINs**

---

## Flexibilidad de Esquema

```json
// Documento 1
{
  "nombre": "Ana",
  "email": "ana@test.com"
}

// Documento 2 - ¡Misma colección!
{
  "nombre": "Carlos",
  "email": "carlos@test.com",
  "telefono": "612345678",
  "linkedin": "linkedin.com/carlos"
}
```

✅ Ambos documentos pueden estar en la **misma colección**
✅ No hay error por campos "extra"
✅ No hay NULLs innecesarios

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

- 12 bytes, representado como 24 caracteres hex
- **Generado automáticamente** si no se especifica
- Ordenable cronológicamente
- **Único globalmente**

---

## CRUD en MongoDB

| Operación | SQL | MongoDB |
|-----------|-----|---------|
| **C**reate | `INSERT INTO...` | `db.users.insertOne({...})` |
| **R**ead | `SELECT * FROM...` | `db.users.find({...})` |
| **U**pdate | `UPDATE ... SET...` | `db.users.updateOne({...})` |
| **D**elete | `DELETE FROM...` | `db.users.deleteOne({...})` |

---

## Ejemplo de Consulta

### Buscar usuarios de IT activos

**SQL:**
```sql
SELECT * FROM usuarios
WHERE departamento = 'IT' AND activo = true;
```

**MongoDB Shell:**
```javascript
db.usuarios.find({
  departamento: "IT",
  activo: true
})
```

**MongoDB Java:**
```java
collection.find(Filters.and(
  Filters.eq("departamento", "IT"),
  Filters.eq("activo", true)
))
```

---

## ¿Cuándo Usar MongoDB?

### ✅ MongoDB es Ideal Para...
- Catálogos de productos
- Gestión de contenido (CMS)
- Datos de IoT / sensores
- Perfiles de usuario
- Logs y analytics
- Aplicaciones móviles
- Prototipado rápido

### ❌ Evitar para...
- Transacciones bancarias complejas
- Sistemas con muchas relaciones
- Datos altamente normalizados
- Requisitos ACID estrictos
- Reporting complejo con JOINs

---

## Empresas que Usan MongoDB

```
┌─────────┬─────────┬─────────┬─────────┐
│ Netflix │  Uber   │  Adobe  │  eBay   │
├─────────┼─────────┼─────────┼─────────┤
│ Forbes  │  Cisco  │  Bosch  │   SAP   │
├─────────┼─────────┼─────────┼─────────┤
│   EA    │ Verizon │ Toyota  │ Expedia │
└─────────┴─────────┴─────────┴─────────┘
```

### Más de **46,000 empresas** usan MongoDB

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

*MongoDB soporta transacciones multi-documento desde v4.0

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

---

<!-- _class: lead -->

## Reflexión

### Si tuvieras que desarrollar una app de e-commerce con millones de productos...

¿Usarías SQL, MongoDB, o ambos?
¿Para qué parte usarías cada uno?

---

## Próximos Pasos

1. 🔧 Poner en marcha el proyecto
2. 🌐 Explorar Swagger UI
3. 📁 Conocer la estructura del código
4. 🔍 Analizar los métodos ya implementados
5. 📝 Identificar los TODOs a completar

### ¡Manos a la obra! 🚀

---

## Para Saber Más

📚 [docs.mongodb.com](https://docs.mongodb.com) - Documentación oficial
🎓 [university.mongodb.com](https://university.mongodb.com) - Cursos gratuitos
📊 [db-engines.com](https://db-engines.com) - Rankings de BD
🍃 [mongodb.com/try](https://mongodb.com/try) - MongoDB Atlas (cloud)

### En el proyecto:
- `README.md` - Guía completa
- `ARQUITECTURA.md` - Diseño técnico

---

<!-- _class: lead -->
<!-- _paginate: false -->

# ¿Preguntas?

## 🍃
