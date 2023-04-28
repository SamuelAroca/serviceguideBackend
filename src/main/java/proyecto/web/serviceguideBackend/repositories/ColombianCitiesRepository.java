package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.ColombianCities;

import java.util.Optional;

public interface ColombianCitiesRepository extends JpaRepository<ColombianCities, Long> {

    Optional<ColombianCities> findOneByCity(@NotNull String city);
}
