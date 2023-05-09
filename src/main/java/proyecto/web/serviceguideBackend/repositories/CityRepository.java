package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.entities.City;

import java.util.Collection;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByCity(@NotNull String city);
    Optional<City> findAllByCityIgnoreCase(@NotNull String city);

    @Query(value = "select c.city from City c")
    Collection<String> allCities();

}
