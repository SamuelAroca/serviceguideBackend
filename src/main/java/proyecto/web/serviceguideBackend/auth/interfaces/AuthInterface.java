package proyecto.web.serviceguideBackend.auth.interfaces;

import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.LoginResponse;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.user.dto.UserDto;

public interface AuthInterface {

    UserDto register(SignUpDto userDto);
    LoginResponse login(CredentialsDto credentialsDto);
    UserDto findByEmail(String email);

}
