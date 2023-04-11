package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.entities.WaterReceipt;

import java.util.Collection;

public interface WaterRepository extends JpaRepository<WaterReceipt, Long> {

    Collection<WaterReceipt> findAllByUser(User user);

}
