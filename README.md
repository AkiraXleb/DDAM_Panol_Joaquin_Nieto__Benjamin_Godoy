# MPAZ Pañol

Aplicación Android para gestionar el préstamo de equipos del **Pañol de la Escuela Marcela Paz**.
Los docentes solicitan equipos desde su teléfono y el Encargado TI revisa, acepta, entrega y recibe las devoluciones.

Proyecto de **DSY1105 – Desarrollo de Aplicaciones Móviles** (DuocUC Maipú, sección 010D) – Experiencia 3.

## Integrantes

| Integrante | Aporte principal |
|---|---|
| **Joaquín Nieto** | Microservicio `solicitudes-service`, despliegue en AWS, capa Retrofit + `PanolRepository`, ViewModels, Login/Catálogo/Detalle/Perfil, mejoras visuales e ícono |
| **Benjamín Godoy** | Microservicio `equipos-service`, rol Encargado TI (Gestión), Carrito, Formulario, Confirmación, Mis Solicitudes y recursos nativos |

## Arquitectura

- **App Android**: Kotlin + Jetpack Compose, patrón **MVVM** (Screen → ViewModel → Repository → API / Room).
- **Backend**: dos microservicios Spring Boot (Java 21) + PostgreSQL con Docker Compose en **AWS EC2** (Elastic IP `32.197.25.36`).
  - `equipos-service` → `:8081`
  - `solicitudes-service` → `:8082`
- **Room**: solo usuarios (login) y carrito local.
- Diagramas en la rama `documents`: `docs/arquitectura_app.jpg` y `docs/microservicios.png`.

Backend: https://github.com/AkiraXleb/DDAM_Panol_Backend_Joaquin_Nieto__Benjamin_Godoy

## Tecnologías

Kotlin, Jetpack Compose (Material 3), Navigation Compose, Room, Retrofit + OkHttp + Gson, ViewModel/StateFlow, Coroutines; JUnit/Mockito en el backend.

## Roles y funcionalidades

**Docente / solicitante**
- Inicio de sesión validado.
- Catálogo con stock y *pull-to-refresh*.
- Detalle, carrito y formulario de solicitud (validación de fecha, hora, cantidad y motivo; máximo 3 horas).
- Seguimiento de solicitudes (Pendiente, Aceptada, Rechazada, Cancelada, Entregada, Devuelta).
- Perfil con foto.

**Encargado TI**
- Consola de Gestión: aceptar/rechazar, marcar entregada y devuelta.

El rol se determina por el cargo del usuario (`Encargado TI` abre la consola de gestión).

## Recursos nativos

- **Vibración**: reglas del formulario violadas y acciones de gestión del Encargado TI.
- **Linterna**: al abrir *Mis Solicitudes* parpadea 2 veces si hay solicitudes aceptadas y 1 si fueron rechazadas.

Código en `ui/screen/RecursosNativos.kt`.

## Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Docente | profesora.patricia@escuelamarcelapaz.cl | docente123 |
| Inspector | inspector.marcelo@escuelamarcelapaz.cl | inspector123 |
| Encargado TI | ti.roberto@escuelamarcelapaz.cl | admin123 |

Se cargan en Room solo en la primera instalación (tabla vacía).

## Cómo ejecutar

1. Abrir en Android Studio y sincronizar Gradle.
2. Ejecutar en emulador o dispositivo Android 8+ con internet.
3. URLs del servidor en `data/remote/ApiConfig.kt`.
4. **El servidor AWS solo responde mientras el AWS Academy Learner Lab esté iniciado.**

APK firmado: pestaña **Releases** (v2.0.0). Pruebas: `./gradlew test`.

## Git Flow

`main` (entrega) · `develop` (integración) · `feature/*` (PR a develop) · `documents` (diagramas).
