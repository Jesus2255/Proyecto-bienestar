@echo off
title Proyecto Bienestar - Inicio Completo
color 0E

echo ========================================
echo   PROYECTO BIENESTAR - INICIO COMPLETO
echo ========================================
echo.
echo Este script iniciará:
echo   1. Backend Spring Boot (puerto 8080)
echo   2. Frontend React (puerto 5173)
echo.
echo Ambos se ejecutarán en ventanas separadas.
echo.
pause

echo.
echo [1/2] Iniciando Backend Spring Boot...
start "Backend - Proyecto Bienestar" cmd /k "cd /d "%~dp0Bienestar" && start-backend.bat"

echo.
echo [INFO] Esperando 5 segundos para que el backend inicie...
timeout /t 5 /nobreak >nul

echo.
echo [2/2] Iniciando Frontend React...
start "Frontend - Proyecto Bienestar" cmd /k "cd /d "%~dp0WEB\Proyecto-bienestar-intefaz" && start-web.bat"

echo.
echo ========================================
echo   SERVIDORES INICIADOS
echo ========================================
echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:5173
echo Swagger:  http://localhost:8080/swagger-ui.html
echo.
echo Las ventanas de los servidores se abrieron por separado.
echo Puedes cerrar esta ventana.
echo.
pause
