package proyecto.web.serviceguideBackend.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = """
      select t from Token t inner join Users u\s
      on t.users.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
    List<Token> findAllValidTokenByUser(Integer id);

    Optional<Token> findByToken(String token);
}
