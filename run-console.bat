@echo off
REM Script para executar aplicação de console no Windows
echo 🖥️  Executando Sistema de Carteiras - Console
echo ================================================

REM Verificar se Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java não encontrado. Instale Java 17+ primeiro.
    echo 💡 Download: https://adoptium.net/
    pause
    exit /b 1
)

echo ✅ Java encontrado
java -version

REM Verificar se Maven está instalado
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven não encontrado.
    echo 💡 Instale Maven primeiro:
    echo    1. Download: https://maven.apache.org/download.cgi
    echo    2. Extrair e adicionar ao PATH
    echo    3. Ou usar IDE (IntelliJ IDEA, Eclipse)
    pause
    exit /b 1
)

echo ✅ Maven encontrado
mvn -version

REM Compilar projeto
echo 📦 Compilando projeto...
mvn clean compile
if %errorlevel% neq 0 (
    echo ❌ Erro na compilação
    pause
    exit /b 1
)

REM Executar aplicação de console
echo 🖥️  Iniciando aplicação de console...
echo 📊 Banco: H2 (em memória)
echo.
echo ⚠️  A aplicação será executada no console
echo.

mvn spring-boot:run -Dspring-boot.run.profiles=h2

pause
