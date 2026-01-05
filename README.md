# Observability App - SDK Consumer

Este proyecto es una aplicación de Android que sirve como **capa de presentación y entorno de pruebas** para un SDK de observabilidad. La aplicación está construida con Jetpack Compose y Kotlin, y su propósito principal es demostrar y validar las funcionalidades del SDK, que se encarga de monitorear y visualizar incidentes.

## El SDK de Observabilidad

El núcleo de la funcionalidad no reside en esta aplicación, sino en un conjunto de artefactos que componen el SDK. Esta aplicación es simplemente un **consumidor** de dicho SDK.

El SDK está dividido en tres módulos principales:

-   **domain**: Contiene los modelos de datos puros (ej. `Incident`, `Screen`) y las definiciones de severidad (`EIncidentSeverity`). No tiene dependencias de plataforma.
-   **data**: Implementa los repositorios y las fuentes de datos (locales y remotas). Se encarga de la lógica de la base de datos (Room) y las llamadas de red (Ktor).
-   **presentation**: Contiene la lógica de presentación a través de un ViewModel (`ContractViewModel`) que expone una interfaz pública (`ContractObservabilityApi`). Este es el punto de entrada principal para que la UI interactúe con el SDK.

## 1. Integración del SDK en la App Consumidora

Para que la aplicación funcione, los artefactos (`.aar` o `.jar`) del SDK deben ser agregados al proyecto.

### Pasos:

1.  **Copiar los Artefactos**: Dentro del módulo `app`, crea una carpeta `libs` (`app/libs/`) y copia los archivos de los artefactos del SDK en ella.

2.  **Importar en Gradle**: Declara los artefactos como dependencias en el archivo `app/build.gradle.kts` para que Gradle los incluya en el classpath del proyecto.

    ```kotlin
    // app/build.gradle.kts

    dependencies {
        // ... otras dependencias

        // Artefactos del SDK
        implementation(files("libs/domain.jar"))
        implementation(files("libs/data.aar"))
        implementation(files("libs/presentation.aar"))
    }
    ```

3.  **Sincronizar Gradle**: Sincroniza el proyecto en Android Studio para que las nuevas dependencias sean reconocidas.

## 2. Configuración de Koin para el SDK

La inyección de dependencias es gestionada por Koin. La aplicación consumidora debe configurar Koin para proveer las instancias que el SDK necesita, principalmente el `ContractObservabilityApi` (ViewModel).

### Pasos:

1.  **Crear el Módulo de Koin**: Define un módulo que declare cómo construir las dependencias del SDK. La aplicación consumidora solo necesita preocuparse por inyectar el ViewModel; las dependencias internas del SDK (repositorios, etc.) son gestionadas por los módulos de Koin que el propio SDK provee.

    ```kotlin
    // En la capa de la app, ej: di/AppModule.kt

    val appModule = module {
        viewModel<ContractObservabilityApi> { ContractViewModel(get()) }
    }
    ```

2.  **Inicializar Koin**: En la clase `Application` de la app, inicia Koin. Es crucial cargar no solo el módulo de la app, sino también los módulos internos del SDK.

    ```kotlin
    // MyApplication.kt

    class MyApplication : Application() {
        override fun onCreate() {
            super.onCreate()
            startKoin {
                androidContext(this@MyApplication)
                // Carga el módulo de la app y los módulos del SDK
                modules(appModule, dataModule, presentationModule)
            }
        }
    }
    ```

## 3. Consumo de `ContractObservabilityApi` en la UI

`ContractObservabilityApi` es la interfaz que el SDK expone para que la UI interactúe con él. Se inyecta directamente en los Composables.

-   **Inyección**: Se utiliza `koinInject()` para obtener la instancia del ViewModel proporcionada por el SDK.
-   **Estado (`state`)**: El SDK expone un `StateFlow` que contiene el estado de la UI (`state.isLoading`, `state.incidentsQuantity`, etc.). La UI se recompone en respuesta a los cambios en este estado usando `collectAsStateWithLifecycle()`.
-   **Eventos (`onEvent`)**: La UI notifica al SDK sobre acciones del usuario a través de la función `onEvent`, enviando acciones predefinidas como `MainActions.SyncToRemote`.

```kotlin
@Composable
fun ObservabilityApp(api: ContractObservabilityApi = koinInject()) {
  val state by api.state.collectAsStateWithLifecycle()
  val onEvent = api::onEvent

  // La UI utiliza el 'state' del SDK para mostrar datos y 'onEvent' para enviarle acciones
  Button(onClick = { onEvent(MainActions.SyncToRemote) }) { /* ... */ }
}
```

## Funcionalidades de Prueba del SDK

### Pantalla Principal (Dashboard)

Esta pantalla es el principal campo de pruebas para las funcionalidades de visualización del SDK. Muestra los datos recopilados y permite interactuar con ellos.

-   **Filtros**: Permite probar la lógica de filtrado del SDK por pantalla, severidad y tiempo.
-   **Métricas**: Valida que los contadores de pantallas e incidentes expuestos por el SDK (`state.screensQuantity`, `state.incidentsQuantity`) se actualizan correctamente.
-   **Gráficos**: Demuestra la capacidad del SDK para procesar y agrupar datos para visualizaciones complejas, como el gráfico de torta (`SeverityPieChart`) y el de serie temporal (`IncidentTimeSeriesChart`).
-   **Sincronización**: Permite probar las acciones `SyncToRemote` y `RollbackFromRemote` del SDK, validando la interacción con el backend y la gestión del estado de sincronización (`state.isSync`).

### Otras Pantallas

-   **Favoritos (`FavoritesScreen`)**: Es una pantalla de depuración para probar la funcionalidad de **registro de incidentes** del SDK. Contiene botones que, al ser presionados, invocan `onEvent(MainActions.InsertIncident(...))` con cada uno de los niveles de severidad, permitiendo verificar que el SDK los almacena correctamente.
-   **Otras Pantallas (Perfil, Usuarios, etc.)**: Sirven para probar el registro de navegación. Cada vez que se accede a una de estas pantallas, se invoca a `onEvent(MainActions.InsertScreen(...))` para comprobar que el SDK registra correctamente las distintas pantallas de la aplicación.