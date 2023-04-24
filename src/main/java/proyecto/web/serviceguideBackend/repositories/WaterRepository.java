package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.WaterReceipt;

import java.util.Collection;

public interface WaterRepository extends JpaRepository<WaterReceipt, Long> {

    Collection<WaterReceipt> findAllByHouse(@NotNull House house);

}
