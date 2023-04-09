package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.Gas;

public interface GasRepository extends JpaRepository<Gas, Long> {
}
