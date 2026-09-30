package co.edu.ue.repository;

import co.edu.ue.model.Arrendatario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioArrendatario extends JpaRepository<Arrendatario, Long> {
}