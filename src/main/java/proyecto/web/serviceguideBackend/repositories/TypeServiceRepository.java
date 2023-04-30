package proyecto.web.serviceguideBackend.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.web.serviceguideBackend.entities.TypeService;

import java.util.Optional;

public interface TypeServiceRepository extends JpaRepository<TypeService, Long> {

    Optional<TypeService> findByType(@NotNull TypeService type);

}
