// package: Capa de seguridad para interceptación y validación de cabeceras HTTP Authorization
package co.edu.ue.security;

// import: Cadena de filtros de servlets de Jakarta EE
import jakarta.servlet.FilterChain;
// import: Excepción estándar de servlets
import jakarta.servlet.ServletException;
// import: Petición HTTP recibida desde el cliente Android o Swagger
import jakarta.servlet.http.HttpServletRequest;
// import: Respuesta HTTP que se enviará de vuelta al cliente
import jakarta.servlet.http.HttpServletResponse;
// import: Inyección de dependencias de Spring
import org.springframework.beans.factory.annotation.Autowired;
// import: Token de autenticación de Spring Security para el contexto de usuario
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import: Contenedor global de seguridad donde se almacena el usuario autenticado
import org.springframework.security.core.context.SecurityContextHolder;
// import: Marca la clase como componente administrado por Spring
import org.springframework.stereotype.Component;
// import: Clase base de Spring que garantiza que el filtro se ejecute exactamente una vez por petición
import org.springframework.web.filter.OncePerRequestFilter;

// import: Manejo de excepciones de entrada/salida
import java.io.IOException;
// import: Lista vacía de autoridades para usuarios autenticados
import java.util.ArrayList;

// @Component: Registra el filtro como Bean en el contenedor de Spring
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // Inyecta la utilidad JwtUtils para parsear y verificar la firma criptográfica del token
    @Autowired
    private JwtUtils jwtUtils;

    // doFilterInternal: Método central invocado en cada petición HTTP entrante al servidor
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extrae el valor de la cabecera HTTP "Authorization" (por ejemplo: "Bearer eyJhbGciOiJIUzI1Ni...")
        String cabecera = request.getHeader("Authorization");

        // Verifica si la cabecera existe y comienza con el prefijo estándar "Bearer "
        if (cabecera != null && cabecera.startsWith("Bearer ")) {
            // Remueve los primeros 7 caracteres ("Bearer ") para aislar la cadena pura del Token JWT
            String token = cabecera.substring(7);
            // Invoca JwtUtils para verificar que el token no haya expirado y que su firma HMAC coincida
            if (jwtUtils.validarToken(token)) {
                // Extrae el nombre de usuario (claim 'sub') codificado dentro del payload del token
                String usuario = jwtUtils.obtenerUsuarioDelToken(token);
                // Construye el objeto de autenticación de Spring Security sin credenciales en texto plano
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(usuario, null, new ArrayList<>());
                // Establece la autenticación en el SecurityContextHolder para que los controladores permitan el acceso
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // Continúa la ejecución hacia el siguiente filtro en la cadena de seguridad o hacia el Controller
        filterChain.doFilter(request, response);
    }
}