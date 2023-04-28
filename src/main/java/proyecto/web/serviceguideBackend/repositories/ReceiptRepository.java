package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.TypeService;

import java.util.Collection;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findByReceiptName(@NotNull String name);
    Collection<Receipt> findByHouse(@NotNull House house);
    Collection<Receipt> findByTypeServiceAndHouse(@NotNull TypeService typeService, @NotNull House house);
    Collection<Receipt> findAllById(@NotNull Long id);

}
