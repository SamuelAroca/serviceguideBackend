package proyecto.web.serviceguideBackend.user.interfaces;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.user.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(@NotNull String email);
    Optional<User> findByTokenPassword(String tokenPassword);
    @Query(value = "select u from User u inner join House h on h.user.id = u.id inner join Receipt r on r.house.id = h.id where r.id = ?1")
    Optional<User> findUserByReceipt(Long idReceipt);

}
