#!/usr/bin/env bash

set -euo pipefail

# Localiza a raiz do repositorio.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Valida os argumentos.
if [ "$#" -lt 1 ] || [ "$#" -gt 2 ]; then
    echo "Uso: $0 <pasta-nand2tetris> [pasta-project-06]" >&2
    exit 1
fi

N2T="$(realpath "$1")"
PROJECT="$(realpath "${2:-$N2T/projects/06}")"

OFFICIAL="$N2T/tools/Assembler.sh"

# Verifica a existencia dos materiais.
if [ ! -f "$OFFICIAL" ]; then
    echo "Erro: Assembler oficial nao encontrado." >&2
    exit 1
fi

if [ ! -d "$PROJECT" ]; then
    echo "Erro: Project 06 nao encontrado." >&2
    exit 1
fi

# Diretorio dos resultados.
OUTPUT="$ROOT/build/etapa1"

mkdir -p "$OUTPUT/our"
mkdir -p "$OUTPUT/reference"

PROGRAMS=(
    "add/Add.asm"
    "max/MaxL.asm"
    "rect/RectL.asm"
    "pong/PongL.asm"
)

PASSED=0
FAILED=0

echo "========================================"
echo "     Validacao da Etapa 1 - Assembler"
echo "========================================"
echo

echo "Compilando o nosso montador..."

"$ROOT/scripts/build.sh"

echo

for program in "${PROGRAMS[@]}"; do

    echo "----------------------------------------"
    echo "Programa: $program"
    echo "----------------------------------------"

    SOURCE="$PROJECT/$program"

    if [ ! -f "$SOURCE" ]; then
        echo "[FALHOU] Arquivo nao encontrado: $SOURCE"
        FAILED=$((FAILED + 1))
        continue
    fi

    NAME="$(basename "$program" .asm)"

    OUR_ASM="$OUTPUT/our/$NAME.asm"
    REF_ASM="$OUTPUT/reference/$NAME.asm"

    OUR_HACK="$OUTPUT/our/$NAME.hack"
    REF_HACK="$OUTPUT/reference/$NAME.hack"

    # Remove resultados anteriores.
    rm -f -- "$OUR_HACK" "$REF_HACK"

    # Cria duas copias independentes do programa.
    cp "$SOURCE" "$OUR_ASM"
    cp "$SOURCE" "$REF_ASM"

    echo "Traduzindo com nosso montador..."

    if ! java -cp "$ROOT/build/classes" \
        main.Main "$OUR_ASM"; then

        echo "[FALHOU] Erro no nosso montador."
        FAILED=$((FAILED + 1))
        continue
    fi

    echo "Traduzindo com o Assembler oficial..."

    if ! bash "$OFFICIAL" "$REF_ASM"; then

        echo "[FALHOU] Erro no Assembler oficial."
        FAILED=$((FAILED + 1))
        continue
    fi

    # Verifica se ambos os arquivos foram gerados.
    if [ ! -s "$OUR_HACK" ] || [ ! -s "$REF_HACK" ]; then
        echo "[FALHOU] Arquivo de saida ausente ou vazio."
        FAILED=$((FAILED + 1))
        continue
    fi

    # Verifica o formato das instrucoes binarias.
    if grep -qvE '^[01]{16}$' "$OUR_HACK"; then
        echo "[FALHOU] Instrucao binaria invalida."
        FAILED=$((FAILED + 1))
        continue
    fi

    echo "Comparando os arquivos binarios..."

    if diff -q "$OUR_HACK" "$REF_HACK"; then

        LINES="$(wc -l < "$OUR_HACK")"

        echo "[PASSOU] Arquivos identicos."
        echo "Instrucoes geradas: $LINES"

        PASSED=$((PASSED + 1))

    else

        echo "[FALHOU] Os arquivos sao diferentes."

        FAILED=$((FAILED + 1))
    fi

    echo

done

echo "========================================"
echo "            Resultado final"
echo "========================================"
echo "Programas testados: ${#PROGRAMS[@]}"
echo "Aprovados: $PASSED"
echo "Reprovados: $FAILED"
echo "Resultados em: $OUTPUT"
echo "========================================"

if [ "$FAILED" -ne 0 ]; then
    exit 1
fi

echo "Etapa 1 validada com sucesso."
