# Proyecto Bienestar

Resumen rápido

Este README explica cómo compilar, ejecutar y verificar el endpoint OpenAPI (/v3/api-docs) después de corregir una incompatibilidad de dependencias (springdoc vs Spring Framework) que provocaba un `NoSuchMethodError` relacionado con `org.springframework.web.method.ControllerAdviceBean`.

Contexto del problema

- Síntoma: Al abrir la UI de Swagger o solicitar `/v3/api-docs` la aplicación devolvía 500 y en los logs aparecía:
  `java.lang.NoSuchMethodError: 'void org.springframework.web.method.ControllerAdviceBean.<init>(java.lang.Object)'`
- Causa: incompatibilidad en tiempo de ejecución entre la versión de Spring Framework empaquetada (`spring-web` 6.2.11) y una versión de `springdoc` que referenciaba un constructor antiguo de `ControllerAdviceBean`.
- Solución aplicada: se actualizó `springdoc-openapi-starter-webmvc-ui` a la versión `2.7.0` y se reempaquetó el `fat-jar`. Con esa versión ya no se produce el NoSuchMethodError y `/v3/api-docs` responde correctamente.

Qué cambié en el repositorio

- `pom.xml`: actualizada la dependencia a

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.7.0</version>
</dependency>
```

- Se añadieron mejoras de diagnóstico temporal en el código (beans que imprimen dónde se cargan clases clave y un `ControllerAdvice` que registra stacktraces completos). Estos archivos son útiles en desarrollo para detectar conflictos de classpath.

Requisitos

- Java 17 (el proyecto está configurado con `<java.version>17</java.version>` en el `pom.xml`).
- Maven 3.x

Cómo compilar (Windows, cmd.exe)

Abre un cmd en la raíz del proyecto (donde está la carpeta `Bienestar`) y ejecuta:

```cmd
cd "C:\Users\estiv\Documents\Visual Studio 2022\java\Proyecto-bienestar\Bienestar"
mvn -DskipTests clean package
```

Si aparece un error al repackage como "Unable to rename ..." o similar, asegúrate de que no haya procesos `java` ejecutando el JAR anterior y mátalos:

```cmd
wmic process where "CommandLine like '%Bienestar-0.0.1-SNAPSHOT.jar%'" get ProcessId,CommandLine /format:list
taskkill /PID <PID> /F
```

Cómo ejecutar

```cmd
java -jar target\Bienestar-0.0.1-SNAPSHOT.jar
```

Alternativamente (desde Maven):

```cmd
mvn spring-boot:run
```

Probar OpenAPI / Swagger

- OpenAPI JSON:

```cmd
curl http://localhost:8080/v3/api-docs
```

- Swagger UI: abrir en el navegador

```
http://localhost:8080/swagger-ui/index.html
```

Comprobaciones útiles (diagnóstico)

- Ver dependencias Spring y springdoc resueltas:

```cmd
mvn -f Bienestar\pom.xml dependency:tree -Dincludes=org.springframework,org.springdoc
```

- Ver qué jars están empaquetados en el fat-jar (útil para verificar versiones en runtime):

```cmd
jar tf target\Bienestar-0.0.1-SNAPSHOT.jar | findstr /I "springdoc-openapi spring-web spring-webmvc"
```

- Desensamblar una clase concreta para comprobar referencias (avanzado):

```cmd
javap -classpath "target\BOOT-INF\lib\springdoc-openapi-starter-common-2.7.0.jar" -v org.springdoc.core.service.GenericResponseService
```

Buenas prácticas y recomendaciones

- Fijar versiones críticas en `dependencyManagement` para evitar regresiones cuando dependencias transitivas introduzcan versiones no deseadas.
- Antes de reempaquetar el `fat-jar`, parar cualquier proceso que esté ejecutando el jar para que el plugin de Spring Boot pueda renombrar/reemplazar ficheros.
- Cuando aparezca un `NoSuchMethodError` o `NoClassDefFoundError`, usar estos pasos:
  1. Identificar el nombre de la clase/método que falta en el stacktrace.
  2. Localizar qué JAR en `BOOT-INF/lib` contiene esa clase y qué versión es.
  3. Ejecutar `mvn dependency:tree` para ver si hay versiones conflictivas presentes.
  4. Alinear versiones (upgrade/downgrade) o excluir la dependencia transitiva problemática.

Ejemplo: fijar versiones en `dependencyManagement` (añadir dentro de `<project>` -> `<dependencyManagement>`)

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springdoc</groupId>
      <artifactId>springdoc-openapi-starter-webmvc-api</artifactId>
      <version>2.7.0</version>
    </dependency>
  </dependencies>
</dependencyManagement>
```

Notas finales

- Tras la actualización a `springdoc` 2.7.0 en esta rama, el endpoint `/v3/api-docs` responde 200 y la UI funciona.
- Si vuelves a desplegar en otro entorno (CI, servidor) asegúrate de limpiar la caché de dependencias y de no ejecutar instancias antiguas que puedan provocar confusión.

Si quieres, puedo:

- Añadir el bloque `dependencyManagement` al `pom.xml` y crear un pequeño `CONTRIBUTING.md` con las instrucciones de build/run.
- Abrir un PR con los cambios que hicimos.

Dime qué prefieres y lo implemento.

---

Actualizaciones recientes (rama fix/springdoc-version)

- Se corrigió la incompatibilidad con `springdoc` fijando `springdoc-openapi-starter-webmvc-ui` a la versión `2.7.0`.
- Se añadió un migrador alternativo in-app (`SimpleMigrationRunner`) para entornos donde Flyway no es compatible con la versión de PostgreSQL (ej. Supabase v17). El migrador lee `classpath:db/migration/V*.sql`, elimina comentarios y aplica las sentencias SQL, registrando las versiones en `flyway_schema_history`.
- Se incluyó un script de verificación (`scripts/do_login_post.ps1`) que realiza: GET `/login` (extrae CSRF), POST `/login` y POST `/api/servicios` para comprobar persistencia.

Cómo arrancar la aplicación contra Supabase (resumen rápido)

1) Crear un fichero `.env` en `Bienestar/` con las variables (o exportarlas en el entorno):

```
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=<tu_password>
SPRING_FLYWAY_ENABLED=false
APP_SIMPLE_MIGRATIONS_ENABLED=true
SPRING_PROFILES_ACTIVE=cloud
```

2) Construir el JAR:

```cmd
cd "C:\Users\estiv\Documents\Visual Studio 2022\java\Proyecto-bienestar\Bienestar"
mvn -DskipTests clean package
```

3) Ejecutar con las propiedades apuntando a Supabase (ejemplo con cmd.exe):

```cmd
java -Dspring.profiles.active=cloud -Dspring.datasource.url="jdbc:postgresql://<host>:5432/postgres?sslmode=require" -Dspring.datasource.username=postgres -Dspring.datasource.password="<tu_password>" -Dspring.flyway.enabled=false -Dapp.simple-migrations.enabled=true -jar target\Bienestar-0.0.1-SNAPSHOT.jar
```

4) Verificar migraciones y sesión de usuario:

- Revisa en la base de datos que exista la tabla `flyway_schema_history` y las tablas de dominio (`usuarios`, `roles`, `servicios`, etc.).
- Usa el script de verificación `scripts/do_login_post.ps1` (PowerShell) para reproducir un login y un POST a `/api/servicios`.

Notas sobre Swagger / sesiones en el navegador

- El proyecto permite form-login con CSRF activo por defecto. Para probar las APIs desde Swagger UI en el navegador asegúrate de estar autenticado con el formulario `/login` en la misma sesión del navegador antes de usar las operaciones protegidas en Swagger (Swagger UI no comparte la cookie JSESSIONID con otras pestañas/orígenes si el navegador la bloquea).
- Si prefieres pruebas programáticas, usa `scripts/do_login_post.ps1` que preserva la cookie de sesión y maneja el token CSRF.

Limpieza aplicada

- Se eliminaron archivos temporales y de depuración que no deben estar versionados (cookies, logs de sesión). Añade en `.gitignore` para evitar volver a cometerlos.

Si quieres que abra un PR con estos cambios y la rama limpia, dímelo y lo creo.