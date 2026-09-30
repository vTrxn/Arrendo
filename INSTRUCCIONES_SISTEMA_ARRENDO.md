# GUÍA MAESTRA DE DESPLIEGUE Y SUSTENTACIÓN - SISTEMA ARRENDO
**Fundación Universitaria Empresarial de la Cámara de Comercio de Bogotá (Uniempresarial)**  
**Facultad de Ingeniería de Software | Arquitectura Distribuida y Móvil (Norma IEEE 830)**  
**Bogotá D.C., Colombia**

---

## 📋 RESUMEN DE LA MEMORIA USB (TODO LISTO PARA LLEVAR)

Esta memoria USB contiene todo el ecosistema del proyecto **Arrendo**. Los scripts de la USB preparan la infraestructura (servidor, base de datos y enlace de red), y la aplicación móvil se abre y ejecuta directamente desde **Android Studio** hacia el celular Android de tu compañero:

| Archivo / Carpeta en la USB | Propósito |
| :--- | :--- |
| `INICIAR_TODO_ARRENDO.bat` | **EL INICIALIZADOR MAESTRO (1 Clic).** Inicia PostgreSQL, levanta el Backend en puerto 8080, activa el túnel ADB para el celular, valida el token JWT y abre Swagger. |
| `DETENER_TODO_ARRENDO.bat` | Apaga limpiamente el servidor, libera el puerto 8080 y detiene la base de datos. |
| `ArrendoAndroid/` | **Proyecto Android nativo completo** (Kotlin + Jetpack + Room SQLite + Volley). Listo para abrir en Android Studio y dar Run (▶). |
| `ArrendoBackendEclipse/` | Servidor Spring Boot 3 con el ejecutable Fat JAR precompilado `target/arrendo-backend-eclipse-1.0.0.jar`. |
| `pgsql/` | Motor portable de PostgreSQL 16 con la base de datos `arrendo_db` preconfigurada (no requiere instalación previa). |
| `Arrendo_Postman_Collection.json` | **Colección Postman oficial** (v2.1.0) con todos los 9 endpoints REST documentados y listos para probar. |
| `GUIA_DEFINITIVA_ARQUITECTURA_ARRENDO.docx` | Documento formal IEEE 830 de sustentación técnica. |
| `INSTRUCCIONES_SISTEMA_ARRENDO.md` | Este manual detallado. |

---

## 🚀 PASO A PASO EN EL SALÓN: LLEGAR Y MONTAR TODO

### Paso 1: Conectar la Memoria USB y el Celular al PC
1. Conecta la memoria USB al computador del salón.
2. Abre el **Explorador de Archivos** (`Tecla Windows + E`) y entra a tu memoria USB.
3. Conecta el celular Android de tu compañero al computador mediante el cable USB.

---

### Paso 2: Iniciar la Infraestructura con 1 Solo Clic
1. Haz **doble clic sobre:** `INICIAR_TODO_ARRENDO.bat` en la memoria USB.
2. El script realizará automáticamente en 5 a 8 segundos:
   - ✅ **Detección de Java:** Detecta el JDK del equipo o el Java (JBR) de Android Studio.
   - ✅ **Arranque de PostgreSQL:** Inicia el motor de base de datos portable desde `pgsql` y verifica la base de datos `arrendo_db`.
   - ✅ **Túnel de Red ADB:** Activa silenciosamente el redireccionamiento de puertos:
     ```bash
     adb reverse tcp:8080 tcp:8080
     ```
     *(Esto permite que la app en el celular acceda a `http://localhost:8080/api` directamente por el cable USB sin configurar IPs ni depender del WiFi del salón).*
   - ✅ **Arranque del Backend Spring Boot:** Lanza el JAR compilado en `http://localhost:8080/api`.
   - ✅ **Prueba de Salud y Token JWT:** Valida las credenciales de administrador (`admin / admin123`) y comprueba la emisión del token JWT.
   - ✅ **Apertura de Swagger UI:** Abre en el navegador la documentación interactiva:
     `http://localhost:8080/api/swagger-ui/index.html`

---

### Paso 3: Abrir y Ejecutar la App desde Android Studio
1. Abre **Android Studio** en el computador.
2. Haz clic en **File > Open...** (o **Open** en la pantalla de bienvenida).
3. Selecciona la carpeta **`ArrendoAndroid`** que está en tu memoria USB.
4. Espera 10-20 segundos a que Gradle sincronice las dependencias (el script ya configuró el `local.properties` para el SDK de ese equipo).
5. En la barra superior de Android Studio, asegúrate de que esté seleccionado el celular Android de tu compañero en el menú de dispositivos.
6. Haz clic en el botón verde **Run (▶)** (o presiona `Shift + F10`).
7. Android Studio compilará y desplegará la app directamente en la pantalla del celular.

---

### Paso 4: Demostración de Sincronización en Tiempo Real con Postman

Para sustentar ante el docente la sincronización bidireccional y la arquitectura del sistema:

1. **Importar la Colección en Postman:**
   - Abre Postman en el PC.
   - Haz clic en **Import** y selecciona el archivo `Arrendo_Postman_Collection.json` ubicado en la raíz de tu USB.
2. **Ejecutar las Peticiones:**
   - **Login Admin:** Ejecuta `POST /v1/auth/login` (dentro de `1. Autenticacion`) para obtener el token JWT.
   - **Listar Inmuebles:** Ejecuta `GET /v1/properties` (dentro de `2. Inmuebles`) para ver los inmuebles de la base de datos PostgreSQL.
3. **Demostración de Sincronización en Vivo:**
   - **De Postman hacia el Celular:**
     - En Postman, ejecuta `Crear Inmueble (POST)` agregando un nuevo inmueble (por ejemplo, *"Penthouse Salón 302"*).
     - En el celular, entra al módulo de **Inmuebles** o refresca: el nuevo inmueble aparecerá de inmediato en la pantalla.
   - **Del Celular hacia Postman:**
     - En el celular, registra un nuevo inquilino en el módulo de **Arrendatarios** o un nuevo inmueble.
     - En Postman, ejecuta `Listar Inquilinos (GET)` o `Listar Inmuebles (GET)`: el registro creado en el celular aparecerá de inmediato en la respuesta de Postman con su ID asignado en PostgreSQL.

---

### 🎯 Paso 5: Puntos Clave para la Sustentación ante el Docente

1. **Patrón Offline-First:** Explica que la app móvil almacena los datos de forma inmediata en la base de datos local **Room SQLite**. Luego, un servicio asíncrono con **Volley** y **Gson** sincroniza con el backend Spring Boot y PostgreSQL.
2. **Seguridad JWT:** El login emite un token firmado con HMAC-SHA256 con contraseñas encriptadas mediante **BCrypt**.
3. **Hardware y APIs Nativas:**
   - **Contactos:** En el módulo de Inquilinos, pulsa "SELECCIONAR CONTACTO" (uso de `ContactsContract` y `Cursor` cerrado con `.use {}`).
   - **Cámara:** En Mantenimientos, pulsa "TOMAR FOTO CON CÁMARA" (uso de `FileProvider` con URIs seguras `content://` para prevenir excepciones `FileUriExposedException`).

---

### 🛑 Paso 6: Apagar Todo al Terminar
1. Haz **doble clic sobre:** `DETENER_TODO_ARRENDO.bat` en la memoria USB.
2. El script detendrá el servidor Spring Boot, liberará el puerto 8080 y cerrará PostgreSQL cleanly.
3. Expulsa la memoria USB de forma segura.
