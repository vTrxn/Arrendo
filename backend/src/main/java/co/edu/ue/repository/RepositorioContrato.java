package co.edu.ue.repository;

import co.edu.ue.model.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioContrato extends JpaRepository<Contrato, Long> {
}