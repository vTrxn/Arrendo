// package: Capa de seguridad criptográfica para generación y validación de Tokens JWT
package co.edu.ue.security;

// import: Interfaz Claims de JJWT que representa los datos codificados en el cuerpo del token
import io.jsonwebtoken.Claims;
// import: Factoría constructora de tokens de la librería JJWT (Java JSON Web Token)
import io.jsonwebtoken.Jwts;
// import: Especificación del algoritmo criptográfico HS256 (HMAC con SHA-256)
import io.jsonwebtoken.SignatureAlgorithm;
// import: Generador de claves secretas a partir de arreglos de bytes
import io.jsonwebtoken.security.Keys;
// import: Inyección de valores desde application.properties
import org.springframework.beans.factory.annotation.Value;
// import: Marca la clase como componente Bean de Spring
import org.springframework.stereotype.Component;

// import: Interfaz Java Security para llaves criptográficas
import java.security.Key;
// import: Clase Date para marcas temporales de emisión y expiración
import java.util.Date;

// @Component: Registra el generador de tokens como componente reutilizable
@Component
public class JwtUtils {

    // @Value: Lee la clave secreta desde application.properties o toma la predeterminada de 256 bits
    @Value("${jwt.secret:ArrendoUniempresarialSecretKeyJWT2026SecureKey1234567890}")
    private String claveSecreta;

    // @Value: Define la duración del token (86400000 milisegundos = 24 horas)
    @Value("${jwt.expiration:86400000}")
    private long tiempoExpiracion;

    // obtenerLlaveFirma: Transforma la clave secreta de texto en una clave criptográfica HMAC-SHA
    private Key obtenerLlaveFirma() {
        return Keys.hmacShaKeyFor(claveSecreta.getBytes());
    }

    // generarToken: Construye y firma digitalmente un nuevo Token JWT con el nombre de usuario
    public String generarToken(String usuario) {
        return Jwts.builder()
            .setSubject(usuario) // Define el sujeto (nombre del usuario autenticado)
            .setIssuedAt(new Date()) // Fecha y hora de creación del token
            .setExpiration(new Date(System.currentTimeMillis() + tiempoExpiracion)) // Fecha de expiración (24h)
            .signWith(obtenerLlaveFirma(), SignatureAlgorithm.HS256) // Firma digital HMAC con SHA-256
            .compact(); // Serializa el token a formato estándar Base64URL separado por puntos (Header.Payload.Signature)
    }

    // obtenerUsuarioDelToken: Parsea el token con la clave secreta y extrae el sujeto (username)
    public String obtenerUsuarioDelToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(obtenerLlaveFirma()) // Configura la llave secreta para verificar la autenticidad
            .build()
            .parseClaimsJws(token) // Valida que la firma coincida y no esté vencido
            .getBody(); // Extrae el cuerpo JSON con los claims
        return claims.getSubject();
    }

    // validarToken: Retorna true si el token es auténtico y no ha expirado; false si fue adulterado
    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(obtenerLlaveFirma()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Firma inválida, token corrupto o tiempo de expiración superado
        }
    }
}