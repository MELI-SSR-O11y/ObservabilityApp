# Observability App

Este proyecto es una aplicación de Android construida con Jetpack Compose y Kotlin, diseñada para monitorear y visualizar incidentes de observabilidad dentro de la propia aplicación. Permite rastrear eventos con diferentes niveles de severidad, asociarlos a pantallas específicas y visualizar los datos a través de gráficos interactivos. La aplicación cuenta con una arquitectura modular y utiliza Koin para la inyección de dependencias.

## Arquitectura y Módulos

La aplicación se integra con tres artefactos principales que componen la lógica de negocio y el acceso a datos:

- **domain**: Contiene los modelos de datos puros (ej. `Incident`, `Screen`) y las definiciones de severidad (`EIncidentSeverity`).
- **data**: Implementa los repositorios y las fuentes de datos (locales y remotas). Se encarga de la lógica de la base de datos (Room) y las llamadas de red (Ktor).
- **presentation**: Contiene la lógica de presentación (ViewModel) que conecta la UI con la capa de datos. `ContractObservabilityApi` es la interfaz principal de este módulo.

## 1. Integración de Artefactos

Para que el proyecto funcione, los artefactos (`.aar` o `.jar`) de los módulos `domain`, `data` y `presentation` deben ser agregados al proyecto.

### Pasos:

1.  **Copiar los Artefactos**: Crea una carpeta `libs` dentro del módulo `app` (`app/libs/`) y copia los archivos de los artefactos en ella.

2.  **Importar en Gradle**: Declara los artefactos como dependencias en el archivo `app/build.gradle.kts`. Esto le permite a Gradle incluirlos en el classpath del proyecto.

    ```kotlin
    // app/build.gradle.kts

    dependencies {
        // ... otras dependencias

        // Artefactos locales
        implementation(files("libs/domain.jar"))
        implementation(files("libs/data.aar"))
        implementation(files("libs/presentation.aar"))
    }
    ```

3.  **Sincronizar Gradle**: Sincroniza el proyecto en Android Studio para que las nuevas dependencias sean reconocidas.

## 2. Configuración de Koin

La inyección de dependencias se gestiona a través de Koin. Se necesita un módulo de Koin para proveer las instancias necesarias, como el ViewModel y sus dependencias.

### Pasos:

1.  **Crear el Módulo de Koin**: Define un módulo que declare cómo construir las dependencias. El `ContractObservabilityApi` (que es un ViewModel) es el punto central.

    ```kotlin
    // En algún lugar de la capa de la app, ej: di/AppModule.kt

    val appModule = module {
        viewModel<ContractObservabilityApi> { ContractViewModel(get()) }
        // Koin proveerá automáticamente las dependencias requeridas por ContractViewModel
        // siempre que estén definidas en los módulos de los artefactos (data, etc.)
    }
    ```

2.  **Inicializar Koin**: En la clase `Application` de la app, inicia Koin para que las dependencias estén disponibles en toda la aplicación.

    ```kotlin
    // MyApplication.kt

    class MyApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            startKoin {
                androidContext(this@MyApplication)
                modules(appModule) // Carga tu módulo junto con los de los artefactos
            }
        }
    }
    ```

## 3. Integración de `ContractObservabilityApi`

`ContractObservabilityApi` es la interfaz que comunica la UI con la lógica de negocio. Se inyecta directamente en los Composables.

-   **Inyección**: Se utiliza `koinInject()` para obtener una instancia del ViewModel en cualquier Composable.
-   **Estado (`state`)**: Expone un `StateFlow` que contiene el estado actual de la UI (ej. `state.isLoading`, `state.incidentsQuantity`). La UI reacciona a los cambios en este flujo de datos usando `collectAsStateWithLifecycle()`.
-   **Eventos (`onEvent`)**: Es una función que la UI invoca para notificar acciones del usuario (ej. `onEvent(MainActions.SyncToRemote)`).

```kotlin
@Composable
fun ObservabilityApp(api: ContractObservabilityApi = koinInject()) {
  val state by api.state.collectAsStateWithLifecycle()
  val onEvent = api::onEvent

  // El resto de la UI usa `state` para mostrar datos y `onEvent` para enviar acciones
  Button(onClick = { onEvent(MainActions.SyncToRemote) }) { /* ... */ }
}
```

## Funcionalidades de la Aplicación

### Pantalla Principal (Dashboard)

Es la pantalla principal y centro de visualización de datos. Ofrece una visión completa de los incidentes registrados.

-   **Filtros Interactivos**:
    -   **Pantalla**: Permite filtrar incidentes que ocurrieron en una pantalla específica.
    -   **Gravedad**: Filtra incidentes por su nivel de severidad (Debug, Info, Warning, Error, Critical).
    -   **Tiempo**: Permite ver incidentes ocurridos en un período determinado (última hora, últimas 24 horas, etc.).
-   **Métricas Clave**: Muestra contadores totales de la cantidad de pantallas registradas y el número total de incidentes, actualizándose según los filtros aplicados.
-   **Gráfico de Torta (`SeverityPieChart`)**: Visualiza la distribución porcentual de los incidentes según su severidad, permitiendo identificar rápidamente cuáles son los niveles más comunes.
-   **Gráfico de Serie Temporal (`IncidentTimeSeriesChart`)**: Muestra la evolución de la cantidad de incidentes a lo largo del tiempo. El eje de tiempo (X) se ajusta dinámicamente (minutos, horas, días) según el rango de datos disponible para una mejor visualización.
-   **Sincronización con Backend**:
    -   Un **Extended FAB** aparece si hay cambios locales sin sincronizar (`state.isSync` es `false`), permitiendo al usuario enviar los datos a un servidor remoto.
    -   Un botón en la `TopAppBar` permite "descargar" o "restaurar" los datos desde el backend, reemplazando la información local.

### Otras Pantallas

-   **Favoritos (`FavoritesScreen`)**: Esta pantalla sirve principalmente para fines de depuración y demostración. Contiene botones para generar y registrar manualmente incidentes de cada uno de los cinco tipos de severidad. Al abrirse por primera vez, también se registra a sí misma como una "pantalla" en la base de datos.
-   **Perfil (`ProfileScreen`), Usuarios (`UsersScreen`) e Información (`InfoScreen`)**: Actualmente, son pantallas de marcador de posición (`placeholder`). Sirven como parte de la estructura de navegación de la aplicación, pero no contienen funcionalidades complejas más allá de mostrar su título.