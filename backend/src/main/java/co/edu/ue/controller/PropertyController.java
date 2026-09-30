// package: Capa de Controladores REST para la Gestión Inmobiliaria (RF02)
package co.edu.ue.controller;

// import: Entidad JPA Inmueble vinculada a la tabla 'inmuebles' en PostgreSQL
import co.edu.ue.model.Inmueble;
// import: Repositorio Spring Data JPA con operaciones CRUD automáticas
import co.edu.ue.repository.RepositorioInmueble;
// import: Anotaciones OpenAPI/Swagger para documentación interactiva de la API
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
// import: Inyección de dependencias del framework Spring
import org.springframework.beans.factory.annotation.Autowired;
// import: Contenedor genérico para respuestas HTTP (200 OK, 404 Not Found, etc.)
import org.springframework.http.ResponseEntity;
// import: Anotaciones de Spring Web (@RestController, @RequestMapping, @GetMapping, @PostMapping, @PutMapping, @DeleteMapping)
import org.springframework.web.bind.annotation.*;

// import: Manejo de colecciones tipo List
import java.util.List;

// @RestController: Marca la clase como controlador REST que serializa respuestas directamente en formato JSON
@RestController
// @RequestMapping("/v1/properties"): Ruta base de los servicios de propiedades
@RequestMapping("/v1/properties")
// @Tag: Categoría de documentación en Swagger UI
@Tag(name = "2. Gestión de Inmuebles (RF02)", description = "APIs REST para consultar, guardar y borrar propiedades en PostgreSQL")
public class PropertyController {

    // Repositorio JPA que interactúa directamente con la tabla 'inmuebles'
    @Autowired
    private RepositorioInmueble repositorioInmueble;

    // @GetMapping: Endpoint HTTP GET para listar todas las propiedades guardadas
    @GetMapping
    @Operation(summary = "Obtener Inmuebles", description = "Retorna la lista de todas las propiedades almacenadas")
    public List<Inmueble> getAllProperties() {
        // Ejecuta 'SELECT * FROM inmuebles' a través de Hibernate y retorna la lista en JSON
        return repositorioInmueble.findAll();
    }

    // @PostMapping: Endpoint HTTP POST para registrar una nueva propiedad
    @PostMapping
    @Operation(summary = "Crear Inmueble", description = "Guarda un nuevo inmueble en la base de datos PostgreSQL")
    public Inmueble createProperty(@RequestBody Inmueble inmueble) {
        // Ejecuta 'INSERT INTO inmuebles ...' y retorna el inmueble con su ID autogenerado
        return repositorioInmueble.save(inmueble);
    }

    // @PutMapping("/{id}"): Endpoint HTTP PUT para actualizar los datos de una propiedad existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Inmueble", description = "Actualiza los datos del inmueble según su ID")
    public ResponseEntity<Inmueble> updateProperty(@PathVariable Long id, @RequestBody Inmueble detalles) {
        // Busca el inmueble por ID; si existe, actualiza sus campos y lo persiste; si no, retorna 404
        return repositorioInmueble.findById(id).map(inm -> {
            inm.setDireccion(detalles.getDireccion());
            inm.setTipoInmueble(detalles.getTipoInmueble());
            inm.setPrecioArriendo(detalles.getPrecioArriendo());
            inm.setEstado(detalles.getEstado());
            inm.setUriImagen(detalles.getUriImagen());
            return ResponseEntity.ok(repositorioInmueble.save(inm));
        }).orElse(ResponseEntity.notFound().build());
    }

    // @DeleteMapping("/{id}"): Endpoint HTTP DELETE para eliminar una propiedad por su ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Inmueble", description = "Elimina un inmueble de la base de datos PostgreSQL")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        // Si el inmueble existe, ejecuta 'DELETE FROM inmuebles WHERE id = ?' y retorna 200 OK
        return repositorioInmueble.findById(id).map(inm -> {
            repositorioInmueble.delete(inm);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}