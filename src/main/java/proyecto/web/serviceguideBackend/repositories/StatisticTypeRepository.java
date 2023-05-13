package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.StatisticType;

import java.util.Optional;

public interface StatisticTypeRepository extends JpaRepository<StatisticType, Long> {

    Optional<StatisticType> findByTypeIgnoreCase(String type);
}
