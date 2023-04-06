package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.Receipt;

import java.util.Optional;

public interface ReceiptsRepository extends JpaRepository<Receipt, Long> {
}
