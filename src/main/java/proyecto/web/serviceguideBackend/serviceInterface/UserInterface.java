package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.dto.SignUpDto;
import proyecto.web.serviceguideBackend.dto.UserDto;
import proyecto.web.serviceguideBackend.entities.User;

import java.util.Optional;

public interface UserInterface {

    UserDto login(CredentialsDto credentialsDto);
    UserDto register(SignUpDto userDto);
    UserDto findByEmail(String email);
    Optional<User> getByEmail(String email);
    Optional<User> findByTokenPassword(String tokenPassword);
    void save(User user);
}
