// package: Capa de Controladores REST para Gestión de Arrendatarios e Inquilinos (RF03)
package co.edu.ue.controller;

// import: Entidad JPA Arrendatario vinculada a la tabla 'arrendatarios' en PostgreSQL
import co.edu.ue.model.Arrendatario;
// import: Repositorio JPA para operaciones de base de datos sobre inquilinos
import co.edu.ue.repository.RepositorioArrendatario;
// import: Anotaciones OpenAPI/Swagger para documentación interactiva de la API
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
// import: Inyección de dependencias de Spring
import org.springframework.beans.factory.annotation.Autowired;
// import: Contenedor genérico para respuestas HTTP
import org.springframework.http.ResponseEntity;
// import: Anotaciones REST de Spring Web
import org.springframework.web.bind.annotation.*;

// import: Manejo de colecciones tipo List
import java.util.List;

// @RestController: Controlador REST que serializa respuestas directamente en JSON
@RestController
// @RequestMapping("/v1/tenants"): Ruta base de los servicios de inquilinos
@RequestMapping("/v1/tenants")
// @Tag: Documentación visual para Swagger UI
@Tag(name = "3. Gestión de Arrendatarios (RF03)", description = "APIs REST para consultar y guardar inquilinos en PostgreSQL")
public class TenantController {

    // Inyección del repositorio JPA de arrendatarios
    @Autowired
    private RepositorioArrendatario repositorioArrendatario;

    // @GetMapping: Endpoint HTTP GET para listar todos los inquilinos
    @GetMapping
    @Operation(summary = "Obtener Arrendatarios", description = "Lista los arrendatarios e inquilinos registrados")
    public List<Arrendatario> getAllTenants() {
        // Ejecuta 'SELECT * FROM arrendatarios' y retorna la lista en formato JSON
        return repositorioArrendatario.findAll();
    }

    // @PostMapping: Endpoint HTTP POST para registrar un nuevo inquilino
    @PostMapping
    @Operation(summary = "Crear Arrendatario", description = "Inscribe un nuevo arrendatario en la base de datos PostgreSQL")
    public Arrendatario createTenant(@RequestBody Arrendatario arrendatario) {
        // Ejecuta 'INSERT INTO arrendatarios ...' y retorna la entidad con ID asignado
        return repositorioArrendatario.save(arrendatario);
    }

    // @PutMapping("/{id}"): Endpoint HTTP PUT para actualizar un inquilino existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Arrendatario", description = "Actualiza los datos del inquilino según su ID")
    public ResponseEntity<Arrendatario> updateTenant(@PathVariable Long id, @RequestBody Arrendatario detalles) {
        // Busca por ID en PostgreSQL y actualiza nombre, teléfono, correo y cédula
        return repositorioArrendatario.findById(id).map(arr -> {
            arr.setNombreCompleto(detalles.getNombreCompleto());
            arr.setTelefono(detalles.getTelefono());
            arr.setCorreo(detalles.getCorreo());
            arr.setCedula(detalles.getCedula());
            return ResponseEntity.ok(repositorioArrendatario.save(arr));
        }).orElse(ResponseEntity.notFound().build());
    }

    // @DeleteMapping("/{id}"): Endpoint HTTP DELETE para eliminar un inquilino por su ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Arrendatario", description = "Elimina un registro de arrendatario")
    public ResponseEntity<?> deleteTenant(@PathVariable Long id) {
        return repositorioArrendatario.findById(id).map(arr -> {
            repositorioArrendatario.delete(arr);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}