package proyecto.web.serviceguideBackend.city.interfaces;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.city.City;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByCity(@NotNull String city);
    Optional<City> findAllByCityIgnoreCase(@NotNull String city);

}
