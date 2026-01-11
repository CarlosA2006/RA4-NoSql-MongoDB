# Presentaciones Marp - Módulo MongoDB

Este directorio contiene las presentaciones del módulo en formato Marp, listas para convertir a PowerPoint, PDF o HTML.

## 📋 Contenido

| Archivo | Semana | Tema | Diapositivas |
|---------|--------|------|--------------|
| `01_NoSQL_vs_SQL.md` | 1 | Introducción a NoSQL y MongoDB | ~27 |
| `02_Spring_Data_Repositories.md` | 2 | MongoRepository y Query Methods | ~23 |
| `03_API_Nativa_MongoDB.md` | 3 | Driver Java y operaciones directas | ~23 |
| `04_MongoTemplate_Criteria.md` | 4 | Consultas dinámicas avanzadas | ~23 |
| `05_Aggregation_Framework.md` | 6 | Pipeline de agregación | ~24 |

## 🚀 Instalación de Marp CLI

### Opción 1: Instalación Global (recomendado)

```bash
npm install -g @marp-team/marp-cli
```

### Opción 2: Uso con npx (sin instalar)

```bash
npx @marp-team/marp-cli [opciones]
```

### Verificar instalación

```bash
marp --version
```

## 📤 Convertir Presentaciones

### Método 1: Usando el Script (Recomendado)

```bash
# Convertir TODAS las presentaciones a PowerPoint
./convertir.sh pptx

# Convertir todas a PDF
./convertir.sh pdf

# Convertir todas a HTML
./convertir.sh html

# Convertir una presentación específica
./convertir.sh pptx 01_NoSQL_vs_SQL.md
```

Los archivos se generan en el directorio `output/`.

### Método 2: Comandos Manuales

#### PowerPoint (.pptx)
```bash
marp 01_NoSQL_vs_SQL.md --pptx -o output/01_NoSQL_vs_SQL.pptx
```

#### PDF
```bash
marp 01_NoSQL_vs_SQL.md --pdf -o output/01_NoSQL_vs_SQL.pdf
```

#### HTML (presentación interactiva)
```bash
marp 01_NoSQL_vs_SQL.md --html -o output/01_NoSQL_vs_SQL.html
```

#### HTML con servidor local (para vista previa)
```bash
marp --server 01_NoSQL_vs_SQL.md
# Abre automáticamente en http://localhost:8080
```

## 🎨 Personalización

### Temas Disponibles

Marp incluye tres temas por defecto:
- `default` (usado en estas presentaciones)
- `gaia`
- `uncover`

Para cambiar el tema, modifica el frontmatter del archivo `.md`:

```yaml
---
marp: true
theme: gaia  # Cambiar a gaia o uncover
---
```

### CSS Personalizado

Puedes añadir estilos personalizados en el frontmatter:

```yaml
---
marp: true
style: |
  section {
    background-color: #1a1a1a;
    color: #ffffff;
  }
  h1 {
    color: #00ED64;  /* Verde MongoDB */
  }
---
```

## 📝 Editar Presentaciones

### Estructura de un Archivo Marp

```markdown
---
marp: true
theme: default
paginate: true
---

# Título de Diapositiva 1

Contenido...

---

# Título de Diapositiva 2

Más contenido...

---
```

### Directivas Especiales

#### Diapositiva de Título (sin paginación, centrada)
```markdown
<!-- _class: lead -->
<!-- _paginate: false -->

# Título Principal
## Subtítulo
```

#### Bloques de Código con Syntax Highlighting
```markdown
\`\`\`java
public class Example {
    public static void main(String[] args) {
        System.out.println("Hello!");
    }
}
\`\`\`
```

Lenguajes soportados: `java`, `javascript`, `python`, `sql`, `json`, `bash`, etc.

#### Columnas (dos columnas)
```markdown
<div class="columns">
<div>

Columna izquierda

</div>
<div>

Columna derecha

</div>
</div>
```

## 🖥️ Vista Previa en Vivo

### Con VS Code
1. Instalar extensión **Marp for VS Code**
2. Abrir cualquier archivo `.md`
3. Click en el icono de Marp en la esquina superior derecha
4. Vista previa en tiempo real

### Con Navegador
```bash
# Servidor con recarga automática
marp --server --watch 01_NoSQL_vs_SQL.md
```

## 📦 Exportar Todo

```bash
# Script para convertir todas las presentaciones a PowerPoint
./convertir.sh pptx

# O manualmente:
for file in *.md; do
    marp "$file" --pptx -o "output/$(basename "$file" .md).pptx"
done
```

## 🔧 Solución de Problemas

### Error: "marp: command not found"

**Causa:** Marp CLI no está instalado o no está en el PATH.

**Solución:**
```bash
# Verificar instalación de npm
npm --version

# Instalar Marp CLI
npm install -g @marp-team/marp-cli

# O usar npx
npx @marp-team/marp-cli --version
```

### Error al generar PDF: "Chromium not found"

**Causa:** Marp necesita Chromium para generar PDFs.

**Solución en Linux:**
```bash
# Ubuntu/Debian
sudo apt-get install chromium-browser

# O instalar las dependencias de Puppeteer
npx @puppeteer/browsers install chrome
```

### Las imágenes no aparecen en el PPTX

**Causa:** Marp no permite archivos locales por seguridad.

**Solución:**
```bash
# Añadir flag --allow-local-files
marp archivo.md --pptx --allow-local-files -o output/archivo.pptx
```

### Fuentes no se ven bien en PowerPoint

**Solución:** Embeber fuentes en el frontmatter:
```yaml
---
marp: true
style: |
  @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');
  section {
    font-family: 'Roboto', sans-serif;
  }
---
```

## 📚 Recursos Adicionales

- [Documentación Oficial de Marp](https://marpit.marp.app/)
- [Marp CLI GitHub](https://github.com/marp-team/marp-cli)
- [Guía de Markdown de Marp](https://marpit.marp.app/markdown)
- [Temas y Estilos](https://github.com/marp-team/marp-core/tree/main/themes)

## 🎯 Flujo de Trabajo Recomendado

1. **Editar** la presentación `.md` en VS Code con vista previa
2. **Probar** con el servidor local: `marp --server archivo.md`
3. **Convertir** a PowerPoint: `./convertir.sh pptx archivo.md`
4. **Revisar** el `.pptx` en PowerPoint
5. **Ajustar** estilos si es necesario
6. **Distribuir** el archivo final

## ⚡ Atajos Rápidos

```bash
# Convertir y abrir automáticamente
marp 01_NoSQL_vs_SQL.md --pptx -o temp.pptx && xdg-open temp.pptx

# Convertir todas a PDF
for f in *.md; do marp "$f" --pdf -o "output/${f%.md}.pdf"; done

# Vista previa rápida en HTML
marp 01_NoSQL_vs_SQL.md --html -o preview.html && xdg-open preview.html
```

## 📋 Checklist de Presentación

Antes de presentar:
- [ ] Convertir a PPTX: `./convertir.sh pptx`
- [ ] Revisar que todas las diapositivas se vean correctamente
- [ ] Verificar que el código tenga syntax highlighting
- [ ] Comprobar que las tablas estén bien formateadas
- [ ] Probar el modo presentación en PowerPoint
- [ ] Tener el archivo `.md` disponible por si hay que hacer cambios rápidos

---

**Nota:** Estos archivos Marp están diseñados para ser editados y personalizados según las necesidades del curso. No dudes en modificarlos.
