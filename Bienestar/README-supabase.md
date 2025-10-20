# Guía de Configuración: Supabase + Spring Boot

Esta guía te ayudará a conectar tu aplicación Spring Boot al backend de Supabase (PostgreSQL) y a usar la API de Supabase desde diferentes plataformas (Android, HTML/JavaScript).

## Tabla de Contenidos

1. [Requisitos Previos](#requisitos-previos)
2. [Obtener Credenciales de Supabase](#obtener-credenciales-de-supabase)
3. [Configuración para Spring Boot](#configuración-para-spring-boot)
4. [Configuración en Producción](#configuración-en-producción)
5. [Uso de Supabase desde Android](#uso-de-supabase-desde-android)
6. [Uso de Supabase desde HTML/JavaScript](#uso-de-supabase-desde-htmljavascript)
7. [Seguridad y Mejores Prácticas](#seguridad-y-mejores-prácticas)
8. [Solución de Problemas](#solución-de-problemas)

---

## Requisitos Previos

- Cuenta en [Supabase](https://supabase.com/) (gratuita)
- Proyecto creado en Supabase
- Java 17+ y Maven instalados
- Git configurado

---

## Obtener Credenciales de Supabase

### 1. URL del Proyecto y API Keys

1. Ve a [Supabase Dashboard](https://app.supabase.com/)
2. Selecciona tu proyecto
3. Ve a **Settings** → **API**
4. Encontrarás:
   - **Project URL**: `https://xyzcompany.supabase.co`
   - **anon public key**: Para uso en frontend (segura de exponer)
   - **service_role secret**: Solo para backend (¡NUNCA exponer!)

### 2. Database Connection String (DATABASE_URL)

1. Ve a **Settings** → **Database**
2. En la sección **Connection string**, selecciona **URI**
3. Verás algo como:
   ```
   postgres://postgres.xyzcompany:[YOUR-PASSWORD]@aws-0-us-east-1.pooler.supabase.com:6543/postgres
   ```
4. **IMPORTANTE**: Si no recuerdas tu contraseña de base de datos:
   - Ve a **Database** → **Database password**
   - Haz clic en **Reset database password**
   - **GUARDA LA NUEVA CONTRASEÑA** en un lugar seguro
   - Actualiza la connection string con la nueva contraseña

### 3. Parámetros de Conexión Detallados (Alternativa)

Si prefieres ver los parámetros individuales:

1. Ve a **Settings** → **Database**
2. Encontrarás:
   - **Host**: `aws-0-us-east-1.pooler.supabase.com`
   - **Database name**: `postgres`
   - **Port**: `6543` (connection pooling) o `5432` (direct connection)
   - **User**: `postgres.xyzcompany`
   - **Password**: (la que configuraste)

---

## Configuración para Spring Boot

### Paso 1: Verificar Dependencias

El archivo `pom.xml` ya incluye la dependencia de PostgreSQL:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Paso 2: Configurar Variables de Entorno

1. **Copia el archivo de ejemplo**:
   ```bash
   cd Bienestar
   cp .env.example .env
   ```

2. **Edita `.env` con tus credenciales reales**:
   ```bash
   # Reemplaza con tus valores de Supabase
   SUPABASE_URL=https://your-project-id.supabase.co
   SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   DATABASE_URL=postgres://postgres.your-project:[YOUR-PASSWORD]@aws-0-region.pooler.supabase.com:6543/postgres
   ```

3. **Verifica que `.env` esté en `.gitignore`** (ya debería estarlo)

### Paso 3: Cargar Variables de Entorno

#### Opción A: Usando un plugin de Spring Boot

Agrega el plugin `dotenv-java` al `pom.xml`:

```xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```

#### Opción B: Usar variables de entorno del sistema

**Linux/Mac**:
```bash
export DATABASE_URL="postgres://postgres.xyz:[PASSWORD]@aws-0-us-east-1.pooler.supabase.com:6543/postgres"
./mvnw spring-boot:run
```

**Windows (CMD)**:
```cmd
set DATABASE_URL=postgres://postgres.xyz:[PASSWORD]@aws-0-us-east-1.pooler.supabase.com:6543/postgres
mvnw.cmd spring-boot:run
```

**Windows (PowerShell)**:
```powershell
$env:DATABASE_URL="postgres://postgres.xyz:[PASSWORD]@aws-0-us-east-1.pooler.supabase.com:6543/postgres"
./mvnw spring-boot:run
```

### Paso 4: Ejecutar la Aplicación

```bash
./mvnw clean spring-boot:run
```

Si todo está correcto, verás en los logs:
```
DATABASE_URL detectada. Configurando DataSource para Supabase/PostgreSQL...
JDBC URL construida: jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres
DataSource configurado exitosamente para Supabase/PostgreSQL
```

### Paso 5: Verificar la Conexión

Prueba algún endpoint de tu aplicación o verifica en los logs que Hibernate se conecta correctamente:
```
Hibernate: select ...
```

---

## Configuración en Producción

### GitHub Actions

En tu repositorio de GitHub:

1. Ve a **Settings** → **Secrets and variables** → **Actions**
2. Agrega los siguientes **Repository secrets**:
   - `DATABASE_URL`: La connection string completa
   - `SUPABASE_URL`: URL del proyecto
   - `SUPABASE_ANON_KEY`: anon public key

3. En tu workflow `.github/workflows/deploy.yml`:
   ```yaml
   env:
     DATABASE_URL: ${{ secrets.DATABASE_URL }}
     SUPABASE_URL: ${{ secrets.SUPABASE_URL }}
     SUPABASE_ANON_KEY: ${{ secrets.SUPABASE_ANON_KEY }}
   ```

### Heroku

```bash
heroku config:set DATABASE_URL="postgres://postgres.xyz:[PASSWORD]@aws-0-us-east-1.pooler.supabase.com:6543/postgres"
heroku config:set SUPABASE_URL="https://xyzcompany.supabase.co"
heroku config:set SUPABASE_ANON_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI..."
```

### Railway / Render / Vercel

En la sección de **Environment Variables** de tu servicio, agrega:
- `DATABASE_URL`
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`

---

## Uso de Supabase desde Android

### 1. Agregar Dependencias

En tu `build.gradle.kts` (Kotlin) o `build.gradle` (Groovy):

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.jan-tennert.supabase:postgrest-kt:2.0.0")
    implementation("io.ktor:ktor-client-android:2.3.5")
}
```

### 2. Inicializar Cliente

```kotlin
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://your-project-id.supabase.co",
    supabaseKey = "your-anon-public-key"
) {
    install(Postgrest)
    // Otros plugins según necesites (Auth, Realtime, etc.)
}
```

### 3. Realizar Queries

```kotlin
// SELECT
val usuarios = supabase.from("usuarios").select().decodeList<Usuario>()

// INSERT
supabase.from("usuarios").insert(Usuario(nombre = "Juan", email = "juan@example.com"))

// UPDATE
supabase.from("usuarios").update({ Usuario::nombre setTo "Pedro" }) {
    filter {
        Usuario::id eq 1
    }
}

// DELETE
supabase.from("usuarios").delete {
    filter {
        Usuario::id eq 1
    }
}
```

---

## Uso de Supabase desde HTML/JavaScript

### 1. Instalar Cliente de Supabase

#### Opción A: Usando NPM

```bash
npm install @supabase/supabase-js
```

```javascript
import { createClient } from '@supabase/supabase-js'

const supabaseUrl = 'https://your-project-id.supabase.co'
const supabaseAnonKey = 'your-anon-public-key'

const supabase = createClient(supabaseUrl, supabaseAnonKey)
```

#### Opción B: Usando CDN

```html
<script src="https://cdn.jsdelivr.net/npm/@supabase/supabase-js@2"></script>
<script>
  const { createClient } = supabase
  const supabaseUrl = 'https://your-project-id.supabase.co'
  const supabaseAnonKey = 'your-anon-public-key'
  const supabase = createClient(supabaseUrl, supabaseAnonKey)
</script>
```

### 2. Realizar Queries

```javascript
// SELECT
const { data: usuarios, error } = await supabase
  .from('usuarios')
  .select('*')

// INSERT
const { data, error } = await supabase
  .from('usuarios')
  .insert([
    { nombre: 'Juan', email: 'juan@example.com' }
  ])

// UPDATE
const { data, error } = await supabase
  .from('usuarios')
  .update({ nombre: 'Pedro' })
  .eq('id', 1)

// DELETE
const { data, error } = await supabase
  .from('usuarios')
  .delete()
  .eq('id', 1)
```

---

## Seguridad y Mejores Prácticas

### ⚠️ Claves y Secretos

| Clave | Dónde Usar | ¿Se puede exponer? |
|-------|------------|-------------------|
| `SUPABASE_URL` | Frontend y Backend | ✅ Sí, es pública |
| `SUPABASE_ANON_KEY` | Frontend y Backend | ✅ Sí, es pública |
| `SUPABASE_SERVICE_ROLE_KEY` | **SOLO Backend** | ❌ **NUNCA** |
| `DATABASE_URL` | **SOLO Backend** | ❌ **NUNCA** |

### 🔒 Mejores Prácticas

1. **NUNCA commitees**:
   - Archivos `.env` con credenciales reales
   - `service_role` key en el código fuente
   - `DATABASE_URL` con contraseña

2. **Row Level Security (RLS)**:
   - Habilita RLS en todas tus tablas de Supabase
   - Define políticas de acceso para cada tabla
   - La `anon` key respeta RLS, la `service_role` lo bypasea

3. **Usa variables de entorno**:
   - En desarrollo: archivo `.env` (no commiteado)
   - En producción: variables de entorno del sistema/plataforma

4. **Rotación de credenciales**:
   - Cambia periódicamente la contraseña de la base de datos
   - Regenera API keys si sospechas que fueron comprometidas

5. **Conexiones seguras**:
   - Supabase usa SSL/TLS por defecto
   - El pooler (puerto 6543) es recomendado para aplicaciones serverless
   - Usa puerto 5432 para conexiones directas en servidores permanentes

### 🛡️ Configurar Row Level Security (RLS)

En Supabase SQL Editor:

```sql
-- Habilitar RLS en una tabla
ALTER TABLE usuarios ENABLE ROW LEVEL SECURITY;

-- Permitir lectura pública
CREATE POLICY "Permitir lectura pública"
  ON usuarios FOR SELECT
  USING (true);

-- Permitir inserción solo a usuarios autenticados
CREATE POLICY "Usuarios autenticados pueden insertar"
  ON usuarios FOR INSERT
  WITH CHECK (auth.role() = 'authenticated');

-- Los usuarios solo pueden editar sus propios registros
CREATE POLICY "Usuarios pueden editar su propio perfil"
  ON usuarios FOR UPDATE
  USING (auth.uid() = user_id);
```

---

## Solución de Problemas

### ❌ "org.postgresql.util.PSQLException: Connection refused"

**Causa**: No se puede conectar a la base de datos.

**Solución**:
1. Verifica que `DATABASE_URL` esté correctamente configurada
2. Asegúrate de usar el pooler (puerto `6543`)
3. Verifica que la contraseña sea correcta
4. Comprueba que tu IP no esté bloqueada en Supabase

### ❌ "password authentication failed for user"

**Causa**: Contraseña incorrecta.

**Solución**:
1. Ve a Supabase Dashboard → Database → Database password
2. Haz clic en **Reset database password**
3. Actualiza `DATABASE_URL` con la nueva contraseña

### ❌ Variables de entorno no se cargan

**Solución para IntelliJ IDEA**:
1. Ve a **Run** → **Edit Configurations**
2. En **Environment variables**, agrega:
   ```
   DATABASE_URL=postgres://...
   ```

**Solución para Eclipse**:
1. Right-click en el proyecto → **Run As** → **Run Configurations**
2. Pestaña **Environment** → **New**
3. Agrega `DATABASE_URL` con su valor

**Solución para VS Code**:
1. Instala la extensión "DotENV"
2. Crea un archivo `.env` en la raíz del proyecto
3. Agrega las variables

### ❌ "HikariPool - Connection is not available"

**Causa**: Pool de conexiones agotado o timeout.

**Solución**:
1. Aumenta `maximum-pool-size` en `application.properties`:
   ```properties
   spring.datasource.hikari.maximum-pool-size=20
   ```
2. Verifica que no haya conexiones colgadas (cierra conexiones correctamente)
3. Ajusta `connection-timeout` si es necesario

### ❌ Error de SSL/TLS

**Solución**:
Agrega al final de `DATABASE_URL`:
```
?sslmode=require
```

Ejemplo:
```
postgres://postgres.xyz:pass@aws-0-us-east-1.pooler.supabase.com:6543/postgres?sslmode=require
```

---

## Recursos Adicionales

- [Documentación oficial de Supabase](https://supabase.com/docs)
- [Supabase Client Libraries](https://supabase.com/docs/reference/javascript/introduction)
- [Spring Boot + PostgreSQL](https://spring.io/guides/gs/accessing-data-jpa/)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)

---

## Soporte

Si encuentras problemas:

1. Revisa los logs de Spring Boot para mensajes de error detallados
2. Verifica la sección de **Logs** en Supabase Dashboard
3. Consulta la [comunidad de Supabase](https://github.com/supabase/supabase/discussions)
4. Abre un issue en este repositorio

---

**¡Listo!** Ahora tu aplicación Spring Boot está conectada a Supabase y tienes ejemplos para conectar desde Android y HTML/JavaScript. 🚀
