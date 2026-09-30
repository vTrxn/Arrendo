package co.edu.ue.repository;

import co.edu.ue.model.Inmueble;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioInmueble extends JpaRepository<Inmueble, Long> {
}