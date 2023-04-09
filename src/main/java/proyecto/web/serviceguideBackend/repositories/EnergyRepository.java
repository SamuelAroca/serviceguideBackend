package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.Energy;

public interface EnergyRepository extends JpaRepository<Energy, Long> {
}
