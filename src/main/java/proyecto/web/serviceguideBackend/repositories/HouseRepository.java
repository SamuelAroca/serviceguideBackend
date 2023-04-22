package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.User;

import java.util.Collection;

public interface HouseRepository extends JpaRepository<House, Long> {

    Collection<House> findAllByUser(User user);
}
