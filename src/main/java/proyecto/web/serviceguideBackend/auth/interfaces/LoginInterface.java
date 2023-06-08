package proyecto.web.serviceguideBackend.auth.interfaces;

import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.LoginResponse;

public interface LoginInterface {

    LoginResponse login(CredentialsDto credentialsDto);

}
