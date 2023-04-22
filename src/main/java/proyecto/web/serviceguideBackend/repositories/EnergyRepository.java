package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.EnergyReceipt;
import proyecto.web.serviceguideBackend.entities.User;

import java.util.Collection;
import java.util.Date;

public interface EnergyRepository extends JpaRepository<EnergyReceipt, Long> {

    Collection<EnergyReceipt> findAllByUser(User user);
    Collection<EnergyReceipt> findAllByDateAndUser(Date date, User userId);

}
