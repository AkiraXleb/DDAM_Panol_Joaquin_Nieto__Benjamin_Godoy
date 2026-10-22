# MPAZ Pañol — Sistema móvil de préstamo de equipos tecnológicos

Aplicación Android para la **Escuela Escritora Marcela Paz** (San Bernardo). Digitaliza la reserva y el préstamo de equipos del pañol (notebooks, proyectores, parlantes, kits de robótica, equipos de red, etc.), que antes se pedían por correo y sin registro.

Esta entrega  implementa el flujo completo del **perfil Solicitante** (docentes, inspectoría, UTP y dirección). La consola del encargado TI queda para la siguiente etapa.

> **Asignatura:** Desarrollo de Aplicaciones Móviles, DuocUC Sede Maipú
> Todos los datos de la app son **ficticios**.

## Integrantes

| Integrante | GitHub | Responsabilidades |
|---|---|---|
| **Joaquín Nieto** | [AkiraXleb](https://github.com/AkiraXleb) | Capa de datos (Room: entidades, DAOs, base de datos, datos iniciales), Login, Catálogo, Detalle de equipo, Perfil, componentes reutilizables (Header, BarraInferior, ChipEstado, MensajeError), tema visual y navegación base |
| **Benjamín Godoy** | [benjaxleb](https://github.com/benjaxleb) | Carrito de solicitud, Formulario con regla de 3 horas, Confirmación, Mis Solicitudes y recursos nativos (vibración + linterna) |

## Tecnologías

- **Kotlin** + **Jetpack Compose** + **Material Design 3**
- Arquitectura **MVVM** (Model · ViewModel · View)
- Persistencia local con **SQLite a través de Room** (KSP)
- **Navigation Compose** para la navegación entre pantallas
- Android Studio · `minSdk 26` · `targetSdk 37`
- Git / GitHub con **Git Flow** y planificación en **Trello**

## Funcionalidades implementadas

| Pantalla | Funcionalidad |
|---|---|
| **Login** | Ingreso con correo institucional y contraseña validados contra Room. Error por campo con ícono y animación de temblor. |
| **Catálogo** | Lista de equipos con búsqueda, filtro por categoría, chip de estado con color (Disponible, Reservado, Prestado, En Mantenimiento, Fuera de Servicio) y stock disponible. Botón para agregar al carrito, deshabilitado si no hay stock. |
| **Detalle** | Datos completos del equipo (código, marca, modelo, ubicación y stock) y selector de cantidad validado (mínimo 1, máximo 3 por docente y nunca más que el stock). |
| **Carrito** | Borrador del préstamo guardado en Room: cambiar cantidades, eliminar con confirmación y resumen de equipos y unidades. |
| **Formulario de solicitud** | Sala (menú desplegable), fecha (DatePicker), hora de inicio y de devolución (TimePicker), asignatura y observaciones. **Regla crítica: mínimo 3 horas de anticipación**. Si no se cumple, se muestra una advertencia y se bloquea el envío. Diálogo de confirmación antes de enviar. |
| **Confirmación** | N° de solicitud, estado Pendiente, datos del solicitante, sala, horario y equipos, con animación de pulso. |
| **Mis Solicitudes** | Seguimiento de las solicitudes con estado (Pendiente, Aceptada, Rechazada, Entregada, Devuelta, Cancelada), filtros, motivo de rechazo y cancelación de solicitudes pendientes. |
| **Perfil** | Datos del usuario y cierre de sesión con confirmación. |

### Formularios y validaciones
- Toda la validación está en los **ViewModel**, desacoplada de las pantallas.
- Cada campo muestra su propio error (`isError` + mensaje con ícono).
- Al intentar enviar con errores, el formulario **tiembla** (animación con `Animatable`).

### Animaciones
- Temblor del formulario ante errores (Login y Formulario).
- Mensajes de error y advertencia de 3 horas con `AnimatedVisibility`.
- Pulso del check en la Confirmación.
- Temblor de la tarjeta de una solicitud cuando llega su resultado (`rememberInfiniteTransition`).

### Recursos nativos
| Recurso | Uso en el caso |
|---|---|
| **Vibración** | Avisa el resultado de una solicitud y vibra corto cuando la hora rompe la regla de 3 horas. |
| **Linterna** (LED del flash) | Parpadea **2 veces si la solicitud fue aceptada** y **1 vez si fue rechazada**, junto con la vibración, al abrir Mis Solicitudes. Usa `setTorchMode`, sin cámara fotográfica ni permiso `CAMERA`. |

Las señales quedan registradas en **Logcat** (filtro `RecursosNativos`), así se pueden revisar también en el emulador.

## Estructura del proyecto

```
app/src/main/java/com/example/panol/
├── data/
│   ├── Constantes.kt          # estados, categorías, salas y reglas del caso
│   ├── DatosIniciales.kt      # usuarios, equipos y solicitudes ficticias
│   ├── Sesion.kt              # usuario conectado e ids entre pantallas
│   ├── database/AppDatabase.kt
│   └── local/
│       ├── dao/               # UsuarioDao, EquipoDao, CarritoDao, SolicitudDao
│       └── entity/            # Usuario, Equipo, CarritoItem, Solicitud, DetalleSolicitud
├── ui/
│   ├── components/            # Header, BarraInferior, ChipEstado, MensajeError
│   ├── model/                 # estado de cada pantalla (data class)
│   ├── viewmodel/             # lógica y validaciones de cada pantalla
│   ├── screen/                # pantallas Compose y RecursosNativos
│   ├── navigation/            # AppNavigation y Routes
│   └── theme/                 # colores del mockup
└── MainActivity.kt
```

## Usuarios de prueba (ficticios)

| Correo | Contraseña | Cargo |
|---|---|---|
| `profesora.patricia@escuelamarcelapaz.cl` | `docente123` | Docente (con solicitudes de ejemplo) |
| `inspector.marcelo@escuelamarcelapaz.cl` | `inspector123` | Inspector |
| `ti.roberto@escuelamarcelapaz.cl` | `admin123` | Encargado TI (siguiente etapa) |

## Cómo ejecutar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/AkiraXleb/DDAM_Panol_Joaquin_Nieto__Benjamin_Godoy.git
   ```
2. Abrir la carpeta en **Android Studio** (*File → Open*) y esperar a que termine el *Gradle Sync*.
3. Ejecutar en un emulador o dispositivo con **Android 8.0 (API 26) o superior**.
4. Iniciar sesión con uno de los usuarios de prueba.
5. Para ver los recursos nativos: abrir **Solicitudes** la primera vez (hay una solicitud aceptada y una rechazada sin notificar) y revisar Logcat con el filtro `RecursosNativos`. En un dispositivo físico se ve la linterna y se siente la vibración.

> Si se cambia alguna entidad de Room, hay que desinstalar la app antes de volver a ejecutarla.

## Flujo de trabajo (Git Flow)

- `main`: versión entregada (tag `v1.0`).
- `develop`: integración.
- `feature/backend/<integrante>` y `feature/frontend/<integrante>`: trabajo de cada integrante por capa.
- `release/v1.0`: preparación de la entrega.
