package proyecto.web.serviceguideBackend.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = "select t from Token t inner join User u on t.user.id = u.id where u.id = ?1 and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokenByUser(Long id);
    Optional<Token> findByToken(String token);

}
