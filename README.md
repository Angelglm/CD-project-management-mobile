# Sistema de Proyectos UAQ (CD-project-management-mobile)

Aplicación móvil Android (Kotlin + Jetpack Compose) para la gestión de proyectos: autenticación, listado/creación de proyectos, miembros y tablero Kanban de tareas. Usa Retrofit con Kotlinx Serialization para consumir la API `https://pmaster.elcilantro.site/api/`.

## Stack
- Android, Kotlin, Gradle
- Jetpack Compose (Material 3, BOM)
- Lifecycle ViewModel (Compose + KTX)
- Retrofit 2 + Kotlinx Serialization JSON
- OkHttp (con interceptor de autorización)

## Requisitos
- Android Studio (Electric Eel/Koala o superior)
- SDK Android `compileSdk = 34`, `targetSdk = 34`, `minSdk = 24`

## Configuración
1. Clonar el repositorio.
2. Abrir en Android Studio; se generará `local.properties` con la ruta del SDK.
3. Verificar/editar el base URL en `app/src/main/java/.../data/network/ApiService.kt`:
   - `private const val BASE_URL = "https://pmaster.elcilantro.site/api/"`
4. Sin credenciales, la app solo mostrará errores al autenticar; solicita acceso al backend correspondiente.

> Nota: `createUnsafeOkHttpClient()` está habilitado para desarrollo (confía todos los certificados y `usesCleartextTraffic=true`). No usar en producción.

## Construir y ejecutar
Desde terminal PowerShell en Windows dentro del directorio del proyecto:

```powershell
# Compilar APK debug
./gradlew.bat assembleDebug

# Instalar en dispositivo/emulador conectado
./gradlew.bat installDebug
```

También puedes ejecutar desde Android Studio con el botón Run.

## Funcionalidades
- Inicio de sesión y manejo de token.
- Listado de proyectos y detalle.
- Crear proyecto (nombre, descripción, cliente, líder, fechas).
- Miembros del proyecto: listado y agregar miembro.
- Tareas en tablero Kanban (TO DO, IN PROGRESS, DONE), ver y actualizar estado, eliminar.
- Acciones condicionadas por rol (p.ej., agregar tareas para admin).


