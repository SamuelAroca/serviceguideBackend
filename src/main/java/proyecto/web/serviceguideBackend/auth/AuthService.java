package proyecto.web.serviceguideBackend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.LoginResponse;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.auth.interfaces.AuthInterface;
import proyecto.web.serviceguideBackend.config.JwtService;
import proyecto.web.serviceguideBackend.token.Token;
import proyecto.web.serviceguideBackend.token.enums.TokenType;
import proyecto.web.serviceguideBackend.token.interfaces.TokenRepository;
import proyecto.web.serviceguideBackend.user.dto.UserDto;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.user.enums.Role;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenRepository tokenRepository;

    @Override
    public UserDto register(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isPresent()) {
            throw new AppException("User already registered", HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .role(Role.USER)
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build();
        var savedUser = userRepository.save(user);
        var token = jwtService.getToken(user);
        return UserDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .token(token)
                .build();
    }

    @Override
    public LoginResponse login(CredentialsDto credentialsDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentialsDto.getEmail(), credentialsDto.getPassword()));
        var user = userRepository.findByEmail(credentialsDto.getEmail()).orElseThrow();
        var token = jwtService.getToken(user);
        revokedAllUserTokens(user);
        saveUserToken(user, token);
        return LoginResponse.builder()
                .token(token)
                .build();
    }

    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokedAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
