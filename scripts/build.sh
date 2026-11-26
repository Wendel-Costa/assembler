#!/usr/bin/env bash

set -euo pipefail

# Localiza a raiz do repositorio.
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$ROOT"

# Verifica se o compilador Java esta disponivel.
if ! command -v javac >/dev/null 2>&1; then
    echo "Erro: javac nao encontrado. Instale o JDK 17 ou superior." >&2
    exit 1
fi

# Permite localizar arquivos Java recursivamente.
shopt -s globstar nullglob

SOURCES=(src/**/*.java)

if [ "${#SOURCES[@]}" -eq 0 ]; then
    echo "Erro: nenhum arquivo Java encontrado em src/." >&2
    exit 1
fi

# Remove somente as classes compiladas anteriormente.
rm -rf -- "$ROOT/build/classes"
mkdir -p "$ROOT/build/classes"

# Compila o codigo-fonte com compatibilidade Java 17.
javac --release 17 \
    -encoding UTF-8 \
    -d "$ROOT/build/classes" \
    "${SOURCES[@]}"
