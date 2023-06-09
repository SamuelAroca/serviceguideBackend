package proyecto.web.serviceguideBackend.auth.interfaces;

import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.user.dto.UserDto;

public interface AuthInterface {

    UserDto register(SignUpDto userDto);
    UserDto findByEmail(String email);

}
