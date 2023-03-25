package proyecto.web.serviceguideBackend.user;

import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    @Query(value = "SELECT u FROM Users u")
    List<Users> listSAll();
}
