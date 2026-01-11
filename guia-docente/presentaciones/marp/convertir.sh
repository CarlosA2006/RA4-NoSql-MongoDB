#!/bin/bash

# Script para convertir presentaciones Marp a diferentes formatos
# Uso: ./convertir.sh [formato] [archivo]
#   formato: pptx, pdf, html (por defecto: pptx)
#   archivo: nombre del archivo .md (sin ruta, opcional)

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuración
FORMATO="${1:-pptx}"
ARCHIVO_ESPECIFICO="$2"
DIR_MARP="$(cd "$(dirname "$0")" && pwd)"
DIR_OUTPUT="$DIR_MARP/output"

# Crear directorio de salida si no existe
mkdir -p "$DIR_OUTPUT"

echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║     Conversor de Presentaciones Marp                  ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Verificar que Marp CLI esté instalado
if ! command -v marp &> /dev/null; then
    echo -e "${YELLOW}⚠️  Marp CLI no está instalado.${NC}"
    echo ""
    echo "Para instalar Marp CLI ejecuta:"
    echo -e "${GREEN}npm install -g @marp-team/marp-cli${NC}"
    echo ""
    echo "O usa npx (sin instalar):"
    echo -e "${GREEN}npx @marp-team/marp-cli ...${NC}"
    echo ""
    exit 1
fi

# Función para convertir un archivo
convertir_archivo() {
    local archivo=$1
    local nombre_base=$(basename "$archivo" .md)
    local salida="$DIR_OUTPUT/${nombre_base}.${FORMATO}"

    echo -e "${BLUE}📄 Convirtiendo:${NC} $nombre_base.md → $nombre_base.$FORMATO"

    case $FORMATO in
        pptx)
            marp "$archivo" --pptx --allow-local-files -o "$salida"
            ;;
        pdf)
            marp "$archivo" --pdf --allow-local-files -o "$salida"
            ;;
        html)
            marp "$archivo" --html --allow-local-files -o "$salida"
            ;;
        *)
            echo -e "${RED}❌ Formato no soportado: $FORMATO${NC}"
            echo "Formatos válidos: pptx, pdf, html"
            return 1
            ;;
    esac

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Generado:${NC} $salida"
    else
        echo -e "${RED}✗ Error al convertir $archivo${NC}"
        return 1
    fi
}

# Procesar archivos
if [ -n "$ARCHIVO_ESPECIFICO" ]; then
    # Convertir solo el archivo especificado
    if [ -f "$DIR_MARP/$ARCHIVO_ESPECIFICO" ]; then
        convertir_archivo "$DIR_MARP/$ARCHIVO_ESPECIFICO"
    else
        echo -e "${RED}❌ Archivo no encontrado: $ARCHIVO_ESPECIFICO${NC}"
        exit 1
    fi
else
    # Convertir todos los archivos .md en el directorio
    echo "Buscando presentaciones en: $DIR_MARP"
    echo ""

    contador=0
    for archivo in "$DIR_MARP"/*.md; do
        if [ -f "$archivo" ]; then
            convertir_archivo "$archivo"
            ((contador++))
            echo ""
        fi
    done

    if [ $contador -eq 0 ]; then
        echo -e "${YELLOW}⚠️  No se encontraron archivos .md en $DIR_MARP${NC}"
        exit 1
    fi

    echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}✓ Conversión completada: $contador archivo(s)${NC}"
    echo -e "${GREEN}═══════════════════════════════════════════════════${NC}"
fi

echo ""
echo -e "📂 Archivos generados en: ${BLUE}$DIR_OUTPUT${NC}"
echo ""
