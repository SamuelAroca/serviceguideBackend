package proyecto.web.serviceguideBackend.token.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.token.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(value = "select t from Token t inner join User u on t.user.id = u.id where u.id = ?1 and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokenByUser(Long idUser);

    // JOIN FETCH: el filtro de autenticacion necesita el User para cada
    // request autenticado; sin esto seria una query aparte por request.
    @Query("select t from Token t join fetch t.user where t.token = ?1")
    Optional<Token> findByToken(String token);
}
