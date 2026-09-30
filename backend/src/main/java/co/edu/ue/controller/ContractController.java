// package: Capa de Controladores REST para Gestión de Contratos y Pagos (RF04)
package co.edu.ue.controller;

// import: Entidad JPA Contrato vinculada a la tabla 'contratos' en PostgreSQL
import co.edu.ue.model.Contrato;
// import: Repositorio JPA para operaciones CRUD sobre contratos
import co.edu.ue.repository.RepositorioContrato;
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
// @RequestMapping("/v1/contracts"): Ruta base para el módulo de contratos
@RequestMapping("/v1/contracts")
// @Tag: Documentación visual para Swagger UI
@Tag(name = "4. Gestión de Contratos (RF04)", description = "APIs REST para registrar y actualizar contratos en PostgreSQL")
public class ContractController {

    // Inyección del repositorio de contratos
    @Autowired
    private RepositorioContrato repositorioContrato;

    // @GetMapping: Endpoint HTTP GET para listar todos los contratos
    @GetMapping
    @Operation(summary = "Obtener Contratos", description = "Lista los contratos de arriendo registrados")
    public List<Contrato> getAllContracts() {
        // Ejecuta 'SELECT * FROM contratos' y retorna la lista en formato JSON
        return repositorioContrato.findAll();
    }

    // @PostMapping: Endpoint HTTP POST para registrar un nuevo contrato
    @PostMapping
    @Operation(summary = "Crear Contrato", description = "Registra un nuevo contrato vinculando inmueble e inquilino")
    public Contrato createContract(@RequestBody Contrato contrato) {
        // Ejecuta 'INSERT INTO contratos ...' y retorna la entidad persistida
        return repositorioContrato.save(contrato);
    }

    // @PutMapping("/{id}"): Endpoint HTTP PUT para actualizar las condiciones o estado de pago ("Al dia", "Moroso")
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Contrato", description = "Modifica los datos o estado de pago de un contrato")
    public ResponseEntity<Contrato> updateContract(@PathVariable Long id, @RequestBody Contrato detalles) {
        return repositorioContrato.findById(id).map(c -> {
            c.setIdInmueble(detalles.getIdInmueble());
            c.setIdArrendatario(detalles.getIdArrendatario());
            c.setFechaInicio(detalles.getFechaInicio());
            c.setFechaFin(detalles.getFechaFin());
            c.setCanonMensual(detalles.getCanonMensual());
            c.setEstadoPago(detalles.getEstadoPago());
            return ResponseEntity.ok(repositorioContrato.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    // @DeleteMapping("/{id}"): Endpoint HTTP DELETE para eliminar un contrato
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Contrato", description = "Elimina un contrato de la base de datos")
    public ResponseEntity<?> deleteContract(@PathVariable Long id) {
        return repositorioContrato.findById(id).map(c -> {
            repositorioContrato.delete(c);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}