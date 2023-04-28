package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;
import java.util.Collection;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {

    Collection<House> findAllByUser(@NotNull User user);
    Optional<House> findOneByName(@NotNull String name);
    Optional<House> findByNameOrId(@NotNull House name, @NotNull House id);

}
