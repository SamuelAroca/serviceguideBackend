package proyecto.web.serviceguideBackend.receipt.typeService;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeServiceRepository extends JpaRepository<TypeService, Long> {

    Optional<TypeService> findByTypeIgnoreCase(@NotNull String type);

}
