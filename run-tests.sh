#!/bin/bash

# Script para executar todos os testes do projeto
# Uso: ./run-tests.sh [tipo]
# Tipos: unit, integration, functional, all (padrão)

set -e

TYPE=${1:-all}
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

echo "═══════════════════════════════════════════════════════════════"
echo "  Executando Testes - Investment Portfolio Manager"
echo "═══════════════════════════════════════════════════════════════"
echo ""

case $TYPE in
    unit)
        echo "▶ Executando testes unitários..."
        mvn test -Dtest="*Test" -DfailIfNoTests=false
        ;;
    integration)
        echo "▶ Executando testes de integração..."
        mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false
        ;;
    functional)
        echo "▶ Executando testes funcionais..."
        mvn test -Dtest="*Functional*" -DfailIfNoTests=false
        ;;
    all)
        echo "▶ Executando todos os testes..."
        mvn clean test
        ;;
    *)
        echo "❌ Tipo de teste inválido: $TYPE"
        echo "Uso: ./run-tests.sh [unit|integration|functional|all]"
        exit 1
        ;;
esac

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "  Testes concluídos!"
echo "═══════════════════════════════════════════════════════════════"

