package proyecto.web.serviceguideBackend.auth.interfaces;

import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.dto.UserDto;

public interface AuthInterface {

    UserDto login(CredentialsDto credentialsDto);
    UserDto register(SignUpDto userDto);
    UserDto findByEmail(String email);
    void saveUserToken(User user, String jwtToken);
    void revokeAllUserTokens(User user);

}
