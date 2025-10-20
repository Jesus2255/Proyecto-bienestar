# Despliegue y conexión a PostgreSQL en la nube

Este documento explica cómo crear una base de datos PostgreSQL en un proveedor cloud (Railway, Supabase, Render, ElephantSQL) y cómo configurar la aplicación Spring Boot para usarla sin exponer credenciales en el repositorio.

Requisitos previos
- Cuenta en Railway/Supabase/Render o similar
- Git + Maven instalados

Pasos rápidos (Railway - ejemplo)
1. Crear un proyecto en Railway y añadir un plugin PostgreSQL.
2. Railway mostrará la cadena de conexión (JDBC) y usuario/password. Copia la URL.
3. Localmente crea un archivo `.env` en la raíz del proyecto (NO comitees `.env`). Usa el `.env.example` como plantilla.

Ejemplo `.env` (rellenar con los valores reales):

```
SPRING_DATASOURCE_URL=jdbc:postgresql://containers-us-west-123.railway.app:5432/dbname
SPRING_DATASOURCE_USERNAME=your_user
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_PROFILES_ACTIVE=cloud
```

4. Ejecuta la aplicación usando las variables de entorno (Windows cmd):

```cmd
# desde la carpeta Bienestar
set "SPRING_DATASOURCE_URL=jdbc:postgresql://..."
set "SPRING_DATASOURCE_USERNAME=your_user"
set "SPRING_DATASOURCE_PASSWORD=your_password"
set "SPRING_PROFILES_ACTIVE=cloud"
mvn -f Bienestar\pom.xml spring-boot:run
```

O con `java -jar`:

```cmd
set "SPRING_DATASOURCE_URL=jdbc:postgresql://..."
set "SPRING_DATASOURCE_USERNAME=your_user"
set "SPRING_DATASOURCE_PASSWORD=your_password"
set "SPRING_PROFILES_ACTIVE=cloud"
java -jar Bienestar\target\Bienestar-0.0.1-SNAPSHOT.jar
```

Nota sobre seguridad
- No comitees nunca tus credenciales. Usa `.env` (ignorarlo con `.gitignore`) o variables de entorno del runner de CI.
- Para despliegues, configura las variables de entorno desde la UI del proveedor (Railway/Render/Supabase) y no incrustes las credenciales en ficheros.

Cambio rápido entre DB en nube y H2
- Para desarrollo local puedes usar el perfil `dev` (H2) y para la nube `cloud`.
- Añade en `application-dev.properties` la configuración de H2, y en `application-cloud.properties` la URL JDBC que use variables de entorno (ya añadida `application-cloud.properties.example`).

Scripts SQL y migraciones
- Recomendación: usar Flyway o Liquibase para versionar scripts de la BD. De momento puedes generar las tablas con `spring.jpa.hibernate.ddl-auto=update` (no recomendado para producción).

Comprobaciones
- Una vez arrancada la app con perfil `cloud`, comprueba la conexión:

```
curl http://localhost:8080/actuator/health
```

- Comprueba que `/v3/api-docs` responde y que las entidades están mapeadas en la BD.

Si quieres, puedo:
- Añadir soporte para Flyway y generar un script SQL inicial `db/migration/V1__init.sql`.
- Crear un pequeño tutorial paso-a-paso con capturas para Railway/Supabase.

*** Fin ***
