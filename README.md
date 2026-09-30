# Sistema Integral de Gestión Inmobiliaria - ARRENDO
**Fundación Universitaria Empresarial de la Cámara de Comercio de Bogotá (Uniempresarial)**  
**Facultad de Ingeniería de Software | Arquitectura de Software y Sistemas Distribuidos (Norma IEEE 830)**  
**Bogotá D.C., Colombia - 2026**

---

## Resumen del Proyecto

**ARRENDO** es una plataforma corporativa distribuida para la administración integral de contratos de arrendamiento, inmuebles, inquilinos y control de incidencias de mantenimiento. 

Implementa una **arquitectura en capas desacoplada** con:
1. **Cliente Móvil Nativo Android (Kotlin):** Arquitectura *Offline-First* con base de datos local SQLite mediante **Room**, sincronización reactiva con **Kotlin Coroutines / Flow**, comunicación REST asíncrona mediante **Volley**, e integración con hardware nativo (Cámara con `FileProvider` y Contactos nativos con `ContactsContract`).
2. **Servidor Backend REST (Spring Boot 3 + Java 21):** Controladores REST, capa de servicios transaccionales ACID, persistencia con **Spring Data JPA / Hibernate**, documentación viva interactiva con **OpenAPI 3 / Swagger UI** y seguridad basada en **Tokens JWT** (HMAC-SHA256) con encriptación **BCrypt**.
3. **Base de Datos en la Nube (Supabase / PostgreSQL 17):** Almacenamiento relacional centralizado de alta disponibilidad con integridad referencial y llaves foráneas.
4. **Despliegue Continuo en la Nube (Render.com / Docker):** Despliegue automático mediante contenedores Docker con compilación multi-etapa (*multi-stage build*).

---

## Estructura del Repositorio

```
Arrendo/
├── backend/                         # Microservicio REST Spring Boot 3
│   ├── src/main/java/co/edu/ue/...  # Controladores, Entidades, Repositorios, Servicios y Seguridad JWT
│   ├── src/main/resources/          # application.properties (Supabase Cloud + Local)
│   ├── Dockerfile                   # Contenedor optimizado Eclipse Temurin 21
│   └── pom.xml                      # Dependencias Maven (Spring Data, Security, JWT, Postgres, Swagger)
│
├── android/                         # Cliente Móvil Nativo Android
│   ├── app/src/main/java/...        # UI Fragments, ViewModels, Room Entities/DAOs, Volley Client
│   ├── app/src/main/res/            # Layouts XML Material Design, Navegación y Drawables
│   ├── build.gradle.kts             # Configuración Gradle con SDK 35
│   └── gradle/                      # Gradle Wrapper oficial
│
├── render.yaml                      # Blueprint de infraestructura como código para Render.com
├── Dockerfile                       # Dockerfile raíz para despliegue automático en la nube
├── Arrendo_Postman_Collection.json  # Colección Postman v2.1.0 con todos los endpoints REST
├── INSTRUCCIONES_SISTEMA_ARRENDO.md # Guía paso a paso de sustentación
└── GUIA_DEFINITIVA_ARQUITECTURA.docx# Documento IEEE 830 formal para entrega
```

---

## Despliegue Gratuito en la Nube (Render + Supabase)

### 1. Base de Datos en Supabase (Ya Configurada y Activa)
- **Proveedor:** [Supabase](https://supabase.com) (PostgreSQL en la Nube)
- **Host de Conexión:** `aws-0-us-east-2.pooler.supabase.com:5432`
- **Base de Datos:** `postgres`
- **Tablas:** `usuarios`, `inmuebles`, `arrendatarios`, `contratos`, `mantenimientos`.
- **Credenciales Maestras:**
  - Usuario Administrador: `admin`
  - Contraseña: `admin123`

### 2. Despliegue del Backend en Render.com (1 Clic)
1. Entra a [Render.com](https://render.com) e inicia sesión con tu cuenta de **GitHub**.
2. Haz clic en **New + > Web Service**.
3. Selecciona tu repositorio: `vTrxn/Arrendo`.
4. Render detectará automáticamente el archivo `render.yaml` o `Dockerfile`.
   - **Environment:** Docker
   - **Region:** Ohio (US East)
   - **Plan:** Free
5. Haz clic en **Create Web Service**.
6. En 2-3 minutos, Render compilará el código y te entregará una URL pública segura con HTTPS (ejemplo: `https://arrendo-backend.onrender.com/api`).
7. Podrás acceder a Swagger UI directamente en:
   `https://tu-servicio.onrender.com/api/swagger-ui/index.html`

---

## Ejecución de la App Móvil en Android Studio

1. Abre **Android Studio**.
2. Selecciona **File > Open...** y abre la carpeta `android/` de este repositorio.
3. Espera a que Gradle descargue las dependencias.
4. Si vas a usar el backend en la nube (Render), configura la URL en `android/app/src/main/java/co/edu/ue/data/remote/api/clienteVolley.kt`:
   ```kotlin
   var URL_BASE = "https://tu-servicio.onrender.com/api/"
   ```
5. Si vas a usar el backend local en tu PC por cable USB, deja la URL por defecto:
   ```kotlin
   var URL_BASE = "http://localhost:8080/api/"
   ```
   *(Y ejecuta en el PC: `adb reverse tcp:8080 tcp:8080`)*.
6. Conecta el celular Android físico al computador con depuración USB activada.
7. Haz clic en el botón verde **Run** (`Shift + F10`).

---

## Pruebas con Postman (Arrendo_Postman_Collection.json)

El repositorio incluye la colección de Postman preconfigurada:
1. Abre Postman y pulsa **Import**.
2. Selecciona el archivo `Arrendo_Postman_Collection.json`.
3. Peticiones principales disponibles:
   - `1. Auth - Login Admin (POST /v1/auth/login)`: Obtiene el token JWT.
   - `2. Inmuebles - Listar Inmuebles (GET /v1/properties)`.
   - `2. Inmuebles - Crear Inmueble (POST /v1/properties)`.
   - `3. Inquilinos - Listar Inquilinos (GET /v1/tenants)`.
   - `4. Contratos - Listar Contratos (GET /v1/contracts)`.
   - `5. Mantenimientos - Listar Incidencias (GET /v1/maintenances)`.

---

## Seguridad y Autenticación JWT

El sistema cuenta con un filtro interceptor `JwtAuthorizationFilter` en Spring Security. Todas las rutas bajo `/api/v1/**` requieren la cabecera HTTP:
```http
Authorization: Bearer <TOKEN_JWT>
```
La ruta pública exenta de autenticación es `/api/v1/auth/login` y las rutas de Swagger UI.
