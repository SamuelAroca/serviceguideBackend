package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.GasReceipt;
import proyecto.web.serviceguideBackend.entities.House;

import java.util.Collection;

public interface GasRepository extends JpaRepository<GasReceipt, Long> {

    Collection<GasReceipt> findAllByHouse(House house);

}
