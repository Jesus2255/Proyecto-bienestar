@echo off
echo ========================================
echo Probando endpoint /login desde CMD
echo ========================================
echo.

echo Test 1: Login exitoso (admin/1234)
curl -X POST http://localhost:8080/login ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "username=admin&password=1234" ^
  -v
echo.
echo.

echo Test 2: Login fallido (credenciales incorrectas)
curl -X POST http://localhost:8080/login ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "username=admin&password=wrong" ^
  -v
echo.
echo.

pause
