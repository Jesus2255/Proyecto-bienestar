@echo off
echo ========================================
echo Probando endpoints /login y /logout
echo ========================================
echo.

echo Test 1: Login exitoso (admin/1234)
curl -X POST http://localhost:8080/login ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "username=admin&password=1234" ^
  -c cookies.txt ^
  -v
echo.
echo.

echo Test 2: Logout (usando la cookie de sesion)
curl -X POST http://localhost:8080/logout ^
  -b cookies.txt ^
  -v
echo.
echo.

echo Test 3: Login fallido (credenciales incorrectas)
curl -X POST http://localhost:8080/login ^
  -H "Content-Type: application/x-www-form-urlencoded" ^
  -d "username=admin&password=wrong" ^
  -v
echo.
echo.

pause
