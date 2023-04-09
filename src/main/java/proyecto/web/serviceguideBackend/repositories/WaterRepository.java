package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.Water;

public interface WaterRepository extends JpaRepository<Water, Long> {
}
