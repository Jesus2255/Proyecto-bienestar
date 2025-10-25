@echo off
title Compilar Backend - Proyecto Bienestar
color 0D

echo ========================================
echo   COMPILAR BACKEND - PROYECTO BIENESTAR
echo ========================================
echo.

cd /d "%~dp0Bienestar"

REM Verificar si Maven está instalado
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven no está instalado o no está en el PATH.
    echo.
    echo Descarga Maven desde: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)

echo [OK] Maven detectado: 
call mvn --version
echo.

echo ========================================
echo   COMPILANDO PROYECTO
echo ========================================
echo.

call mvn -DskipTests clean package

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   COMPILACIÓN EXITOSA
    echo ========================================
    echo.
    echo El archivo JAR se generó en:
    echo %~dp0Bienestar\target\Bienestar-0.0.1-SNAPSHOT.jar
    echo.
    echo Ahora puedes ejecutar:
    echo - start-backend.bat (solo backend)
    echo - start-all.bat (backend + frontend)
    echo.
) else (
    echo.
    echo ========================================
    echo   ERROR EN LA COMPILACIÓN
    echo ========================================
    echo.
    echo Revisa los errores arriba.
    echo.
)

pause
