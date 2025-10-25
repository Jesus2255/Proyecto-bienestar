@echo off
title Frontend React - Proyecto Bienestar
color 0A

echo ========================================
echo   FRONTEND REACT - PROYECTO BIENESTAR
echo ========================================
echo.

REM Verificar si Node.js está instalado
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Node.js no está instalado.
    echo.
    echo Por favor instala Node.js desde: https://nodejs.org/
    echo.
    pause
    exit /b 1
)

echo [OK] Node.js detectado: 
node --version
npm --version
echo.

REM Verificar si node_modules existe
if not exist "node_modules\" (
    echo [INFO] Instalando dependencias por primera vez...
    echo.
    call npm install
    if %ERRORLEVEL% NEQ 0 (
        echo.
        echo [ERROR] Falló la instalación de dependencias.
        pause
        exit /b 1
    )
    echo.
    echo [OK] Dependencias instaladas correctamente.
    echo.
)

echo ========================================
echo   INICIANDO SERVIDOR DE DESARROLLO
echo ========================================
echo.
echo El frontend estará disponible en:
echo http://localhost:5173
echo.
echo Presiona Ctrl+C para detener el servidor
echo.

REM Iniciar el servidor de desarrollo
npm run dev

pause
