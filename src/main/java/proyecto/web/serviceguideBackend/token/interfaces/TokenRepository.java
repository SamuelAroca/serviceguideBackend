package proyecto.web.serviceguideBackend.token.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.token.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = "select t from Token t inner join User u on t.user.id = u.id where u.id = ?1 and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokenByUser(Long idUser);

    Optional<Token> findByToken(String token);
}
