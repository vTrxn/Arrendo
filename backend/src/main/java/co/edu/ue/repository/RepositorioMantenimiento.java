package co.edu.ue.repository;

import co.edu.ue.model.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioMantenimiento extends JpaRepository<Mantenimiento, Long> {
}