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