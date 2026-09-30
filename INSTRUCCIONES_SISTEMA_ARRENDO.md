# GUÍA MAESTRA DE DESPLIEGUE Y SUSTENTACIÓN - SISTEMA ARRENDO
**Fundación Universitaria Empresarial de la Cámara de Comercio de Bogotá (Uniempresarial)**  
**Facultad de Ingeniería de Software | Arquitectura Distribuida y Móvil (Norma IEEE 830)**  
**Bogotá D.C., Colombia**

---

## 1. RESUMEN DE LA MEMORIA USB

Esta memoria USB contiene los proyectos fuente y los artefactos de sustentación para el sistema **Arrendo**. La infraestructura de backend y base de datos se encuentra desplegada en la nube con alta disponibilidad, permitiendo ejecutar la aplicación móvil desde cualquier estación de trabajo o emulador sin configuraciones locales complejas:

| Directorio / Archivo | Propósito |
| :--- | :--- |
| `ArrendoAndroid/` | Proyecto Android Studio nativo (Kotlin, Jetpack, Room SQLite, Volley). Configurado para consumir el backend cloud. |
| `ArrendoBackendEclipse/` | Proyecto Spring Boot 3 (Java 21, Spring Data JPA, Spring Security, JWT, Swagger OpenAPI). |
| `Arrendo_Postman_Collection.json` | Colección Postman oficial con todos los endpoints REST configurados hacia el entorno cloud. |
| `GUIA_DEFINITIVA_ARQUITECTURA_ARRENDO.docx` | Documentación formal de arquitectura y requerimientos bajo norma IEEE 830. |
| `INSTRUCCIONES_SISTEMA_ARRENDO.md` | Guía de ejecución, pruebas y sustentación del proyecto. |
| `README.md` | Resumen técnico y especificaciones de despliegue. |

---

## 2. ENLACES Y SERVICIOS EN LA NUBE

* **Repositorio GitHub:** https://github.com/vTrxn/Arrendo
* **Backend REST API (Render):** https://arrendo-backend.onrender.com/api/
* **Documentación Interactiva Swagger UI:** https://arrendo-backend.onrender.com/api/swagger-ui/index.html
* **Base de Datos Cloud (Supabase):** PostgreSQL 17 alojado en AWS us-east-2 con cifrado SSL.
* **Credenciales de Administrador:**
  * Usuario: `admin`
  * Contraseña: `admin123`

---

## 3. PASO A PASO PARA LA EJECUCIÓN

### Paso 1: Abrir la Aplicación en Android Studio
1. Conecta la memoria USB al computador.
2. Abre **Android Studio**.
3. Selecciona **File > Open...** y abre la carpeta `ArrendoAndroid` ubicada en la raíz de la USB (o cópiala al disco local si prefieres mayor velocidad de compilación).
4. Espera a que Gradle termine de sincronizar las dependencias.

### Paso 2: Ejecutar en Emulador o Celular Físico
1. En la barra superior de Android Studio, selecciona el dispositivo de destino:
   * **Emulador de Android Studio** (Cualquier AVD con Android 8.0 o superior).
   * **Celular Físico Android** conectado mediante cable USB con Depuración USB habilitada.
2. Haz clic en el botón **Run (▶)** o presiona `Shift + F10`.
3. Inicia sesión en la aplicación con las credenciales de administrador (`admin` / `admin123`).

---

## 4. DEMOSTRACIÓN DE SINCRONIZACIÓN EN TIEMPO REAL

Para sustentar la persistencia distribuida y la comunicación cliente-servidor:

1. **Importar la Colección en Postman:**
   * Abre Postman y haz clic en **Import**.
   * Selecciona `Arrendo_Postman_Collection.json`.
   * La variable `baseUrl` ya apunta a `https://arrendo-backend.onrender.com/api`.
2. **Autenticación:**
   * Ejecuta `POST /v1/auth/login` para recibir el token JWT y validar la seguridad BCrypt.
3. **Sincronización Bidireccional:**
   * **Desde la App Móvil:** Registra un nuevo arrendatario o marca un contrato como pagado. Al consultar el endpoint correspondiente en Postman o en el Table Editor de Supabase, los cambios aparecen inmediatamente.
   * **Desde Postman o Supabase:** Crea un inmueble mediante `POST /v1/properties`. Al ingresar al módulo de Inmuebles en la app móvil, el nuevo elemento se carga de inmediato desde el servidor.

---

## 5. PUNTOS CLAVE PARA LA EVALUACIÓN TÉCNICA

* **Arquitectura Offline-First:** La aplicación móvil almacena información en base de datos local **Room SQLite** para garantizar operatividad sin conexión, sincronizando asíncronamente con el backend mediante **Volley** y **Gson**.
* **Seguridad y Criptografía:** Autenticación basada en **JWT** (HMAC-SHA256) con contraseñas encriptadas mediante **BCrypt**.
* **Integración con Hardware Nativo:**
  * Contactos del sistema mediante `ContactsContract` para selección de inquilinos.
  * Cámara fotográfica mediante `FileProvider` con URIs seguras (`content://`) para registro de mantenimientos.
