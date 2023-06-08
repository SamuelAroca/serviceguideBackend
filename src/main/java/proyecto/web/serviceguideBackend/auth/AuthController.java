package proyecto.web.serviceguideBackend.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.LoginResponse;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.dto.UserDto;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.net.URI;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/auth")
public class AuthController {

    private final AuthService authService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final UserRepository userRepository;
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        return ResponseEntity.ok(loginService.login(credentialsDto));
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpDto user) {
        UserDto createdUser = authService.register(user);
        String token = userAuthenticationProvider.createToken(user.getEmail());
        createdUser.setToken(token);

        Optional<User> findUSer = userRepository.findByEmail(user.getEmail());
        if (findUSer.isEmpty()) {
            throw new AppException("Algo salió mal", HttpStatus.BAD_REQUEST);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(location).body(createdUser);
    }

    @GetMapping("/whoiam/{token}")
    public ResponseEntity<String> whoIAm(@PathVariable String token) {
        return ResponseEntity.ok(userAuthenticationProvider.whoIAm(token));
    }

    @GetMapping("/whoismyid/{token}")
    public ResponseEntity<Long> whoIsMyId(@PathVariable String token) {
        return ResponseEntity.ok(userAuthenticationProvider.whoIsMyId(token));
    }

    @GetMapping("/myName/{token}")
    public ResponseEntity<String> myName(@PathVariable String token) {
        return ResponseEntity.ok(userAuthenticationProvider.myName(token));
    }
}