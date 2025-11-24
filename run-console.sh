#!/bin/bash
# Script para executar aplicação de console (Unix/Linux/macOS)

echo "🖥️  Executando Sistema de Carteiras - Console"
echo "================================================"

# Verificar se Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ Java não encontrado. Instale Java 17+ primeiro."
    echo "💡 Download: https://adoptium.net/"
    exit 1
fi

echo "✅ Java encontrado: $(java -version 2>&1 | head -n 1)"

# Verificar se Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven não encontrado."
    echo "💡 Instale Maven primeiro:"
    echo "   - macOS: brew install maven"
    echo "   - Linux: sudo apt install maven"
    echo "   - Windows: https://maven.apache.org/download.cgi"
    echo "   - Ou use IDE (IntelliJ IDEA, Eclipse)"
    exit 1
fi

echo "✅ Maven encontrado: $(mvn -version | head -n 1)"

# Compilar projeto
echo "📦 Compilando projeto..."
mvn clean compile
if [ $? -ne 0 ]; then
    echo "❌ Erro na compilação"
    exit 1
fi

# Executar aplicação de console
echo "🖥️  Iniciando aplicação de console..."
echo "📊 Banco: H2 (em memória)"
echo ""
echo "⚠️  A aplicação será executada no console"
echo ""

mvn spring-boot:run -Dspring-boot.run.profiles=h2
