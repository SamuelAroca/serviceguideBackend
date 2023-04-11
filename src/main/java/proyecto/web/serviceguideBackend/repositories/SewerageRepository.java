package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.SewerageReceipt;
import proyecto.web.serviceguideBackend.entities.User;

import java.util.Collection;

public interface SewerageRepository extends JpaRepository<SewerageReceipt, Long> {

    Collection<SewerageReceipt> findAllByUser(User user);

}
