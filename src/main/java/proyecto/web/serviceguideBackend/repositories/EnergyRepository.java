package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.EnergyReceipt;
import proyecto.web.serviceguideBackend.entities.House;

import java.util.Collection;

public interface EnergyRepository extends JpaRepository<EnergyReceipt, Long> {

    Collection<EnergyReceipt> findAllByHouse(@NotNull House house);

}
