# Guía de Configuración de Supabase

Esta guía explica cómo configurar la conexión a Supabase para el proyecto Bienestar y cómo usar las credenciales en diferentes entornos.

## 📋 Tabla de Contenidos

1. [Obtener Credenciales de Supabase](#1-obtener-credenciales-de-supabase)
2. [Configuración Local](#2-configuración-local)
3. [Configuración en Despliegue](#3-configuración-en-despliegue)
4. [Row Level Security (RLS)](#4-row-level-security-rls)
5. [Consideraciones de IPv4](#5-consideraciones-de-ipv4)
6. [Connection Pooling](#6-connection-pooling)
7. [Seguridad](#7-seguridad)

---

## 1. Obtener Credenciales de Supabase

### 1.1 API Keys (para REST API)

1. Ve a tu proyecto en [Supabase Dashboard](https://supabase.com/dashboard)
2. Navega a **Settings** > **API**
3. Encontrarás las siguientes keys:

![Supabase API Keys](https://github.com/user-attachments/assets/f06b89c7-e7f5-49a9-9cd4-62a50e57ea39)

- **Project URL** (`SUPABASE_URL`): URL base de tu proyecto (ej: `https://xxxxx.supabase.co`)
- **anon/public key** (`SUPABASE_ANON_KEY`): Segura para usar en frontend (Android, HTML5)
- **service_role key** (`SUPABASE_SERVICE_ROLE_KEY`): ⚠️ **SOLO BACKEND** - Bypasses RLS

### 1.2 Connection String (para conexión directa a PostgreSQL)

1. Ve a **Settings** > **Database**
2. Busca la sección **Connection string**
3. Selecciona la pestaña **URI**

![Supabase Connection Info](https://github.com/user-attachments/assets/fcc0ef7c-fe8b-42bc-8f46-33fa22ad5e2f)

4. Copia el connection string en formato:
   ```
   postgres://postgres:[YOUR-PASSWORD]@db.xxxxx.supabase.co:5432/postgres
   ```

5. Reemplaza `[YOUR-PASSWORD]` con tu contraseña de base de datos
   - La encontrarás en **Settings** > **Database** > **Database password**
   - Si olvidaste la contraseña, puedes generar una nueva

---

## 2. Configuración Local

### 2.1 Crear archivo .env

1. Copia el archivo de ejemplo:
   ```bash
   cp .env.example .env
   ```

2. Edita `.env` con tus credenciales reales:
   ```properties
   # API Keys
   SUPABASE_URL=https://xxxxx.supabase.co
   SUPABASE_ANON_KEY=tu_anon_key_aqui
   SUPABASE_SERVICE_ROLE_KEY=tu_service_role_key_aqui
   
   # Database Connection (elige una opción)
   
   # Opción 1: DATABASE_URL
   DATABASE_URL=postgres://postgres:tu_password@db.xxxxx.supabase.co:5432/postgres
   
   # Opción 2: Variables JDBC (tienen prioridad sobre DATABASE_URL)
   JDBC_DATABASE_URL=jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres
   JDBC_DATABASE_USERNAME=postgres
   JDBC_DATABASE_PASSWORD=tu_password
   ```

3. **IMPORTANTE**: Asegúrate de que `.env` está en `.gitignore`

### 2.2 Cargar variables de entorno

**Linux/Mac:**
```bash
export $(cat .env | xargs)
```

**Windows (PowerShell):**
```powershell
Get-Content .env | ForEach-Object {
    if ($_ -match '^([^=]+)=(.*)$') {
        [Environment]::SetEnvironmentVariable($matches[1], $matches[2])
    }
}
```

**Windows (CMD):**
```cmd
for /F "tokens=*" %i in (.env) do set %i
```

### 2.3 Ejecutar la aplicación

```bash
cd Bienestar
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

O con el JAR compilado:
```bash
mvn -DskipTests clean package
java -Dspring.profiles.active=prod -jar target/Bienestar-0.0.1-SNAPSHOT.jar
```

---

## 3. Configuración en Despliegue

### 3.1 GitHub Actions

En tu repositorio de GitHub:

1. Ve a **Settings** > **Secrets and variables** > **Actions**
2. Click en **New repository secret**
3. Añade cada variable:
   - `SUPABASE_URL`
   - `SUPABASE_ANON_KEY`
   - `SUPABASE_SERVICE_ROLE_KEY`
   - `JDBC_DATABASE_URL`
   - `JDBC_DATABASE_USERNAME`
   - `JDBC_DATABASE_PASSWORD`

Uso en workflow (`.github/workflows/deploy.yml`):
```yaml
- name: Deploy
  env:
    SUPABASE_URL: ${{ secrets.SUPABASE_URL }}
    JDBC_DATABASE_URL: ${{ secrets.JDBC_DATABASE_URL }}
    JDBC_DATABASE_USERNAME: ${{ secrets.JDBC_DATABASE_USERNAME }}
    JDBC_DATABASE_PASSWORD: ${{ secrets.JDBC_DATABASE_PASSWORD }}
  run: |
    # Tu comando de despliegue
```

### 3.2 Heroku

```bash
heroku config:set SUPABASE_URL=https://xxxxx.supabase.co
heroku config:set JDBC_DATABASE_URL=jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres
heroku config:set JDBC_DATABASE_USERNAME=postgres
heroku config:set JDBC_DATABASE_PASSWORD=tu_password
```

### 3.3 Render

1. Ve a tu servicio en [Render Dashboard](https://dashboard.render.com/)
2. Click en **Environment**
3. Añade las variables de entorno una por una

### 3.4 Vercel

1. Ve a tu proyecto en [Vercel Dashboard](https://vercel.com/dashboard)
2. **Settings** > **Environment Variables**
3. Añade cada variable con su valor

---

## 4. Row Level Security (RLS)

### ¿Qué es RLS?

Row Level Security permite controlar qué filas de una tabla puede ver o modificar cada usuario. Es **OBLIGATORIO** cuando usas `anon_key` desde aplicaciones cliente.

### Configurar RLS

1. Ve a **Table Editor** en Supabase Dashboard
2. Selecciona tu tabla
3. Click en **Enable RLS** si no está habilitado
4. Ve a **Authentication** > **Policies**
5. Crea políticas para cada operación (SELECT, INSERT, UPDATE, DELETE)

Ejemplo de política SELECT (todos pueden leer):
```sql
CREATE POLICY "Enable read access for all users" 
ON public.tu_tabla FOR SELECT 
USING (true);
```

Ejemplo de política INSERT (solo usuarios autenticados):
```sql
CREATE POLICY "Enable insert for authenticated users only" 
ON public.tu_tabla FOR INSERT 
TO authenticated
WITH CHECK (true);
```

### ⚠️ Advertencia

- **anon_key**: Respeta RLS - SEGURA para frontend
- **service_role_key**: Bypasses RLS - **SOLO BACKEND**

---

## 5. Consideraciones de IPv4

### Advertencia en Dashboard

Si ves este mensaje en tu dashboard de Supabase:

![Not IPv4 Compatible Warning](https://github.com/user-attachments/assets/fcc0ef7c-fe8b-42bc-8f46-33fa22ad5e2f)

Significa que tu base de datos no tiene una dirección IPv4 asignada por defecto.

### Soluciones

#### Opción 1: Session Pooler (Recomendado)
Cambiar el puerto en tu connection string de `5432` a `6543`:

```properties
DATABASE_URL=postgres://postgres:password@db.xxxxx.supabase.co:6543/postgres
```

El Session Pooler funciona bien con HikariCP y maneja mejor múltiples conexiones.

#### Opción 2: IPv4 Add-on
Supabase ofrece un add-on de IPv4 (puede tener costo). Ve a **Settings** > **Add-ons** > **IPv4 Address**.

#### Opción 3: Usar API REST
En lugar de conexión directa a PostgreSQL, usa la API REST de Supabase:

```java
// Ejemplo usando HTTP client
String supabaseUrl = System.getenv("SUPABASE_URL");
String anonKey = System.getenv("SUPABASE_ANON_KEY");

// GET request
String url = supabaseUrl + "/rest/v1/tabla?select=*";
// Añadir header: Authorization: Bearer {anonKey}
```

---

## 6. Connection Pooling

### Puertos de Supabase

- **Puerto 5432**: Direct connection / Transaction Pooler
  - Usar cuando tienes control total sobre el pool de conexiones
  - Compatible con HikariCP configurado en `DataSourceConfig.java`
  
- **Puerto 6543**: Session Pooler
  - Recomendado para aplicaciones con múltiples instancias
  - Mejor para entornos serverless
  - Maneja automáticamente el pooling del lado del servidor

### Configuración en HikariCP

El proyecto ya incluye configuración optimizada en `DataSourceConfig.java`:

```java
config.setMaximumPoolSize(10);     // Máximo de conexiones
config.setMinimumIdle(2);          // Mínimo idle
config.setConnectionTimeout(30000); // 30 segundos
config.setIdleTimeout(600000);     // 10 minutos
config.setMaxLifetime(1800000);    // 30 minutos
```

Para Session Pooler, puedes ajustar:
```java
config.setConnectionTestQuery("SELECT 1");
config.setValidationTimeout(5000);
```

---

## 7. Seguridad

### ✅ Buenas Prácticas

1. **NUNCA commitear credenciales reales**
   - Usa `.env.example` sin valores
   - Añade `.env` a `.gitignore`

2. **service_role_key: SOLO BACKEND**
   - Nunca en Android, HTML5, o cualquier cliente
   - Solo en código del servidor Spring Boot

3. **anon_key: Para Frontend**
   - Segura para aplicaciones cliente
   - Requiere RLS habilitado y políticas configuradas

4. **Rotar keys regularmente**
   - Settings > API > Roll API Keys
   - Actualizar en todos los entornos

5. **Usar variables de entorno**
   - GitHub Secrets para CI/CD
   - Variables de entorno en plataformas de hosting
   - Nunca hardcodear en código

### ❌ Evitar

- ❌ Commitear `.env` con valores reales
- ❌ Exponer `service_role_key` en frontend
- ❌ Desabilitar RLS en tablas usadas con `anon_key`
- ❌ Hardcodear passwords en código fuente
- ❌ Compartir credenciales por chat/email

---

## 📚 Recursos Adicionales

- [Documentación de Supabase](https://supabase.com/docs)
- [Supabase Database Connection](https://supabase.com/docs/guides/database/connecting-to-postgres)
- [Row Level Security](https://supabase.com/docs/guides/auth/row-level-security)
- [Connection Pooling](https://supabase.com/docs/guides/database/connection-pooling)

---

## 🆘 Solución de Problemas

### Error: "No se encontró configuración de base de datos"

**Causa**: No se definieron las variables de entorno.

**Solución**: Verifica que las variables están cargadas:
```bash
echo $JDBC_DATABASE_URL
echo $DATABASE_URL
```

### Error: "Connection refused"

**Causa**: Puerto incorrecto o firewall.

**Solución**: 
- Verifica el puerto (5432 o 6543)
- Prueba con Session Pooler (6543)
- Verifica que tu IP no esté bloqueada

### Error: "Authentication failed"

**Causa**: Contraseña incorrecta.

**Solución**:
- Verifica la contraseña en Supabase Dashboard
- Regenera la contraseña si es necesario
- Asegúrate de no tener caracteres especiales sin escapar

### La aplicación frontend no puede leer datos

**Causa**: RLS habilitado pero sin políticas.

**Solución**:
- Ve a Authentication > Policies
- Crea políticas para las operaciones necesarias
- Usa `anon_key`, no `service_role_key`

---

**¿Preguntas?** Abre un issue en el repositorio o consulta la [documentación de Supabase](https://supabase.com/docs).
