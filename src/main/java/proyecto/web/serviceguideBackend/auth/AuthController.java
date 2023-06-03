package proyecto.web.serviceguideBackend.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.user.dto.UserDto;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/auth")
public class AuthController {

    private final AuthService authService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserDto userDto = authService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpDto user) {
        UserDto createdUser = authService.register(user);
        createdUser.setToken(userAuthenticationProvider.createToken(user.getEmail()));

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