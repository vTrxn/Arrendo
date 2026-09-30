// package: Capa de Controladores REST para Autenticación de Usuarios (RF01)
package co.edu.ue.controller;

// import: DTO de respuesta que encapsula el Token JWT generado y el username
import co.edu.ue.dto.RespuestaLogin;
// import: DTO de solicitud que recibe el usuario y la clave en texto plano
import co.edu.ue.dto.SolicitudLogin;
// import: Entidad Usuario de JPA mapeada a la tabla 'usuarios'
import co.edu.ue.model.Usuario;
// import: Repositorio JPA con métodos para consultar usuarios por username
import co.edu.ue.repository.RepositorioUsuario;
// import: Componente utilitario para generar tokens JWT
import co.edu.ue.security.JwtUtils;
// import: Documentación OpenAPI/Swagger para describir el endpoint
import io.swagger.v3.oas.annotations.Operation;
// import: Agrupador de Swagger para categorizar el controlador
import io.swagger.v3.oas.annotations.tags.Tag;
// import: Inyección de dependencias de Spring
import org.springframework.beans.factory.annotation.Autowired;
// import: Envoltorio de respuesta HTTP con código de estado y cuerpo
import org.springframework.http.ResponseEntity;
// import: Interfaz para verificar y codificar hashes BCrypt
import org.springframework.security.crypto.password.PasswordEncoder;
// import: Anotaciones de Spring MVC para endpoints REST (@RestController, @RequestMapping, @PostMapping)
import org.springframework.web.bind.annotation.*;

// @RestController: Combina @Controller y @ResponseBody para retornar JSON automáticamente
@RestController
// @RequestMapping("/v1/auth"): Prefijo de ruta base para servicios de autenticación
@RequestMapping("/v1/auth")
// @Tag: Documentación visual para la interfaz de Swagger UI
@Tag(name = "1. Autenticación JWT (RF01)", description = "Servicios REST para inicio de sesión con contraseñas encriptadas en BCrypt y Tokens JWT")
public class AuthController {

    // Repositorio JPA para consultar la tabla 'usuarios' en PostgreSQL
    @Autowired
    private RepositorioUsuario repositorioUsuario;

    // Componente para emitir Tokens JWT firmados criptográficamente
    @Autowired
    private JwtUtils jwtUtils;

    // Encriptador BCrypt para verificar contraseñas contra hashes almacenados
    @Autowired
    private PasswordEncoder passwordEncoder;

    // @PostMapping("/login"): Endpoint HTTP POST para autenticación de credenciales
    @PostMapping("/login")
    @Operation(summary = "Inicio de Sesión", description = "Valida las credenciales hasheadas con BCrypt y retorna el Token JWT de acceso")
    public ResponseEntity<?> login(@RequestBody SolicitudLogin solicitud) {
        // Busca en PostgreSQL el usuario por su nombre de usuario
        var usuarioOpt = repositorioUsuario.findByNombreUsuario(solicitud.getUsuario());
        // Si el usuario existe en la base de datos
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            // Compara la clave ingresada con el hash BCrypt guardado en la base de datos
            if (passwordEncoder.matches(solicitud.getClave(), u.getClaveEncriptada())) {
                // Genera el Token JWT firmado para el usuario autenticado
                String token = jwtUtils.generarToken(u.getNombreUsuario());
                // Retorna HTTP 200 OK con el DTO que contiene el token y el username
                return ResponseEntity.ok(new RespuestaLogin(token, u.getNombreUsuario()));
            }
        }
        // Si las credenciales no coinciden, retorna HTTP 401 Unauthorized
        return ResponseEntity.status(401).body("Credenciales de acceso inválidas");
    }

    // @PostMapping("/register"): Endpoint HTTP POST para registrar nuevos administradores
    @PostMapping("/register")
    @Operation(summary = "Registro de Administrador", description = "Inscribe un nuevo administrador encriptando la contraseña con BCrypt")
    public ResponseEntity<?> register(@RequestBody SolicitudLogin solicitud) {
        // Verifica si el nombre de usuario ya está ocupado en PostgreSQL
        if (repositorioUsuario.findByNombreUsuario(solicitud.getUsuario()).isPresent()) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }
        // Crea la entidad Usuario hasheando la contraseña con BCrypt antes de persistir
        Usuario nuevo = new Usuario(solicitud.getUsuario(), passwordEncoder.encode(solicitud.getClave()), "ADMIN");
        // Guarda el nuevo usuario en PostgreSQL
        repositorioUsuario.save(nuevo);
        // Emite inmediatamente el Token JWT para iniciar sesión de forma automática
        String token = jwtUtils.generarToken(nuevo.getNombreUsuario());
        return ResponseEntity.ok(new RespuestaLogin(token, nuevo.getNombreUsuario()));
    }
}