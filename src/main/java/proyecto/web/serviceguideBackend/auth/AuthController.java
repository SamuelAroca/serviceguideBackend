package proyecto.web.serviceguideBackend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.auth.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.auth.dto.LoginResponse;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.config.JwtService;
import proyecto.web.serviceguideBackend.config.RateLimiter;
import proyecto.web.serviceguideBackend.exceptions.AppException;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/auth")
public class AuthController {

    private static final int LOGIN_MAX_ATTEMPTS = 5;
    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(15);
    private static final int REGISTER_MAX_ATTEMPTS = 10;
    private static final Duration REGISTER_WINDOW = Duration.ofHours(1);

    private final AuthService authService;
    private final JwtService jwtService;
    private final RateLimiter rateLimiter;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        String key = "login:" + credentialsDto.getEmail().toLowerCase();
        rateLimiter.checkAllowed(key, LOGIN_MAX_ATTEMPTS, LOGIN_WINDOW);
        try {
            LoginResponse response = authService.login(credentialsDto);
            rateLimiter.recordSuccess(key);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            rateLimiter.recordFailure(key, LOGIN_WINDOW);
            log.warn("Login failed for email {}: {}", credentialsDto.getEmail(), e.getMessage());
            throw new AppException("User or password incorrect", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<LoginResponse> register(@RequestBody @Valid SignUpDto user, HttpServletRequest request) {
        String key = "register:" + request.getRemoteAddr();
        rateLimiter.checkAllowed(key, REGISTER_MAX_ATTEMPTS, REGISTER_WINDOW);
        // A diferencia del login, se cuenta CADA intento (no solo los
        // fallidos): el abuso aca es crear cuentas en masa, asi que un
        // registro exitoso no debe limpiar el contador o alguien podria
        // crear cuentas sin limite mientras cada una individualmente
        // funcione.
        rateLimiter.recordFailure(key, REGISTER_WINDOW);
        try {
            return ResponseEntity.ok(authService.register(user));
        } catch (RuntimeException e) {
            throw new AppException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/whoismyid")
    public ResponseEntity<Long> whoIsMyId(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AppException("Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring("Bearer ".length());
        return ResponseEntity.ok(jwtService.whoIsMyId(token));
    }

    @GetMapping("/myName")
    public ResponseEntity<String> myName(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(jwtService.myName(token));
    }
}