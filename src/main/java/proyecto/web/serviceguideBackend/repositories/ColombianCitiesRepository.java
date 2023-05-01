package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.entities.ColombianCities;

import java.util.List;
import java.util.Optional;

public interface ColombianCitiesRepository extends JpaRepository<ColombianCities, Long> {

    Optional<ColombianCities> findIdByCity(@NotNull String city);
    Optional<ColombianCities> findByCity(@NotNull String city);

    @Query(value = "select c.city from ColombianCities c")
    List<String> allCities();

}
