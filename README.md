# 🌿 Sistema de Gestión de Bienestar

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-green?style=for-the-badge&logo=spring)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple?style=for-the-badge&logo=kotlin)
![Android](https://img.shields.io/badge/Android-7.0+-blue?style=for-the-badge&logo=android)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**Aplicación empresarial para gestión de servicios de bienestar**  
Backend REST API + Aplicación Móvil Android

[📖 Manual Técnico](docs/manual-tecnico.html) • [👥 Manual de Usuario](docs/manual-usuario.html) • [📋 Buenas Prácticas](INFORME_BUENAS_PRACTICAS.md)

</div>

---

## 📋 Tabla de Contenidos

- [Descripción](#-descripción)
- [Características](#-características)
- [Tecnologías](#-tecnologías)
- [Arquitectura](#-arquitectura)
- [Instalación](#-instalación)
- [Documentación](#-documentación)
- [API REST](#-api-rest)
- [Seguridad](#-seguridad)
- [Patrones de Diseño](#-patrones-de-diseño)
- [Licencia](#-licencia)

---

## 🎯 Descripción

Sistema integral para gestión de servicios de bienestar que permite administrar clientes, servicios, citas y facturación. Compuesto por:

- **Backend:** API REST desarrollada en Spring Boot 3.5.6 con Java 17
- **Frontend Móvil:** Aplicación Android nativa con Kotlin y Jetpack Compose
- **Base de Datos:** PostgreSQL 17 (Supabase)

### Resumen Técnico (Nota de Versión)


**Problema resuelto:** Incompatibilidad entre `springdoc-openapi` y Spring Framework 6.2.11 causaba `NoSuchMethodError` en `ControllerAdviceBean`. Se actualizó `springdoc-openapi-starter-webmvc-ui` a **2.7.0** y ahora `/v3/api-docs` y Swagger UI funcionan correctamente.

---

## ✨ Características

### Backend (Spring Boot)
- ✅ **API REST completa** con operaciones CRUD
- ✅ **Autenticación y autorización** con Spring Security
- ✅ **Control de acceso basado en roles** (RBAC): Admin, Recepcionista, Cliente
- ✅ **Validación de datos** con Jakarta Validation
- ✅ **Documentación automática** con Swagger/OpenAPI 3
- ✅ **Persistencia** con JPA/Hibernate + PostgreSQL
- ✅ **Manejo centralizado de excepciones**
- ✅ **Compatibilidad con clientes móviles** (sin redirecciones)

### Frontend Android
- ✅ **UI moderna** con Jetpack Compose + Material Design 3
- ✅ **Arquitectura MVVM** (Model-View-ViewModel)
- ✅ **Gestión de estado** con StateFlow y Coroutines
- ✅ **Networking** con Retrofit 2 + OkHttp
- ✅ **Navegación** entre pantallas con Navigation Compose
- ✅ **Validación de formularios** en tiempo real
- ✅ **Manejo de sesiones** con UserSession singleton

---

## 🛠️ Tecnologías

### Backend
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 17 LTS | Lenguaje principal |
| Spring Boot | 3.5.6 | Framework backend |
| Spring Security | 6.x | Autenticación/Autorización |
| Spring Data JPA | 3.x | ORM y persistencia |
| PostgreSQL | 17 | Base de datos |
| SpringDoc OpenAPI | 2.7.0 | Documentación API |
| Maven | 3.9+ | Gestión de dependencias |

### Frontend Android
| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Kotlin | 1.9+ | Lenguaje principal |
| Jetpack Compose | 1.5+ | UI declarativa |
| Material 3 | Latest | Componentes UI |
| Retrofit | 2.9.0 | Cliente HTTP |
| OkHttp | 4.12.0 | Networking |
| Navigation Compose | 2.7.7 | Navegación |
| Coroutines | 1.7+ | Asincronía |

---

## 🏗️ Arquitectura

### Arquitectura General

```
┌─────────────────┐      HTTP/JSON      ┌─────────────────┐
│  Android App    │◄──────────────────►│  Spring Boot    │
│  (MVVM)         │                     │  (Backend API)  │
└─────────────────┘                     └────────┬────────┘
                                                 │
                                        ┌────────▼────────┐
                                        │   PostgreSQL    │
                                        │   (Supabase)    │
                                        └─────────────────┘
```

### Backend (Capas)

```
┌──────────────────────────────────────────────┐
│           Controllers (REST API)              │  ← Presentation
├──────────────────────────────────────────────┤
│              Services (Business)              │  ← Business Logic
├──────────────────────────────────────────────┤
│          Repositories (DAO Pattern)           │  ← Data Access
├──────────────────────────────────────────────┤
│         Entities + DTOs (Domain Model)        │  ← Domain
└──────────────────────────────────────────────┘
```

### Android (MVVM)

```
┌──────────────┐     observes     ┌──────────────┐
│  UI (Compose)│◄─────────────────│  ViewModel   │
└──────────────┘                  └──────┬───────┘
                                          │ calls
                                  ┌───────▼───────┐
                                  │  ApiService   │
                                  │  (Retrofit)   │
                                  └───────────────┘
```

---

## 🚀 Instalación

### Requisitos Previos

**Backend:**
- JDK 17 o superior
- Maven 3.8+
- PostgreSQL 12+ (o cuenta Supabase)

**Android:**
- Android Studio Hedgehog o superior
- Android SDK 24+ (Android 7.0)
- Dispositivo o emulador Android

### Backend - Instalación Rápida

1. **Clonar repositorio:**
```bash
git clone https://github.com/Jesus2255/Proyecto-bienestar.git
cd Proyecto-bienestar/Bienestar
```

2. **Configurar base de datos:**

Crear `config/application-local.properties`:
```properties
spring.datasource.url=jdbc:postgresql://HOST:5432/DATABASE
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

3. **Compilar:**
```bash
mvn clean package -DskipTests
```

4. **Ejecutar:**
```bash
java -jar target/Bienestar-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

O usar el script proporcionado:
```bash
run-local.cmd
```

5. **Verificar:**
- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

### Android - Instalación

1. **Abrir en Android Studio:**
```
File → Open → Seleccionar carpeta "Bienestar app"
```

2. **Configurar URL del backend:**

En `NetworkModule.kt`:
```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"  // Emulador
// private const val BASE_URL = "http://TU_IP:8080/"  // Dispositivo físico
```

3. **Ejecutar:**
- Conectar dispositivo o iniciar emulador
- Clic en Run (▶️)
- Credenciales de prueba: `admin/1234` o `client/1234`

---

## 📚 Documentación

### Manuales Disponibles

| Documento | Descripción | Enlace |
|-----------|-------------|--------|
| **Manual Técnico** | Arquitectura, instalación, configuración, API, base de datos, patrones de diseño | [📖 Ver HTML](docs/manual-tecnico.html) |
| **Manual de Usuario** | Guía de uso de la aplicación móvil con capturas de pantalla | [👥 Ver HTML](docs/manual-usuario.html) |
| **Informe de Buenas Prácticas** | Análisis de POO, SOLID, patrones de diseño y seguridad | [📋 Ver Markdown](INFORME_BUENAS_PRACTICAS.md) |

### Diagramas

**Modelo de Datos:**
```
┌─────────────┐     M:N     ┌─────────────┐
│  USUARIOS   │◄───────────►│    ROLES    │
└──────┬──────┘             └─────────────┘
       │
       │ 1:N
       │
┌──────▼──────┐             ┌─────────────┐
│  CLIENTES   │             │  SERVICIOS  │
└──────┬──────┘             └──────┬──────┘
       │                           │
       │ 1:N                  N:1  │
       │      ┌─────────────┐      │
       └─────►│    CITAS    │◄─────┘
              └──────┬──────┘
                     │ 1:N
              ┌──────▼──────┐
              │  FACTURAS   │
              └─────────────┘
```

---

## 🔌 API REST

### Autenticación

**POST** `/login` - Iniciar sesión
```bash
curl -X POST http://localhost:8080/login \
  -d "username=admin&password=1234"
```

**GET** `/api/auth/user-info` - Obtener información del usuario
```bash
curl http://localhost:8080/api/auth/user-info \
  -b cookies.txt
```

### Clientes

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/api/clientes` | Listar clientes | Todos |
| GET | `/api/clientes/{id}` | Obtener cliente | Todos |
| POST | `/api/clientes` | Crear cliente | Admin, Receptionist |
| PUT | `/api/clientes/{id}` | Actualizar cliente | Admin, Receptionist |
| DELETE | `/api/clientes/{id}` | Eliminar cliente | Admin |

**Ejemplo - Crear cliente:**
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "telefono": "555-1234"
  }'
```

### Servicios

- `GET /api/servicios` - Listar servicios
- `POST /api/servicios` - Crear servicio (Admin)
- `PUT /api/servicios/{id}` - Actualizar servicio (Admin)
- `DELETE /api/servicios/{id}` - Eliminar servicio (Admin)

### Citas

- `POST /api/citas` - Agendar cita
- `PUT /api/citas/{id}` - Actualizar cita
- `DELETE /api/citas/{id}` - Cancelar cita

📖 **Documentación completa:** http://localhost:8080/swagger-ui.html

---

## 🔐 Seguridad

### Autenticación
- **Mecanismo:** Form-based authentication con Spring Security
- **Almacenamiento:** Cookie JSESSIONID (session-based)
- **Validación:** `UserDetailsService` personalizado

### Autorización (RBAC)

| Rol | Permisos |
|-----|----------|
| **ADMIN** | Acceso completo: CRUD de clientes, servicios, citas, facturas, usuarios |
| **RECEPTIONIST** | Gestión de clientes y citas (sin eliminar) |
| **CLIENT** | Solo lectura: ver servicios y sus propias citas |

### Validación de Datos

Validación en múltiples niveles:
1. **DTOs:** `@NotBlank`, `@Email`, `@NotNull`
2. **Controllers:** `@Valid` activa validaciones
3. **Exception Handler:** `@ControllerAdvice` centraliza errores

```java
public class ClienteDTO {
    @NotBlank(message = "Nombre obligatorio")
    private String nombre;

    @NotBlank @Email(message = "Email inválido")
    private String email;
}
```

### ⚠️ Seguridad en Producción

```java
// ❌ Desarrollo (actual)
@Bean
public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();  // SOLO DESARROLLO
}

// ✅ Producción (REQUERIDO)
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

---

## 🎨 Patrones de Diseño

### Implementados

| Patrón | Ubicación | Descripción |
|--------|-----------|-------------|
| **DAO** | `*Repository` | Abstracción de acceso a datos con Spring Data JPA |
| **Singleton** | `@Service`, `@Component` | Instancia única gestionada por Spring IoC |
| **MVC** | Arquitectura general | Model (Entidades), View (JSON), Controller (REST) |
| **Facade** | Capa de servicios | Simplifica operaciones complejas |
| **Dependency Injection** | Constructores | Inyección automática de dependencias |
| **MVVM** | Android | Model-View-ViewModel en la app móvil |

### Principios SOLID

✅ **S** - Single Responsibility: Cada clase tiene una responsabilidad única  
✅ **O** - Open/Closed: Extensible mediante `@ExceptionHandler`, DTOs  
✅ **L** - Liskov Substitution: Interfaces intercambiables  
✅ **I** - Interface Segregation: Interfaces específicas  
✅ **D** - Dependency Inversion: Dependencia de abstracciones  

📋 **Análisis completo:** [INFORME_BUENAS_PRACTICAS.md](INFORME_BUENAS_PRACTICAS.md)

---

## 🧪 Testing

```bash
# Backend - Ejecutar tests
mvn test

# Backend - Ver cobertura
mvn jacoco:report

# Android - Ejecutar tests unitarios
./gradlew test

# Android - Tests instrumentados
./gradlew connectedAndroidTest
```

---

## 📦 Despliegue

### Compilar para Producción

```bash
mvn clean package -DskipTests
```

### Docker (Opcional)

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
docker build -t bienestar-app .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod bienestar-app
```

---

## 🐛 Troubleshooting

### Error: NoSuchMethodError (ControllerAdviceBean)


**Causa:** Incompatibilidad entre `springdoc-openapi` y Spring Framework 6.2.11

**Solución:** Actualizar en `pom.xml`:

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.7.0</version>
</dependency>
```

### Error: Unable to rename JAR

**Solución:** Matar proceso Java que está usando el JAR:
```bash
wmic process where "CommandLine like '%Bienestar%'" get ProcessId
taskkill /PID <PID> /F
```

### App Android no conecta

**Solución:**
1. Verificar que el backend esté corriendo: `http://localhost:8080/actuator/health`
2. Emulador: usar `http://10.0.2.2:8080/`
3. Dispositivo físico: usar IP de tu PC (ej: `http://192.168.1.100:8080/`)

---

## 📊 Métricas del Proyecto

- **Líneas de código backend:** ~3,500
- **Líneas de código Android:** ~2,000
- **Endpoints REST:** 15+
- **Entidades JPA:** 6
- **Pantallas Android:** 5
- **Cobertura de tests:** En desarrollo

---

## 👥 Contribuir

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add: AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo `LICENSE` para más detalles.

---

## 📧 Contacto

- **GitHub:** [@Jesus2255](https://github.com/Jesus2255)
- **Proyecto:** [Proyecto-bienestar](https://github.com/Jesus2255/Proyecto-bienestar)
- **Email:** soporte@bienestar.com

---

## 🙏 Agradecimientos

- Spring Boot Team por el excelente framework
- Google por Jetpack Compose y Material Design
- Comunidad open-source

---

<div align="center">

**⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub ⭐**

Hecho con ❤️ usando Spring Boot + Kotlin + Jetpack Compose

</div>

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

