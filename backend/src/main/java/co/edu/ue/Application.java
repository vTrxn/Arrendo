// package: Paquete raíz de la arquitectura Spring Boot en co.edu.ue (estándar Uniempresarial)
package co.edu.ue;

// import: Importa la clase principal que arranca el contenedor de servlets embebido Tomcat
import org.springframework.boot.SpringApplication;
// import: Habilita la autoconfiguración de Spring Boot (Data JPA, Web MVC, Security)
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import: Especifica los paquetes donde Hibernate debe buscar entidades JPA (@Entity)
import org.springframework.boot.autoconfigure.domain.EntityScan;
// import: Define los paquetes donde Spring inyectará componentes, servicios y controladores
import org.springframework.context.annotation.ComponentScan;
// import: Habilita el escaneo de repositorios Spring Data JPA
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
// import: Interfaz CommandLineRunner para ejecutar tareas automáticas inmediatamente tras el arranque
import org.springframework.boot.CommandLineRunner;
// import: Anotación @Bean para registrar objetos administrados en el contenedor de inversión de control (IoC)
import org.springframework.context.annotation.Bean;
// import: Interfaz del codificador de contraseñas de Spring Security
import org.springframework.security.crypto.password.PasswordEncoder;
// import: Modelo de entidad Usuario que mapea la tabla 'usuarios' en PostgreSQL
import co.edu.ue.model.Usuario;
// import: Repositorio JPA para operaciones con la tabla de usuarios
import co.edu.ue.repository.RepositorioUsuario;

// @SpringBootApplication: Marca esta clase como la aplicación principal de Spring Boot
@SpringBootApplication
// @EntityScan: Escaneo explícito de paquetes de modelos para asegurar compatibilidad con Eclipse y JARs externos
@EntityScan(basePackages = {"co.edu.ue.model", "co.edu.ue.entities"})
// @EnableJpaRepositories: Vincula los repositorios de Spring Data con PostgreSQL
@EnableJpaRepositories(basePackages = {"co.edu.ue.repository", "co.edu.ue.jpa"})
// @ComponentScan: Descubre controladores (@RestController) y servicios (@Service)
@ComponentScan(basePackages = {"co.edu.ue"})
public class Application {

    // main: Método estático de punto de partida (Entry Point) ejecutado por Eclipse o Java CLI
    public static void main(String[] args) {
        // Inicia el contexto de Spring Boot, conecta el DataSource PostgreSQL y levanta Tomcat en el puerto 8080
        SpringApplication.run(Application.class, args);
    }

    // @Bean inicializarDatos: Semilla de datos (Data Seeding) que garantiza la existencia del usuario admin
    @Bean
    public CommandLineRunner inicializarDatos(RepositorioUsuario repositorioUsuario, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verifica si el usuario 'admin' ya existe en PostgreSQL
            if (repositorioUsuario.findByNombreUsuario("admin").isEmpty()) {
                // Guarda el usuario con contraseña hasheada en BCrypt ('admin123') y rol 'ADMIN'
                repositorioUsuario.save(new Usuario("admin", passwordEncoder.encode("admin123"), "ADMIN"));
                System.out.println("==================================================================");
                System.out.println(">>> [ARRENDO] Usuario 'admin' inicializado con éxito (clave: admin123) <<<");
                System.out.println("==================================================================");
            }
        };
    }
}