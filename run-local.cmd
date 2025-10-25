@echo off
REM Helper to run the app locally using the ignored config/application-local.properties
cd "%~dp0\Bienestar"
REM The --spring.config.location tells Spring Boot to load the local properties in addition to defaults.
java -Dspring.config.additional-location=classpath:/,config/application-local.properties -jar target\Bienestar-0.0.1-SNAPSHOT.jar
pause
