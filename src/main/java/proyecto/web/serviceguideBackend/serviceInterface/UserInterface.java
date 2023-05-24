package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.*;
import proyecto.web.serviceguideBackend.entities.User;

import java.util.Collection;
import java.util.Optional;

public interface UserInterface {

    UserDto login(CredentialsDto credentialsDto);
    UserDto register(SignUpDto userDto);
    Optional<User> getByEmail(String email);
    Optional<UpdateUserDto> updateUser(SignUpDto updateUserDto, String token);
    Collection<User> listAll();
    UserLoadDto findById(String token);
    Optional<User> findByTokenPassword(String tokenPassword);
    void save(User user);
    Message delete(String token);
}
