// package: Capa de Controladores REST para Mantenimientos e Incidencias con Cámara (RF05)
package co.edu.ue.controller;

// import: Entidad JPA Mantenimiento mapeada a la tabla 'mantenimientos' en PostgreSQL
import co.edu.ue.model.Mantenimiento;
// import: Repositorio JPA para operaciones CRUD de incidencias
import co.edu.ue.repository.RepositorioMantenimiento;
// import: Anotaciones OpenAPI/Swagger para documentación de la API REST
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
// import: Inyección de dependencias de Spring
import org.springframework.beans.factory.annotation.Autowired;
// import: Contenedor genérico para respuestas HTTP
import org.springframework.http.ResponseEntity;
// import: Anotaciones Spring Web
import org.springframework.web.bind.annotation.*;

// import: Manejo de colecciones tipo List
import java.util.List;

// @RestController: Controlador REST que serializa respuestas directamente en JSON
@RestController
// @RequestMapping("/v1/maintenances"): Ruta base para el módulo de mantenimientos
@RequestMapping("/v1/maintenances")
// @Tag: Documentación visual para Swagger UI
@Tag(name = "5. Mantenimientos e Incidencias (RF05)", description = "APIs REST para registrar mantenimientos y evidencias en PostgreSQL")
public class MaintenanceController {

    // Inyección del repositorio de mantenimientos
    @Autowired
    private RepositorioMantenimiento repositorioMantenimiento;

    // @GetMapping: Endpoint HTTP GET para listar todos los mantenimientos reportados
    @GetMapping
    @Operation(summary = "Obtener Mantenimientos", description = "Lista todos los reportes de daño o mantenimientos")
    public List<Mantenimiento> getAllMaintenances() {
        // Ejecuta 'SELECT * FROM mantenimientos' y retorna la lista en formato JSON
        return repositorioMantenimiento.findAll();
    }

    // @PostMapping: Endpoint HTTP POST para registrar una nueva incidencia o daño con evidencia
    @PostMapping
    @Operation(summary = "Crear Mantenimiento", description = "Registra una nueva incidencia o reporte de daño")
    public Mantenimiento createMaintenance(@RequestBody Mantenimiento mantenimiento) {
        // Ejecuta 'INSERT INTO mantenimientos ...' y retorna la entidad persistida
        return repositorioMantenimiento.save(mantenimiento);
    }

    // @PutMapping("/{id}"): Endpoint HTTP PUT para actualizar el estado del mantenimiento ("Pendiente", "En Proceso", "Resuelto")
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Mantenimiento", description = "Actualiza los datos de una incidencia")
    public ResponseEntity<Mantenimiento> updateMaintenance(@PathVariable Long id, @RequestBody Mantenimiento detalles) {
        return repositorioMantenimiento.findById(id).map(m -> {
            m.setIdInmueble(detalles.getIdInmueble());
            m.setDescripcionDano(detalles.getDescripcionDano());
            m.setFechaReporte(detalles.getFechaReporte());
            m.setRutaFoto(detalles.getRutaFoto());
            m.setEstadoMantenimiento(detalles.getEstadoMantenimiento());
            return ResponseEntity.ok(repositorioMantenimiento.save(m));
        }).orElse(ResponseEntity.notFound().build());
    }

    // @DeleteMapping("/{id}"): Endpoint HTTP DELETE para eliminar un reporte
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Mantenimiento", description = "Elimina un reporte de la base de datos")
    public ResponseEntity<?> deleteMaintenance(@PathVariable Long id) {
        return repositorioMantenimiento.findById(id).map(m -> {
            repositorioMantenimiento.delete(m);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}