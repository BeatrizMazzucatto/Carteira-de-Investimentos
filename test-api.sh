#!/bin/bash
# Script de teste da API do Sistema de Carteiras

BASE_URL="http://localhost:8080/api"
echo "🧪 Testando API do Sistema de Carteiras"
echo "======================================"

# Verificar se a aplicação está rodando
echo "🔍 Verificando se a aplicação está rodando..."
if ! curl -s "$BASE_URL/investidores" > /dev/null 2>&1; then
    echo "❌ Aplicação não está rodando em $BASE_URL"
    echo "💡 Execute: mvn spring-boot:run"
    exit 1
fi

echo "✅ Aplicação está rodando!"

# 1. Criar Investidor
echo ""
echo "📝 1. Criando investidor..."
INVESTIDOR_RESPONSE=$(curl -s -X POST "$BASE_URL/investidores" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@email.com"
  }')

echo "Investidor criado: $INVESTIDOR_RESPONSE"

# Extrair ID do investidor (assumindo formato JSON simples)
INVESTIDOR_ID=$(echo $INVESTIDOR_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
if [ -z "$INVESTIDOR_ID" ]; then
    INVESTIDOR_ID="1"  # Fallback para teste
fi
echo "ID do Investidor: $INVESTIDOR_ID"

# 2. Criar Carteira
echo ""
echo "📝 2. Criando carteira..."
CARTEIRA_RESPONSE=$(curl -s -X POST "$BASE_URL/carteiras/investidor/$INVESTIDOR_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Carteira Aposentadoria",
    "descricao": "Carteira para aposentadoria",
    "objetivo": "APOSENTADORIA",
    "perfilRisco": "MODERADO",
    "valorInicial": 10000.00
  }')

echo "Carteira criada: $CARTEIRA_RESPONSE"

# Extrair ID da carteira
CARTEIRA_ID=$(echo $CARTEIRA_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
if [ -z "$CARTEIRA_ID" ]; then
    CARTEIRA_ID="1"  # Fallback para teste
fi
echo "ID da Carteira: $CARTEIRA_ID"

# 3. Registrar Compra
echo ""
echo "📝 3. Registrando compra de PETR4..."
COMPRA_RESPONSE=$(curl -s -X POST "$BASE_URL/transacoes/carteira/$CARTEIRA_ID/compra" \
  -H "Content-Type: application/json" \
  -d '{
    "codigoAtivo": "PETR4",
    "nomeAtivo": "Petrobras",
    "tipoAtivo": "ACAO",
    "quantidade": 100,
    "precoUnitario": 25.50,
    "taxasCorretagem": 5.00,
    "observacoes": "Compra inicial"
  }')

echo "Compra registrada: $COMPRA_RESPONSE"

# 4. Registrar Venda
echo ""
echo "📝 4. Registrando venda parcial..."
VENDA_RESPONSE=$(curl -s -X POST "$BASE_URL/transacoes/carteira/$CARTEIRA_ID/venda" \
  -H "Content-Type: application/json" \
  -d '{
    "codigoAtivo": "PETR4",
    "nomeAtivo": "Petrobras",
    "tipoAtivo": "ACAO",
    "quantidade": 50,
    "precoUnitario": 30.00,
    "taxasCorretagem": 3.00,
    "observacoes": "Venda parcial"
  }')

echo "Venda registrada: $VENDA_RESPONSE"

# 5. Registrar Provento
echo ""
echo "📝 5. Registrando provento..."
PROVENTO_RESPONSE=$(curl -s -X POST "$BASE_URL/transacoes/carteira/$CARTEIRA_ID/provento" \
  -H "Content-Type: application/json" \
  -d '{
    "codigoAtivo": "PETR4",
    "nomeAtivo": "Petrobras",
    "tipoAtivo": "ACAO",
    "quantidade": 50,
    "precoUnitario": 0.50,
    "observacoes": "Dividendo trimestral"
  }')

echo "Provento registrado: $PROVENTO_RESPONSE"

# 6. Consultar Rentabilidade
echo ""
echo "📊 6. Consultando rentabilidade..."
RENTABILIDADE_RESPONSE=$(curl -s -X GET "$BASE_URL/rentabilidade/carteira/$CARTEIRA_ID")

echo "Rentabilidade: $RENTABILIDADE_RESPONSE"

# 7. Listar Transações
echo ""
echo "📋 7. Listando transações..."
TRANSACOES_RESPONSE=$(curl -s -X GET "$BASE_URL/transacoes/carteira/$CARTEIRA_ID")

echo "Transações: $TRANSACOES_RESPONSE"

# 8. Estatísticas da Carteira
echo ""
echo "📈 8. Estatísticas da carteira..."
STATS_RESPONSE=$(curl -s -X GET "$BASE_URL/transacoes/carteira/$CARTEIRA_ID/estatisticas")

echo "Estatísticas: $STATS_RESPONSE"

echo ""
echo "✅ Testes concluídos!"
echo ""
echo "💡 Para testar manualmente:"
echo "   - Acesse: http://localhost:8080/api/investidores"
echo "   - Use Postman com a coleção fornecida no TESTING_GUIDE.md"
echo "   - Execute testes unitários: mvn test"
