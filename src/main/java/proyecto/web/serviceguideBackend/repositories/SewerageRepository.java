package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.Sewerage;

public interface SewerageRepository extends JpaRepository<Sewerage, Long> {
}
