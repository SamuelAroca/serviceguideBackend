package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.SewerageReceipt;

import java.util.Collection;

public interface SewerageRepository extends JpaRepository<SewerageReceipt, Long> {

    Collection<SewerageReceipt> findAllByHouse(@NotNull House house);

}
