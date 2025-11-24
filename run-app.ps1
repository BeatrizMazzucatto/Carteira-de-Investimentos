# Script PowerShell para executar a aplicação (Windows/Linux/macOS)

Write-Host "🚀 Executando Sistema de Carteiras" -ForegroundColor Green
Write-Host "==================================" -ForegroundColor Green

# Verificar se Java está instalado
try {
    $javaVersion = java -version 2>&1 | Select-Object -First 1
    Write-Host "✅ Java encontrado: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Java não encontrado. Instale Java 17+ primeiro." -ForegroundColor Red
    Write-Host "💡 Download: https://adoptium.net/" -ForegroundColor Yellow
    exit 1
}

# Verificar se Maven está instalado
try {
    $mavenVersion = mvn -version | Select-Object -First 1
    Write-Host "✅ Maven encontrado: $mavenVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven não encontrado." -ForegroundColor Red
    Write-Host "💡 Instale Maven primeiro:" -ForegroundColor Yellow
    Write-Host "   - Windows: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
    Write-Host "   - macOS: brew install maven" -ForegroundColor Yellow
    Write-Host "   - Linux: sudo apt install maven" -ForegroundColor Yellow
    Write-Host "   - Ou use IDE (IntelliJ IDEA, Eclipse)" -ForegroundColor Yellow
    exit 1
}

# Compilar projeto
Write-Host "📦 Compilando projeto..." -ForegroundColor Blue
mvn clean compile
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Erro na compilação" -ForegroundColor Red
    exit 1
}

# Executar aplicação
Write-Host "🚀 Iniciando aplicação..." -ForegroundColor Blue
Write-Host "📊 Banco: H2 (em memória)" -ForegroundColor Cyan
Write-Host "🌐 URL: http://localhost:8080" -ForegroundColor Cyan
Write-Host "📋 Console H2: http://localhost:8080/h2-console" -ForegroundColor Cyan
Write-Host ""
Write-Host "⚠️  Pressione Ctrl+C para parar a aplicação" -ForegroundColor Yellow
Write-Host ""

mvn spring-boot:run -Dspring-boot.run.profiles=h2
