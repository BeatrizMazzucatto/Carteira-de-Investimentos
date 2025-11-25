@echo off
REM Script de teste da API para Windows
set BASE_URL=http://localhost:8080/api
echo 🧪 Testando API do Sistema de Carteiras
echo ======================================

REM Verificar se a aplicação está rodando
echo 🔍 Verificando se a aplicação está rodando...
curl -s "%BASE_URL%/investidores" >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Aplicação não está rodando em %BASE_URL%
    echo 💡 Execute: run-app.bat
    pause
    exit /b 1
)

echo ✅ Aplicação está rodando!

REM 1. Criar Investidor
echo.
echo 📝 1. Criando investidor...
curl -s -X POST "%BASE_URL%/investidores" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\": \"João Silva\", \"email\": \"joao@email.com\"}"

echo.
echo ✅ Investidor criado!

REM 2. Criar Carteira (assumindo investidor ID = 1)
echo.
echo 📝 2. Criando carteira...
curl -s -X POST "%BASE_URL%/carteiras/investidor/1" ^
  -H "Content-Type: application/json" ^
  -d "{\"nome\": \"Carteira Aposentadoria\", \"descricao\": \"Carteira para aposentadoria\", \"objetivo\": \"APOSENTADORIA\", \"perfilRisco\": \"MODERADO\", \"valorInicial\": 10000.00}"

echo.
echo ✅ Carteira criada!

REM 3. Registrar Compra (assumindo carteira ID = 1)
echo.
echo 📝 3. Registrando compra de PETR4...
curl -s -X POST "%BASE_URL%/transacoes/carteira/1/compra" ^
  -H "Content-Type: application/json" ^
  -d "{\"codigoAtivo\": \"PETR4\", \"nomeAtivo\": \"Petrobras\", \"tipoAtivo\": \"ACAO\", \"quantidade\": 100, \"precoUnitario\": 25.50, \"taxasCorretagem\": 5.00, \"observacoes\": \"Compra inicial\"}"

echo.
echo ✅ Compra registrada!

REM 4. Consultar Rentabilidade
echo.
echo 📊 4. Consultando rentabilidade...
curl -s -X GET "%BASE_URL%/rentabilidade/carteira/1"

echo.
echo ✅ Rentabilidade calculada!

REM 5. Listar Transações
echo.
echo 📋 5. Listando transações...
curl -s -X GET "%BASE_URL%/transacoes/carteira/1"

echo.
echo ✅ Transações listadas!

echo.
echo 🎉 Testes concluídos com sucesso!
echo.
echo 💡 Para testar manualmente:
echo    - Acesse: http://localhost:8080/api/investidores
echo    - Console H2: http://localhost:8080/h2-console
echo.

pause
