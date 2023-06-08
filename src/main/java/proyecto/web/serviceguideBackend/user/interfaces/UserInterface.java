package proyecto.web.serviceguideBackend.user.interfaces;

import proyecto.web.serviceguideBackend.dto.*;
import proyecto.web.serviceguideBackend.user.dto.UpdateResponse;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.user.dto.UserLoadDto;

import java.util.Optional;

public interface UserInterface {

    Optional<User> getByEmail(String email);
    Optional<UpdateResponse> updateUser(UpdateUserDto updateUserDto, Long idUser);
    UserLoadDto loadById(String token);
    Optional<User> findByTokenPassword(String tokenPassword);
    void save(User user);
    Message delete(Long idUser);

}
