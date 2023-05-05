package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;

import java.util.Collection;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {

    Collection<House> findAllByUserOrderById(@NotNull User user);
    Optional<House> findByUserAndName(@NotNull User user, @NotNull String name);
    Collection<House> findByUser(@NotNull User user);

    @Query(value = "select h.name from House h inner join User u on h.user.id = u.id where u.id = ?1")
    Collection<String> getHouseName(Long id);

}
