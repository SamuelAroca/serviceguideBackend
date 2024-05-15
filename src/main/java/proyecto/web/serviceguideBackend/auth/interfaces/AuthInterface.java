package proyecto.web.serviceguideBackend.auth.interfaces;

import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.LoginResponse;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.dto.Message;

public interface AuthInterface {

    LoginResponse register(SignUpDto userDto);
    LoginResponse login(CredentialsDto credentialsDto);

}
