@echo off
REM Script para executar todos os testes do projeto (Windows)
REM Uso: run-tests.bat [tipo]
REM Tipos: unit, integration, functional, all (padrão)

setlocal enabledelayedexpansion

set TYPE=%1
if "%TYPE%"=="" set TYPE=all

echo =══════════════════════════════════════════════════════════════
echo   Executando Testes - Investment Portfolio Manager
echo =══════════════════════════════════════════════════════════════
echo.

if "%TYPE%"=="unit" (
    echo ▶ Executando testes unitários...
    call mvn test -Dtest="*Test" -DfailIfNoTests=false
) else if "%TYPE%"=="integration" (
    echo ▶ Executando testes de integração...
    call mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false
) else if "%TYPE%"=="functional" (
    echo ▶ Executando testes funcionais...
    call mvn test -Dtest="*Functional*" -DfailIfNoTests=false
) else if "%TYPE%"=="all" (
    echo ▶ Executando todos os testes...
    call mvn clean test
) else (
    echo ❌ Tipo de teste inválido: %TYPE%
    echo Uso: run-tests.bat [unit^|integration^|functional^|all]
    exit /b 1
)

echo.
echo =══════════════════════════════════════════════════════════════
echo   Testes concluídos!
echo =══════════════════════════════════════════════════════════════

