package proyecto.web.serviceguideBackend.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.LoginResponse;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.config.JwtService;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.user.dto.UserDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/auth")
@CrossOrigin("http://localhost:5002")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        try {
            return ResponseEntity.ok(authService.login(credentialsDto));
        } catch (RuntimeException e) {
            throw new AppException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpDto user) {
        try {
            return ResponseEntity.ok(authService.register(user));
        } catch (RuntimeException e) {
            throw new AppException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/whoismyid/{token}")
    public ResponseEntity<Long> whoIsMyId(@PathVariable String token) {
        return ResponseEntity.ok(jwtService.whoIsMyId(token));
    }

    @GetMapping("/myName/{token}")
    public ResponseEntity<String> myName(@PathVariable String token) {
        return ResponseEntity.ok(jwtService.myName(token));
    }
}