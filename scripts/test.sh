#!/usr/bin/env bash

set -euo pipefail

# Localiza a raiz do repositorio.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$ROOT"

echo "========================================"
echo "       Testes do Assembler Hack"
echo "========================================"

echo
echo "[1/3] Compilando codigo-fonte..."

"$ROOT/scripts/build.sh"

echo "[2/3] Compilando testes..."

shopt -s globstar nullglob

TEST_SOURCES=(tests/**/*.java)

if [ "${#TEST_SOURCES[@]}" -eq 0 ]; then
    echo "Erro: nenhum teste Java encontrado." >&2
    exit 1
fi

rm -rf -- "$ROOT/build/test-classes"
mkdir -p "$ROOT/build/test-classes"

javac --release 17 \
    -encoding UTF-8 \
    -cp "$ROOT/build/classes" \
    -d "$ROOT/build/test-classes" \
    "${TEST_SOURCES[@]}"

CLASSPATH="$ROOT/build/classes:$ROOT/build/test-classes"

echo "[3/3] Executando testes..."
echo

TEST_CLASSES=(
    parser.ParserTest
    code.CodeTest
    integration.MainTest
)

for test_class in "${TEST_CLASSES[@]}"; do
    echo "Executando $test_class..."
    java -cp "$CLASSPATH" "$test_class"
    echo
done

echo "========================================"
echo "Todos os testes passaram com sucesso."
echo "========================================"
