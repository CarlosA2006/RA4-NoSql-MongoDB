# Presentación: NoSQL vs SQL - Introducción a MongoDB

> **Instrucciones para exportar a Google Slides:**
> 1. Crear una nueva presentación en Google Slides
> 2. Usar el tema "Simple" o "Minimal" para mejor legibilidad
> 3. Copiar el contenido de cada diapositiva
> 4. Las imágenes se pueden crear con las herramientas de formas de Google Slides
> 5. Los bloques de código se pueden insertar como cuadros de texto con fuente monoespaciada

---

## DIAPOSITIVA 1: Portada

**Diseño sugerido:** Título centrado, fondo oscuro (azul marino o verde MongoDB)

### Contenido:

```
╔════════════════════════════════════════════════════════════╗
║                                                            ║
║              NoSQL vs SQL                                  ║
║                                                            ║
║         Introducción a MongoDB                             ║
║                                                            ║
║     ─────────────────────────────────────                  ║
║                                                            ║
║         Acceso a Datos - 2º DAM                            ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Texto:**
- Título: **NoSQL vs SQL**
- Subtítulo: **Introducción a MongoDB**
- Pie: Acceso a Datos - 2º DAM

**Notas del presentador:**
Bienvenida al módulo. Hoy vamos a descubrir un nuevo paradigma de bases de datos que complementa lo que ya sabéis de SQL.

---

## DIAPOSITIVA 2: Pregunta Inicial

**Diseño sugerido:** Fondo claro, texto grande centrado

### Contenido:

```
    ┌─────────────────────────────────────────────────┐
    │                                                 │
    │      ¿Todas las aplicaciones necesitan         │
    │         bases de datos relacionales?            │
    │                                                 │
    │                    🤔                           │
    │                                                 │
    └─────────────────────────────────────────────────┘
```

**Texto:**
- Pregunta central: **¿Todas las aplicaciones necesitan bases de datos relacionales?**

**Notas del presentador:**
Pregunta retórica para activar el pensamiento. Dejar 10-15 segundos para que reflexionen. Pedir 2-3 opiniones antes de continuar.

---

## DIAPOSITIVA 3: El Mundo ha Cambiado

**Diseño sugerido:** Lista con iconos, aparición progresiva

### Contenido:

**Título:** El mundo de los datos ha cambiado

**Lista (con animación de aparición):**
- 📱 Millones de usuarios simultáneos
- 🌍 Datos distribuidos globalmente
- 📊 Volúmenes masivos (Big Data)
- 🔄 Estructuras de datos variables
- ⚡ Necesidad de respuesta inmediata

**Notas del presentador:**
Las aplicaciones modernas tienen requisitos que las BD relacionales tradicionales no siempre pueden satisfacer eficientemente. Netflix, Uber, Amazon... manejan millones de peticiones por segundo.

---

## DIAPOSITIVA 4: SQL - Lo que Conocemos

**Diseño sugerido:** Dos columnas, iconos verdes para ventajas

### Contenido:

**Título:** Bases de Datos Relacionales (SQL)

**Columna izquierda - Características:**
- Tablas con filas y columnas
- Esquema fijo y predefinido
- Relaciones con claves foráneas
- Transacciones ACID
- Lenguaje SQL estándar

**Columna derecha - Ejemplos:**
- MySQL
- PostgreSQL
- Oracle
- SQL Server
- MariaDB

**Notas del presentador:**
Repaso rápido de lo que ya conocen. ACID = Atomicidad, Consistencia, Aislamiento, Durabilidad. El esquema fijo significa que hay que definir la estructura antes de insertar datos.

---

## DIAPOSITIVA 5: SQL - Ejemplo Visual

**Diseño sugerido:** Tabla visual con datos de ejemplo

### Contenido:

**Título:** Ejemplo: Tabla de Usuarios

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

**Notas del presentador:**
Estructura rígida. Si quisiéramos añadir un campo "teléfono secundario" solo para algunos usuarios, tendríamos que modificar la tabla y muchas filas tendrían NULL.

---

## DIAPOSITIVA 6: Limitaciones de SQL

**Diseño sugerido:** Lista con iconos rojos/naranjas de advertencia

### Contenido:

**Título:** ¿Cuándo SQL puede ser limitante?

**Lista:**
- ⚠️ Esquemas muy cambiantes → Migraciones constantes
- ⚠️ Datos heterogéneos → Muchos campos NULL
- ⚠️ Escalado horizontal → Complejo y costoso
- ⚠️ Datos anidados → Múltiples JOINs
- ⚠️ Alta velocidad de escritura → Bloqueos

**Notas del presentador:**
No significa que SQL sea malo, sino que hay escenarios donde otras opciones son más adecuadas. SQL sigue siendo excelente para transacciones financieras, inventarios, etc.

---

## DIAPOSITIVA 7: Entra NoSQL

**Diseño sugerido:** Texto grande con definición, fondo con gradiente

### Contenido:

**Título:** NoSQL: "Not Only SQL"

**Definición centrada:**
> Familia de bases de datos diseñadas para casos de uso específicos donde las bases relacionales no son la mejor opción.

**Subtexto:**
No es un reemplazo, es un **complemento**

**Notas del presentador:**
El nombre puede confundir. No significa "sin SQL" sino "no solo SQL". Muchas aplicaciones modernas usan AMBOS tipos según la necesidad.

---

## DIAPOSITIVA 8: Tipos de NoSQL

**Diseño sugerido:** 4 cuadrantes con iconos representativos

### Contenido:

**Título:** Tipos de Bases de Datos NoSQL

```
┌─────────────────────────┬─────────────────────────┐
│                         │                         │
│   📄 DOCUMENTOS         │   🔑 CLAVE-VALOR        │
│                         │                         │
│   MongoDB               │   Redis                 │
│   CouchDB               │   DynamoDB              │
│                         │   Memcached             │
├─────────────────────────┼─────────────────────────┤
│                         │                         │
│   📊 COLUMNAS           │   🕸️ GRAFOS             │
│                         │                         │
│   Cassandra             │   Neo4j                 │
│   HBase                 │   Amazon Neptune        │
│                         │                         │
└─────────────────────────┴─────────────────────────┘
```

**Notas del presentador:**
- Documentos: datos semi-estructurados (JSON)
- Clave-valor: caché, sesiones (muy rápido)
- Columnas: big data, analytics
- Grafos: redes sociales, recomendaciones

Nosotros nos centraremos en DOCUMENTOS con MongoDB.

---

## DIAPOSITIVA 9: ¿Por Qué MongoDB?

**Diseño sugerido:** Logo de MongoDB + lista de ventajas

### Contenido:

**Título:** MongoDB - La BD Documental más Popular

**Logo:** 🍃 (hoja verde - símbolo de MongoDB)

**Ventajas:**
- ✅ Líder del mercado en BD documentales
- ✅ Gran comunidad y documentación
- ✅ Fácil de aprender viniendo de JSON
- ✅ Escalable horizontalmente
- ✅ Flexible: sin esquema fijo
- ✅ Driver oficial para Java

**Notas del presentador:**
MongoDB es la 5ª base de datos más popular del mundo (db-engines.com). Usado por empresas como Adobe, eBay, Forbes, Google, Uber...

---

## DIAPOSITIVA 10: SQL vs MongoDB - Terminología

**Diseño sugerido:** Tabla comparativa lado a lado

### Contenido:

**Título:** Traduciendo Conceptos

```
┌─────────────────────┬─────────────────────┐
│        SQL          │      MongoDB        │
├─────────────────────┼─────────────────────┤
│    Base de datos    │   Base de datos     │
├─────────────────────┼─────────────────────┤
│       Tabla         │     Colección       │
├─────────────────────┼─────────────────────┤
│       Fila          │     Documento       │
├─────────────────────┼─────────────────────┤
│      Columna        │       Campo         │
├─────────────────────┼─────────────────────┤
│    PRIMARY KEY      │        _id          │
├─────────────────────┼─────────────────────┤
│       JOIN          │   Embedding/$lookup │
└─────────────────────┴─────────────────────┘
```

**Notas del presentador:**
Los conceptos se mapean bastante bien. La diferencia principal está en cómo se estructuran los datos dentro de cada "fila" (documento).

---

## DIAPOSITIVA 11: Documento JSON/BSON

**Diseño sugerido:** Bloque de código con colores de sintaxis

### Contenido:

**Título:** Anatomía de un Documento MongoDB

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

**Destacar con flechas:**
- `_id` → Identificador único automático
- `skills` → ¡Array embebido!
- `direccion` → ¡Objeto anidado!

**Notas del presentador:**
Esto en SQL requeriría 3 tablas: usuarios, skills_usuario, direcciones. Aquí todo está junto en un solo documento. BSON = Binary JSON (más eficiente que JSON texto).

---

## DIAPOSITIVA 12: Comparación Visual

**Diseño sugerido:** Dos columnas, SQL izquierda, MongoDB derecha

### Contenido:

**Título:** El Mismo Usuario: SQL vs MongoDB

**Columna SQL:**
```
TABLA usuarios:
id=1, nombre="Ana", dept_id=1

TABLA skills:
user_id=1, skill="Java"
user_id=1, skill="Spring"

TABLA direcciones:
user_id=1, ciudad="Madrid"
```
3 tablas, 2 JOINs necesarios

**Columna MongoDB:**
```json
{
  "nombre": "Ana",
  "departamento": "IT",
  "skills": ["Java", "Spring"],
  "direccion": { "ciudad": "Madrid" }
}
```
1 documento, 0 JOINs

**Notas del presentador:**
MongoDB favorece la desnormalización. Los datos relacionados se guardan juntos. Esto acelera las lecturas pero puede duplicar datos.

---

## DIAPOSITIVA 13: Esquema Flexible

**Diseño sugerido:** Dos documentos uno al lado del otro

### Contenido:

**Título:** Flexibilidad de Esquema

**Documento 1:**
```json
{
  "nombre": "Ana",
  "email": "ana@test.com"
}
```

**Documento 2:**
```json
{
  "nombre": "Carlos",
  "email": "carlos@test.com",
  "telefono": "612345678",
  "linkedin": "linkedin.com/carlos"
}
```

**Texto destacado:**
✅ Ambos documentos pueden estar en la **misma colección**
✅ No hay error por campos "extra"
✅ No hay NULLs innecesarios

**Notas del presentador:**
En SQL, tendríamos que tener columnas telefono y linkedin con NULL para Ana. En MongoDB, simplemente no existen esos campos en su documento.

---

## DIAPOSITIVA 14: ObjectId Explicado

**Diseño sugerido:** Diagrama del ObjectId desglosado

### Contenido:

**Título:** El Identificador _id (ObjectId)

```
        507f1f77bcf86cd799439011
        ├──────┤├──┤├──┤├──────┤
            │     │    │     │
            │     │    │     └── Contador (3 bytes)
            │     │    └──────── ID Proceso (2 bytes)
            │     └───────────── ID Máquina (3 bytes)
            └─────────────────── Timestamp (4 bytes)
```

**Puntos clave:**
- 12 bytes, representado como 24 caracteres hex
- Generado automáticamente si no se especifica
- Ordenable cronológicamente
- Único globalmente

**Notas del presentador:**
El timestamp permite ordenar por fecha de creación sin un campo extra. El ID de máquina y proceso evitan colisiones en sistemas distribuidos.

---

## DIAPOSITIVA 15: CRUD en MongoDB

**Diseño sugerido:** Tabla con operaciones y ejemplos

### Contenido:

**Título:** Operaciones CRUD

| Operación | SQL | MongoDB |
|-----------|-----|---------|
| **C**reate | `INSERT INTO...` | `db.users.insertOne({...})` |
| **R**ead | `SELECT * FROM...` | `db.users.find({...})` |
| **U**pdate | `UPDATE ... SET...` | `db.users.updateOne({...})` |
| **D**elete | `DELETE FROM...` | `db.users.deleteOne({...})` |

**Notas del presentador:**
La sintaxis es diferente pero los conceptos son idénticos. En el proyecto usaremos tanto la API nativa de MongoDB como Spring Data que abstrae estas operaciones.

---

## DIAPOSITIVA 16: Ejemplo de Consulta

**Diseño sugerido:** Código comparativo lado a lado

### Contenido:

**Título:** Buscar usuarios de IT activos

**SQL:**
```sql
SELECT * FROM usuarios
WHERE departamento = 'IT'
  AND activo = true;
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
collection.find(
  Filters.and(
    Filters.eq("departamento", "IT"),
    Filters.eq("activo", true)
  )
)
```

**Notas del presentador:**
Los filtros en MongoDB son documentos JSON. En Java usamos clases helper como Filters para construirlos. Es más verboso pero muy explícito.

---

## DIAPOSITIVA 17: ¿Cuándo Usar MongoDB?

**Diseño sugerido:** Dos columnas con iconos ✅ y ❌

### Contenido:

**Título:** MongoDB es Ideal Para...

**Columna ✅ USAR:**
- Catálogos de productos
- Gestión de contenido (CMS)
- Datos de IoT / sensores
- Perfiles de usuario
- Logs y analytics
- Aplicaciones móviles
- Prototipado rápido

**Columna ❌ EVITAR:**
- Transacciones bancarias complejas
- Sistemas con muchas relaciones
- Datos altamente normalizados
- Requisitos ACID estrictos
- Reporting complejo con JOINs

**Notas del presentador:**
La clave es elegir la herramienta correcta para cada trabajo. Muchas empresas usan SQL para finanzas y MongoDB para el catálogo de productos en la misma aplicación.

---

## DIAPOSITIVA 18: Quién Usa MongoDB

**Diseño sugerido:** Logos de empresas conocidas

### Contenido:

**Título:** Empresas que Usan MongoDB

**Logos/Nombres en grid:**
```
┌─────────┬─────────┬─────────┬─────────┐
│ Netflix │  Uber   │  Adobe  │  eBay   │
├─────────┼─────────┼─────────┼─────────┤
│ Forbes  │  Cisco  │ Bosch   │  SAP    │
├─────────┼─────────┼─────────┼─────────┤
│  EA     │ Verizon │ Toyota  │ Expedia │
└─────────┴─────────┴─────────┴─────────┘
```

**Dato:** Más de 46,000 empresas usan MongoDB

**Notas del presentador:**
Estas empresas manejan millones de usuarios y peticiones. MongoDB les permite escalar horizontalmente añadiendo más servidores cuando es necesario.

---

## DIAPOSITIVA 19: Resumen Comparativo

**Diseño sugerido:** Tabla resumen con colores

### Contenido:

**Título:** SQL vs MongoDB - Resumen

| Aspecto | SQL | MongoDB |
|---------|-----|---------|
| Modelo | Relacional | Documental |
| Esquema | Rígido | Flexible |
| Escalado | Vertical | Horizontal |
| Relaciones | JOINs | Embedding |
| Transacciones | Nativas ACID | Limitadas* |
| Consultas | SQL | JSON/BSON |
| Ideal para | Datos estructurados | Datos semi-estructurados |

*MongoDB soporta transacciones multi-documento desde v4.0, pero no es su punto fuerte.

**Notas del presentador:**
No hay un ganador absoluto. Son herramientas complementarias. Un buen desarrollador sabe cuándo usar cada una.

---

## DIAPOSITIVA 20: El Proyecto

**Diseño sugerido:** Captura o diagrama del proyecto

### Contenido:

**Título:** Nuestro Proyecto: Gestión de Usuarios

**Diagrama:**
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

**Lo que aprenderemos:**
- Operaciones CRUD de dos formas diferentes
- Consultas con filtros dinámicos
- Agregaciones básicas
- Comparar ambos enfoques

**Notas del presentador:**
Durante las próximas 6 semanas implementaréis métodos en ambos módulos. Esto os dará perspectiva para elegir el enfoque adecuado en proyectos reales.

---

## DIAPOSITIVA 21: Pregunta de Cierre

**Diseño sugerido:** Pregunta centrada, espacio para respuestas

### Contenido:

**Título:** Reflexión

```
┌─────────────────────────────────────────────────────────┐
│                                                         │
│   Si tuvieras que desarrollar una app de               │
│   e-commerce con millones de productos...              │
│                                                         │
│   ¿Usarías SQL, MongoDB, o ambos?                      │
│                                                         │
│   ¿Para qué parte usarías cada uno?                    │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**Notas del presentador:**
Discusión abierta 5 minutos. Respuesta ideal:
- MongoDB para catálogo de productos (flexible, escalable)
- SQL para pedidos y pagos (transacciones ACID)
- Posiblemente Redis para carrito de compra (sesiones)

---

## DIAPOSITIVA 22: Próximos Pasos

**Diseño sugerido:** Lista con iconos de check

### Contenido:

**Título:** ¿Qué Viene Ahora?

**Lista:**
1. 🔧 Poner en marcha el proyecto
2. 🌐 Explorar Swagger UI
3. 📁 Conocer la estructura del código
4. 🔍 Analizar los métodos ya implementados
5. 📝 Identificar los TODOs a completar

**Texto final:**
¡Manos a la obra! 🚀

**Notas del presentador:**
Transición a la parte práctica de la sesión. Verificar que todos tienen el entorno preparado antes de continuar.

---

## DIAPOSITIVA 23: Recursos

**Diseño sugerido:** Lista de enlaces con iconos

### Contenido:

**Título:** Para Saber Más

**Recursos:**
- 📚 [docs.mongodb.com](https://docs.mongodb.com) - Documentación oficial
- 🎓 [university.mongodb.com](https://university.mongodb.com) - Cursos gratuitos
- 📊 [db-engines.com](https://db-engines.com) - Rankings de BD
- 🍃 [mongodb.com/try](https://mongodb.com/try) - MongoDB Atlas (cloud)

**En el proyecto:**
- `README.md` - Guía completa
- `ARQUITECTURA.md` - Diseño técnico

**Notas del presentador:**
MongoDB University tiene cursos gratuitos con certificación. Muy recomendables para profundizar.

---

## DIAPOSITIVA 24: Final

**Diseño sugerido:** Igual que la portada

### Contenido:

```
╔════════════════════════════════════════════════════════════╗
║                                                            ║
║                    ¿Preguntas?                             ║
║                                                            ║
║                       🍃                                   ║
║                                                            ║
║     ─────────────────────────────────────                  ║
║                                                            ║
║         Acceso a Datos - 2º DAM                            ║
║                                                            ║
╚════════════════════════════════════════════════════════════╝
```

**Notas del presentador:**
Resolver dudas antes de pasar a la práctica. Si no hay preguntas, continuar con la puesta en marcha del proyecto.

---

# ANEXO: Notas de Diseño para Google Slides

## Paleta de Colores Sugerida

| Elemento | Color | Hex |
|----------|-------|-----|
| Fondo principal | Blanco | #FFFFFF |
| Títulos | Verde MongoDB | #00684A |
| Texto principal | Gris oscuro | #333333 |
| Código/técnico | Azul | #1A73E8 |
| Advertencias | Naranja | #F9A825 |
| Positivo | Verde | #34A853 |
| Negativo | Rojo | #EA4335 |

## Fuentes Sugeridas

- **Títulos:** Roboto Bold, 36-44pt
- **Cuerpo:** Roboto Regular, 20-24pt
- **Código:** Roboto Mono, 16-18pt

## Transiciones

- Usar transiciones simples (Fade o None)
- Evitar transiciones llamativas que distraigan
- Animación de aparición para listas largas

## Tiempo por Diapositiva

| Diapositivas | Tiempo |
|--------------|--------|
| 1-3 | 5 min (introducción) |
| 4-6 | 10 min (SQL conocido) |
| 7-9 | 10 min (NoSQL concepto) |
| 10-14 | 15 min (MongoDB específico) |
| 15-18 | 10 min (uso práctico) |
| 19-24 | 10 min (resumen y cierre) |

**Total:** ~60 minutos

---

# Exportación Rápida

Para crear la presentación más rápidamente:

1. **Google Slides:** Crear presentación nueva → Copiar contenido diapositiva por diapositiva

2. **PowerPoint:** Mismo proceso, luego subir a Google Drive y abrir con Slides

3. **Canva:** Buscar template "Minimal" → Adaptar contenido

4. **Marp (Markdown):** Convertir este archivo con la herramienta Marp CLI:
   ```bash
   npx @marp-team/marp-cli 01_NoSQL_vs_SQL.md --pptx
   ```
