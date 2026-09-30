# GUÍA EJECUTIVA DE SUSTENTACIÓN: ARQUITECTURA CLOUD, BASE DE DATOS Y GESTIÓN DEL SISTEMA ARRENDO

**Fundación Universitaria Empresarial de la Cámara de Comercio de Bogotá (Uniempresarial)**  
**Facultad de Ingeniería de Software | Arquitectura de Software y Sistemas Distribuidos (Norma IEEE 830)**  
**Bogotá D.C., Colombia**

---

## 1. ENLACES OFICIALES DEL ECOSISTEMA

* **Repositorio de Código Fuente (GitHub):** https://github.com/vTrxn/Arrendo
* **Servidor de Aplicaciones REST (Render Cloud):** https://arrendo-backend.onrender.com/api/
* **Documentación Interactiva (Swagger UI):** https://arrendo-backend.onrender.com/api/swagger-ui/index.html
* **Consola de Base de Datos (Supabase Cloud):** https://supabase.com/dashboard/project/xiejuueuxqheyiawxffz
* **Credenciales de Acceso Inicial:**
  * Usuario: `admin`
  * Contraseña: `admin123`

---

## 2. ARQUITECTURA DE LA BASE DE DATOS (SUPABASE POSTGRESQL 17)

La capa de persistencia utiliza un motor relacional **PostgreSQL versión 17** alojado en la infraestructura de AWS (Región us-east-2) gestionado por Supabase con conexión cifrada SSL y pool de conexiones HikariCP.

### Modelo Entidad-Relación y Tablas

1. **`usuarios`**: Almacena las cuentas administrativas con credenciales protegidas.
   * `id` (BIGINT, PK, Autoincremental): Identificador único del usuario.
   * `nombre_usuario` (VARCHAR, Unique): Nombre de acceso para autenticación.
   * `clave_encriptada` (VARCHAR): Hash criptográfico generado mediante algoritmo BCrypt con factor de costo 10. Nunca se guarda texto plano.
   * `rol` (VARCHAR): Perfil del sistema (`ADMIN`).

2. **`inmuebles`**: Catálogo de propiedades gestionadas.
   * `id` (BIGINT, PK, Autoincremental).
   * `direccion` (VARCHAR): Ubicación física del inmueble.
   * `tipo_inmueble` (VARCHAR): Categoría (`Apartamento`, `Casa`, `Oficina`, `Penthouse`).
   * `precio_arriendo` (DOUBLE PRECISION): Canon mensual en pesos colombianos.
   * `estado` (VARCHAR): Disponibilidad (`DISPONIBLE`, `ARRENDADO`, `MANTENIMIENTO`).
   * `uri_imagen` (VARCHAR): URL pública de la fotografía del inmueble.

3. **`arrendatarios`**: Directorio de clientes e inquilinos.
   * `id` (BIGINT, PK, Autoincremental).
   * `nombre_completo` (VARCHAR).
   * `numero_documento` (VARCHAR): Cédula de ciudadanía o NIT.
   * `telefono` (VARCHAR): Número de contacto.
   * `correo_electronico` (VARCHAR): Email de notificación.

4. **`contratos`**: Relación contractual entre un inmueble y un arrendatario.
   * `id` (BIGINT, PK, Autoincremental).
   * `inmueble_id` (BIGINT, FK): Referencia a la tabla `inmuebles`.
   * `arrendatario_id` (BIGINT, FK): Referencia a la tabla `arrendatarios`.
   * `fecha_inicio` (VARCHAR) y `fecha_fin` (VARCHAR): Rango de vigencia.
   * `valor_mensual` (DOUBLE PRECISION): Tarifa pactada.
   * `estado_pago` (VARCHAR): Control financiero (`PENDIENTE`, `PAGADO`).

5. **`mantenimientos`**: Historial de novedades y reparaciones locativas.
   * `id` (BIGINT, PK, Autoincremental).
   * `inmueble_id` (BIGINT, FK): Propiedad afectada.
   * `descripcion` (VARCHAR): Detalle técnico del daño o trabajo.
   * `fecha_solicitud` (VARCHAR): Fecha de radicación.
   * `costo_estimado` (DOUBLE PRECISION): Presupuesto de la reparación.
   * `estado` (VARCHAR): Avance (`PENDIENTE`, `EN_PROCESO`, `COMPLETADO`).
   * `uri_foto` (VARCHAR): Evidencia fotográfica tomada con la cámara nativa.

---

## 3. GESTIÓN DE USUARIOS Y SEGURIDAD JWT (AGREGAR, AUTENTICAR Y ELIMINAR)

El sistema implementa seguridad **Stateless** basada en tokens **JSON Web Tokens (JWT)** y contraseñas hasheadas con **BCrypt**:

### A. Autenticar Usuario (Login)
* **Ruta:** `POST /api/v1/auth/login`
* **Cuerpo JSON:**
  ```json
  {
    "usuario": "admin",
    "clave": "admin123"
  }
  ```
* **Mecanismo:** Spring Security consulta la tabla `usuarios` por `nombre_usuario`, ejecuta `passwordEncoder.matches()` para validar la clave contra el hash BCrypt y genera un Token firmado con algoritmo HMAC-SHA256 con validez de 24 horas.

### B. Agregar un Nuevo Usuario
Existen dos métodos para registrar un nuevo usuario:
1. **Vía API REST / Swagger / Postman:**
   * **Ruta:** `POST /api/v1/auth/register`
   * **Cuerpo JSON:**
     ```json
     {
       "usuario": "nuevo_usuario",
       "clave": "claveSegura2026"
     }
     ```
   * El controlador encripta automáticamente la contraseña mediante BCrypt antes de persistirla en Supabase y retorna inmediatamente el token JWT.
2. **Vía Consola de Supabase:**
   * Ingresar al **Table Editor** en Supabase, seleccionar la tabla `usuarios` y pulsar **Insert row**.
   * Nota: Como la autenticación requiere hash BCrypt, para crearlo desde la consola de Supabase se recomienda insertar la contraseña ya hasheada o utilizar el endpoint oficial `/api/v1/auth/register`.

### C. Quitar o Bloquear un Usuario
* **Desde Supabase:**
  1. Ingresar a https://supabase.com/dashboard/project/xiejuueuxqheyiawxffz
  2. Abrir **Table Editor** > tabla `usuarios`.
  3. Seleccionar la fila del usuario y hacer clic en **Delete 1 row**.
  4. De inmediato, cualquier intento de inicio de sesión con ese usuario retornará HTTP 401 Unauthorized.
* **Desde el SQL Editor de Supabase:**
  ```sql
  DELETE FROM usuarios WHERE nombre_usuario = 'usuario_a_eliminar';
  ```

---

## 4. CATÁLOGO COMPLETO DE RUTAS Y SERVICIOS REST

Todos los servicios están protegidos por el filtro `JwtAuthorizationFilter`. Exceptuando `/api/v1/auth/**` y `/swagger-ui/**`, cada petición debe incluir el encabezado HTTP:
`Authorization: Bearer <TOKEN_JWT>`

| Módulo | Método | Ruta del Endpoint | Descripción del Servicio |
| :--- | :---: | :--- | :--- |
| **Autenticación** | `POST` | `/api/v1/auth/login` | Inicia sesión y emite token JWT |
| | `POST` | `/api/v1/auth/register` | Registra nuevo usuario administrador |
| **Inmuebles** | `GET` | `/api/v1/properties` | Lista todos los inmuebles de la base de datos |
| | `GET` | `/api/v1/properties/{id}` | Consulta un inmueble por su identificador |
| | `POST` | `/api/v1/properties` | Registra una nueva propiedad |
| | `PUT` | `/api/v1/properties/{id}` | Actualiza datos o estado de un inmueble |
| | `DELETE`| `/api/v1/properties/{id}` | Elimina un inmueble |
| **Arrendatarios**| `GET` | `/api/v1/tenants` | Obtiene lista completa de inquilinos |
| | `GET` | `/api/v1/tenants/{id}` | Consulta un inquilino por su ID |
| | `POST` | `/api/v1/tenants` | Registra un nuevo arrendatario |
| | `PUT` | `/api/v1/tenants/{id}` | Modifica información de contacto |
| | `DELETE`| `/api/v1/tenants/{id}` | Elimina un inquilino |
| **Contratos** | `GET` | `/api/v1/contracts` | Consulta contratos registrados |
| | `GET` | `/api/v1/contracts/{id}` | Consulta contrato específico |
| | `POST` | `/api/v1/contracts` | Crea una vinculación contractual |
| | `PUT` | `/api/v1/contracts/{id}` | Actualiza vigencia o estado de pago |
| | `DELETE`| `/api/v1/contracts/{id}` | Anula un contrato |
| **Mantenimientos**| `GET` | `/api/v1/maintenances` | Historial de mantenimientos |
| | `GET` | `/api/v1/maintenances/{id}`| Detalle de un mantenimiento |
| | `POST` | `/api/v1/maintenances` | Radica orden con foto y costo estimado |
| | `PUT` | `/api/v1/maintenances/{id}`| Actualiza estado de ejecución |
| | `DELETE`| `/api/v1/maintenances/{id}`| Elimina registro de mantenimiento |

---

## 5. FLUJO DE DATOS EN LA APP MÓVIL (PATRÓN OFFLINE-FIRST)

1. **Capa Local (Room SQLite):** Cada vez que el usuario abre la aplicación o crea un registro, este se almacena de inmediato en la base de datos local SQLite mediante la librería Room. Esto permite navegar por la app incluso si el celular pierde señal momentáneamente.
2. **Capa de Red Asíncrona (Volley):** La clase `ClienteVolley` gestiona una cola de peticiones HTTP en segundo plano. Al crearse un registro, envía el JSON al servidor en Render (`https://arrendo-backend.onrender.com/api/`).
3. **Persistencia en la Nube:** Spring Boot valida el token JWT en `JwtAuthorizationFilter`, delega al repositorio JPA e impacta la base de datos en Supabase.
4. **Sincronización:** Cuando la app recupera la lista, Room se actualiza con los datos devueltos por el backend, manteniendo consistencia absoluta.

---

## 6. JUSTIFICACIÓN TÉCNICA: ¿POR QUÉ NUBE (RENDER + SUPABASE) EN LUGAR DE LOCAL?

Para la defensa ante el comité evaluador, estas son las razones de ingeniería por las cuales se adoptó esta arquitectura moderna:

### 1. Eliminación de Dependencias Locales y Portabilidad Absoluta
* **Problema del entorno local:** Ejecutar el sistema localmente exigía que la máquina de sustentación tuviera preinstalado Java 17+, PostgreSQL, variables de entorno configuradas (`JAVA_HOME`, `PATH`), puertos 8080 y 5432 desocupados y privilegios de administrador.
* **Ventaja Cloud:** La infraestructura se ejecuta en contenedores Docker estandarizados en Render y Supabase. El día de la presentación, la aplicación puede evaluarse desde cualquier equipo, emulador o celular sin instalar absolutamente nada en la computadora del aula.

### 2. Cumplimiento de Políticas de Seguridad de Android (HTTPS Obligatorio)
* **Problema del entorno local:** Desde Android 9.0 (API 28), el sistema operativo bloquea de manera predeterminada todo el tráfico HTTP sin cifrar (Cleartext Traffic). En entornos locales esto obliga a habilitar configuraciones inseguras de red (`usesCleartextTraffic=true`) o túneles frágiles por cable.
* **Ventaja Cloud:** Render provee certificados SSL/TLS automáticos con cifrado HTTPS de 256 bits. La app móvil consume una API segura bajo los estándares de producción de la industria de software.

### 3. Independencia de Red y Eliminación de Cables / NAT
* **Problema del entorno local:** Para conectar un celular físico a un servidor local se dependía de que ambos estuvieran en la misma red WiFi (la cual en campus universitarios suele tener aislamiento de clientes / Client Isolation que bloquea la comunicación) o de mantener un cable USB conectado ejecutando comandos de túnel ADB (`adb reverse tcp:8080 tcp:8080`).
* **Ventaja Cloud:** El celular puede utilizar **datos móviles (4G/5G)** o cualquier red WiFi pública. No se requiere cable físico una vez instalada la app, permitiendo manipular el celular libremente durante la exposición.

### 4. Alta Disponibilidad y Multi-Usuario en Tiempo Real
* **Problema del entorno local:** La base de datos y la API se apagan en el momento en que se cierra la consola o se suspende la laptop.
* **Ventaja Cloud:** La plataforma se mantiene en línea 24/7. El docente puede interactuar con Swagger UI o Postman desde su propio computador al mismo tiempo que el estudiante realiza operaciones desde el celular, observando la sincronización en tiempo real sin interferencias.

### 5. Arquitectura Orientada a Microservicios y Cloud-Native
* El diseño implementado refleja el flujo real de la industria: Repositorio en GitHub con Integración Continua (CI/CD), Despliegue Automatizado en Render y Base de Datos Gestionada (DBaaS) en Supabase. Demuestra dominio de tecnologías de despliegue en la nube más allá de un prototipo monolítico de escritorio.
