# Guía de Implementación CRUD Completo - Bienestar App

## ✅ Ya Implementado

### 1. Modelos de Datos (`Models.kt`)
- ✅ `Cliente`: id, nombre, email, telefono, direccion
- ✅ `Cita`: id, clienteId, servicioId, fecha, hora, estado, notas
- ✅ `LoginResponse`: Para manejar roles de usuario
- ✅ `UserRole`: Enum con ADMIN, USER, GUEST

### 2. API Service Completo (`Apiservice.kt`)
- ✅ Endpoints CRUD para Servicios (GET, POST, PUT, DELETE)
- ✅ Endpoints CRUD para Clientes (GET, POST, PUT, DELETE)
- ✅ Endpoints CRUD para Citas (GET, POST, PUT, DELETE)
- ✅ Login y Logout

### 3. ViewModels
- ✅ `ClientesViewModel`: CRUD completo con estados
- ✅ `CitasViewModel`: CRUD completo con listas relacionadas
- ✅ `CrudUiState<T>`: Estados genéricos (Loading, Success, Error)

### 4. Pantallas
- ✅ `ClientesScreen`: Lista con FloatingActionButton, edición, eliminación y diálogo de formulario
- ✅ `ClienteDialog`: Formulario reutilizable para crear/editar

## 📋 Próximos Pasos

### PASO 1: Implementar CitasScreen (Similar a ClientesScreen)

Crear `CitasScreen.kt` con:
- Lista de citas con información del cliente y servicio
- FloatingActionButton para agregar nuevas citas
- Formulario con selectores (Dropdown) para Cliente y Servicio
- DatePicker y TimePicker para fecha y hora
- Selector de estado (PENDIENTE, CONFIRMADA, etc.)

### PASO 2: Mejorar ServiciosScreen con CRUD

Modificar `ServiciosScreen.kt`:
- Añadir FloatingActionButton
- Botones de editar y eliminar en cada item
- Diálogo de formulario para crear/editar servicios

### PASO 3: Sistema de Roles de Usuario

#### Backend (Spring Boot)
```java
// En tu LoginController o SecurityConfig
@PostMapping("/api/auth/user-info")
public Map<String, Object> getUserInfo(Principal principal) {
    UserDetails user = userDetailsService.loadUserByUsername(principal.getName());
    return Map.of(
        "username", user.getUsername(),
        "role", user.getAuthorities().iterator().next().getAuthority()
    );
}
```

#### Android
1. **Modificar `ApiService.kt`**:
```kotlin
@GET("api/auth/user-info")
suspend fun getUserInfo(): LoginResponse
```

2. **Actualizar `LoginViewModel.kt`**:
```kotlin
var userRole by mutableStateOf<UserRole>(UserRole.GUEST)
    private set

fun performLogin() {
    viewModelScope.launch {
        try {
            loginStatus = "Cargando..."
            val response = NetworkModule.api.login(username, password)
            
            if (response.isSuccessful) {
                // Obtener info del usuario
                val userInfo = NetworkModule.api.getUserInfo()
                userRole = when(userInfo.role) {
                    "ROLE_ADMIN" -> UserRole.ADMIN
                    "ROLE_USER" -> UserRole.USER
                    else -> UserRole.GUEST
                }
                navigateToHome = true
            }
        } catch (e: Exception) {
            // ...
        }
    }
}
```

3. **Crear `UserSession.kt`** (Singleton para compartir el rol):
```kotlin
object UserSession {
    var userRole: UserRole = UserRole.GUEST
    var username: String = ""
    
    fun isAdmin() = userRole == UserRole.ADMIN
    fun isUser() = userRole == UserRole.USER
}
```

4. **Actualizar `HomeScreen.kt`** para mostrar botones según rol:
```kotlin
@Composable
fun HomeScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    Column {
        Text("Panel Principal")
        
        Button(onClick = { navController.navigate("servicios") }) {
            Text("Ver Servicios")
        }
        
        // Solo para ADMIN
        if (UserSession.isAdmin()) {
            Button(onClick = { navController.navigate("clientes") }) {
                Text("Ver Clientes")
            }
        }
        
        Button(onClick = { navController.navigate("citas") }) {
            Text("Ver Citas")
        }
        
        // Botón de logout
    }
}
```

### PASO 4: Persistencia Local con Room (Opcional pero Recomendado)

#### Añadir dependencias en `build.gradle.kts`:
```kotlin
dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
}
```

#### Crear estructura Room:

1. **Entities** (Ya tienes las data classes, solo añadir @Entity):
```kotlin
@Entity(tableName = "clientes")
data class Cliente(
    @PrimaryKey val id: Long = 0,
    val nombre: String = "",
    // ...
)
```

2. **DAOs**:
```kotlin
@Dao
interface ClienteDao {
    @Query("SELECT * FROM clientes")
    fun getAllClientes(): Flow<List<Cliente>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clientes: List<Cliente>)
    
    @Delete
    suspend fun delete(cliente: Cliente)
}
```

3. **Database**:
```kotlin
@Database(entities = [Cliente::class, Cita::class, Servicio::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun citaDao(): CitaDao
    abstract fun servicioDao(): ServicioDao
}
```

4. **Repository Pattern**:
```kotlin
class ClienteRepository(
    private val api: ApiService,
    private val dao: ClienteDao
) {
    val clientesFlow = dao.getAllClientes()
    
    suspend fun refreshClientes() {
        try {
            val clientes = api.getClientes()
            dao.insertAll(clientes)
        } catch (e: Exception) {
            // Usamos datos locales si falla la red
        }
    }
}
```

5. **Actualizar ViewModels para usar Repository**:
```kotlin
class ClientesViewModel(private val repository: ClienteRepository) : ViewModel() {
    val clientes = repository.clientesFlow.asStateFlow()
    
    fun refreshClientes() {
        viewModelScope.launch {
            repository.refreshClientes()
        }
    }
}
```

## 🎯 Características Adicionales Recomendadas

### 1. Búsqueda y Filtrado
- SearchBar en cada lista
- Filtros por fecha (para citas)
- Filtros por estado (para citas)

### 2. Validaciones
- Email válido en formulario de cliente
- Fecha no en el pasado para citas
- Campos requeridos con indicadores visuales

### 3. Confirmaciones
- AlertDialog antes de eliminar
- "¿Está seguro de eliminar este cliente?"

### 4. Mejoras de UX
- Pull to refresh en las listas
- Skeleton screens durante carga
- Animaciones de transición
- Indicadores de sincronización offline

### 5. Notificaciones
- WorkManager para recordatorios de citas
- Notificaciones push para nuevas citas

## 🔐 Seguridad Adicional

### Manejo de Sesiones
```kotlin
// En NetworkModule.kt
private val cookieJar = JavaNetCookieJar(
    CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }
)

private val okHttpClient = OkHttpClient.Builder()
    .cookieJar(cookieJar)
    .addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Requested-With", "XMLHttpRequest")
            .build()
        chain.proceed(request)
    }
    .build()
```

### Manejo de Errores HTTP
```kotlin
sealed class ApiResult<T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error<T>(val code: Int, val message: String) : ApiResult<T>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(apiCall())
    } catch (e: HttpException) {
        ApiResult.Error(e.code(), e.message())
    } catch (e: Exception) {
        ApiResult.Error(-1, e.message ?: "Error desconocido")
    }
}
```

## 📊 Testing

### Unit Tests para ViewModels
```kotlin
class ClientesViewModelTest {
    @Test
    fun `fetchClientes updates state to Success`() = runTest {
        // Arrange
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getClientes() } returns listOf(testCliente)
        
        // Act
        viewModel.fetchClientes()
        
        // Assert
        assertTrue(viewModel.uiState is CrudUiState.Success)
    }
}
```

## 🚀 Comandos de Construcción

```bash
# Sincronizar Gradle
./gradlew clean build

# Ejecutar en emulador
./gradlew installDebug

# Generar APK de release
./gradlew assembleRelease
```

## 📱 Estructura Final del Proyecto

```
app/src/main/java/com/example/bienestarapp/
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/
│   │   └── entities/
│   ├── remote/
│   │   ├── ApiService.kt
│   │   └── NetworkModule.kt
│   └── repository/
├── ui/
│   ├── screens/
│   │   ├── home/
│   │   ├── login/
│   │   ├── clientes/
│   │   ├── citas/
│   │   └── servicios/
│   ├── components/
│   │   └── CommonUI.kt
│   └── theme/
├── viewmodels/
└── models/
```

## ✅ Checklist de Implementación

- [x] Modelos de datos
- [x] API Service completo
- [x] ClientesViewModel
- [x] CitasViewModel
- [x] ClientesScreen con CRUD
- [ ] CitasScreen con CRUD
- [ ] ServiciosScreen mejorado con CRUD
- [ ] Sistema de roles
- [ ] Persistencia local con Room
- [ ] Búsqueda y filtros
- [ ] Validaciones de formularios
- [ ] Confirmaciones de eliminación
- [ ] Tests unitarios

## 📚 Recursos

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Retrofit](https://square.github.io/retrofit/)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
