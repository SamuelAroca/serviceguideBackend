package proyecto.web.serviceguideBackend.user.interfaces;

import proyecto.web.serviceguideBackend.auth.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.SignUpDto;
import proyecto.web.serviceguideBackend.dto.*;
import proyecto.web.serviceguideBackend.user.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.dto.UserDto;
import proyecto.web.serviceguideBackend.user.dto.UserLoadDto;

import java.util.Collection;
import java.util.Optional;

public interface UserInterface {

    Optional<User> getByEmail(String email);
    Optional<UpdateUserDto> updateUser(SignUpDto updateUserDto, String token);
    UserLoadDto loadById(String token);
    Optional<User> findById(String token);
    Optional<User> findByTokenPassword(String tokenPassword);
    void save(User user);
    Message delete(String token);
}
