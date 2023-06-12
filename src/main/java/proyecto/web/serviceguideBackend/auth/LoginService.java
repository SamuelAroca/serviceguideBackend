package proyecto.web.serviceguideBackend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.LoginResponse;
import proyecto.web.serviceguideBackend.auth.interfaces.LoginInterface;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.nio.CharBuffer;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginInterface {

    private final UserAuthenticationProvider authenticationProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(CredentialsDto credentialsDto) {
        LoginResponse loginResponse = new LoginResponse();
        Optional<User> optionalUser = userRepository.findByEmail(credentialsDto.getEmail());
        if (optionalUser.isEmpty()) {
            throw new AppException("Wrong email or password", HttpStatus.BAD_REQUEST);
        }
        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), optionalUser.get().getPassword())) {
            String token = authenticationProvider.createToken(optionalUser.get().getEmail());
            loginResponse.setToken(token);
            return loginResponse;
        }
        throw new AppException("Wrong email or password", HttpStatus.BAD_REQUEST);
    }
}
