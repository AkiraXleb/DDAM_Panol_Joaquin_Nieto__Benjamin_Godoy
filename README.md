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
- **Backend**: tres microservicios Spring Boot (Java 21) + PostgreSQL con Docker Compose en **AWS EC2** (Elastic IP `32.197.25.36`).
  - `equipos-service` → `:8081`
  - `solicitudes-service` → `:8082`
  - `usuarios-service` → `:8083` (login y administración de usuarios)
  - `img-service` → `:8084` (imágenes de los equipos; el TI las sube desde Inventario y se ven en Catálogo, Detalle y Carrito)
- **Room**: sesión iniciada (se guarda el usuario conectado) y carrito local.
- Diagramas en la rama `documents`: `docs/arquitectura_app.jpg` y `docs/microservicios.png`.

Backend: https://github.com/AkiraXleb/DDAM_Panol_Backend_Joaquin_Nieto__Benjamin_Godoy

## Tecnologías

Kotlin, Jetpack Compose (Material 3), Navigation Compose, Room, Retrofit + OkHttp + Gson, ViewModel con mutableStateOf, Coroutines; JUnit/Mockito en el backend.

## Roles y funcionalidades

**Docente / solicitante**
- Inicio de sesión validado contra el servidor; la sesión queda guardada y no se vuelve a pedir hasta cerrar sesión.
- Catálogo con stock y *pull-to-refresh*.
- Detalle, carrito y formulario de solicitud (validación de fecha, hora, cantidad y motivo; el inicio debe ser al menos 3 horas después de ahora).
- Seguimiento de solicitudes (Pendiente, Aceptada, Rechazada, Cancelada, Entregada, Devuelta).
- Perfil con foto.

**Encargado TI**
- Consola de Gestión: aceptar/rechazar, marcar entregada y devuelta (con sonido de aprobación).
- Inventario: agregar equipos nuevos y eliminar los que no tengan unidades en préstamo.

**Super Admin**
- Usuarios: agregar y eliminar usuarios con correo `prefijo.nombre@escuelamarcelapaz.cl`.

**Todos los roles**
- Perfil: cambiar la foto de perfil desde la cámara o la galería (se guarda en el teléfono).

El rol se determina por el cargo del usuario, que sale del prefijo del correo (`ti.` abre la consola de gestión, `admin.` la de usuarios).

## Recursos nativos

- **Vibración**: reglas del formulario violadas y acciones de gestión del Encargado TI.
- **Linterna**: al abrir *Mis Solicitudes* parpadea 2 veces si hay solicitudes aceptadas y 1 si fueron rechazadas.

Código en `ui/screen/RecursosNativos.kt`.

## Usuarios de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Docente | prof.patricia@escuelamarcelapaz.cl | docente123 |
| Inspector | insp.marcelo@escuelamarcelapaz.cl | inspector123 |
| Encargado TI | ti.roberto@escuelamarcelapaz.cl | admin123 |
| Super Admin | admin.panol@escuelamarcelapaz.cl | superadmin123 |

Los usuarios viven en el servidor (`usuarios-service`) y se cargan al arrancar si la tabla está vacía.
Todos los correos siguen el formato `prefijo.nombre@escuelamarcelapaz.cl` y el prefijo define el cargo:
`prof.` Docente · `insp.` Inspector · `ti.` Encargado TI · `admin.` Super Admin.
El Super Admin agrega y elimina usuarios desde la pantalla *Usuarios*.

## Cómo ejecutar

1. Abrir en Android Studio y sincronizar Gradle.
2. Ejecutar en emulador o dispositivo Android 8+ con internet.
3. URLs del servidor en `data/remote/ApiConfig.kt`.
4. **El servidor AWS solo responde mientras el AWS Academy Learner Lab esté iniciado.**

APK firmado: pestaña **Releases** (v2.0.0). Pruebas: `./gradlew test`.

## Git Flow

`main` (entrega) · `develop` (integración) · `feature/*` (PR a develop) · `documents` (diagramas).
