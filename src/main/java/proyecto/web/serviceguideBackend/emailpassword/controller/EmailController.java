package proyecto.web.serviceguideBackend.emailpassword.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.web.serviceguideBackend.config.RateLimiter;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.emailpassword.dto.*;
import proyecto.web.serviceguideBackend.emailpassword.repository.VerificationCodeRepository;
import proyecto.web.serviceguideBackend.emailpassword.service.EmailService;
import proyecto.web.serviceguideBackend.emailpassword.verificationCode.VerificationCode;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.user.UserService;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final RateLimiter rateLimiter;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    // Mismo namespace de clave para verify-code y reset-password: los dos
    // adivinan el mismo codigo de 6 digitos, asi que cuentan contra el mismo
    // limite (si no, alcanzaba con pegarle directo a reset-password para
    // saltarse el limite puesto solo en verify-code).
    private static final int CODE_MAX_ATTEMPTS = 5;
    private static final Duration CODE_WINDOW = Duration.ofMinutes(10);

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${mail.subject}")
    private String mailSubject;

    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmailTemplate(@RequestBody EmailValuesDto dto) {
        // No revela si el correo existe o no (evita enumeracion de usuarios):
        // siempre responde igual, y solo envia si de verdad hay una cuenta.
        userService.getByEmail(dto.getMailTo()).ifPresent(user -> {
            dto.setMailFrom(mailFrom);
            dto.setMailTo(user.getEmail());
            dto.setSubject(mailSubject);
            dto.setUserName(user.getFirstName());

            String tokenPassword = UUID.randomUUID().toString();
            dto.setToken(tokenPassword);
            user.setTokenPassword(tokenPassword);

            userService.save(user);
            emailService.sendEmail(dto);
        });
        return ResponseEntity.ok(new Message("If the email is registered, you'll receive instructions"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new AppException("Misplaced fields", HttpStatus.BAD_REQUEST);
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new AppException("Passwords don't match", HttpStatus.BAD_REQUEST);
        }

        Optional<User> optionalUser = userService.findByTokenPassword(dto.getTokenPassword());
        if (optionalUser.isEmpty()) {
            throw new AppException("Invalid Token", HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();
        String newPassword = passwordEncoder.encode(dto.getPassword());
        user.setPassword(newPassword);
        user.setTokenPassword(null);
        userService.save(user);
        return ResponseEntity.ok(new Message("Updated password", HttpStatus.OK));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        // Misma respuesta exista o no la cuenta: evita que este endpoint sirva
        // para averiguar que correos estan registrados.
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String verificationCode = generateVerificationCode();
            verificationCodeRepository.save(new VerificationCode(user, verificationCode));
            emailService.sendVerificationEmail(request.getEmail(), verificationCode);
        });

        return ResponseEntity.ok(new Message("If the email is registered, a verification code has been sent", HttpStatus.OK));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        String key = "code:" + request.getEmail().toLowerCase();
        rateLimiter.checkAllowed(key, CODE_MAX_ATTEMPTS, CODE_WINDOW);

        Optional<VerificationCode> optionalCode = verificationCodeRepository.findByUserEmailAndCode(request.getEmail(), request.getCode());
        if (optionalCode.isEmpty() || optionalCode.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            rateLimiter.recordFailure(key, CODE_WINDOW);
            throw new AppException("Invalid or expired verification code", HttpStatus.BAD_REQUEST);
        }
        rateLimiter.recordSuccess(key);

        return ResponseEntity.ok(new Message("Verification successful", HttpStatus.OK));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        String key = "code:" + request.getEmail().toLowerCase();
        rateLimiter.checkAllowed(key, CODE_MAX_ATTEMPTS, CODE_WINDOW);

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        // Un solo mensaje generico para "correo no existe" y "codigo invalido":
        // separarlos deja saber a un atacante que correos si estan registrados.
        Optional<VerificationCode> optionalCode = optionalUser.isPresent()
                ? verificationCodeRepository.findByUserEmailAndCode(request.getEmail(), request.getCode())
                : Optional.empty();
        if (optionalUser.isEmpty() || optionalCode.isEmpty() || optionalCode.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            rateLimiter.recordFailure(key, CODE_WINDOW);
            throw new AppException("Invalid email or verification code", HttpStatus.BAD_REQUEST);
        }
        rateLimiter.recordSuccess(key);

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new Message("Password reset successfully", HttpStatus.OK));
    }

    private String generateVerificationCode() {
        // Generate a random 6-digit code
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }
}
