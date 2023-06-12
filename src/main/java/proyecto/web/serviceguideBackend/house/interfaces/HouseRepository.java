package proyecto.web.serviceguideBackend.house.interfaces;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.user.User;

import java.util.Collection;
import java.util.Optional;

public interface HouseRepository extends JpaRepository<House, Long> {

    Collection<House> findAllByUserOrderById(@NotNull User user);
    Optional<House> findByUserAndName(@NotNull User user, @NotNull String name);
    @Query(value = "select u.id from User u inner join House h on h.user.id = u.id where h.id = ?1")
    Long findUserByHouseId(Long id);
    @Query(value = "select h.name from House h inner join User u on h.user.id = u.id where u.id = ?1")
    Collection<String> getHouseName(Long id);
    @Query(value = "select h.id from House h where h.name = ?1")
    Long findIdByName(String name);

}
