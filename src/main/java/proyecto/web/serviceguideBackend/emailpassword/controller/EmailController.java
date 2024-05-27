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
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.emailpassword.dto.*;
import proyecto.web.serviceguideBackend.emailpassword.repository.VerificationCodeRepository;
import proyecto.web.serviceguideBackend.emailpassword.service.EmailService;
import proyecto.web.serviceguideBackend.emailpassword.verificationCode.VerificationCode;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.user.UserService;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
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

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${mail.subject}")
    private String mailSubject;

    @PostMapping("/send-email")
    public ResponseEntity<?> sendEmailTemplate(@RequestBody EmailValuesDto dto) {
        Optional<User> optionalUser = userService.getByEmail(dto.getMailTo());
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();

        dto.setMailFrom(mailFrom);
        dto.setMailTo(user.getEmail());
        dto.setSubject(mailSubject);
        dto.setUserName(user.getFirstName());

        UUID uuid = UUID.randomUUID();
        String tokenPassword = uuid.toString();

        dto.setToken(tokenPassword);
        user.setTokenPassword(tokenPassword);

        userService.save(user);
        emailService.sendEmail(dto);
        return ResponseEntity.ok(new Message("We have sent you an email"));
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
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }

        String verificationCode = generateVerificationCode();
        VerificationCode code = new VerificationCode(optionalUser.get(), verificationCode);
        verificationCodeRepository.save(code);

        emailService.sendVerificationEmail(request.getEmail(), verificationCode);

        return ResponseEntity.ok(new Message("Verification code sent", HttpStatus.OK));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyCodeRequest request) {
        Optional<VerificationCode> optionalCode = verificationCodeRepository.findByUserEmailAndCode(request.getEmail(), request.getCode());
        if (optionalCode.isEmpty() || optionalCode.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new AppException("Invalid or expired verification code", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(new Message("Verification successful", HttpStatus.OK));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }

        Optional<VerificationCode> optionalCode = verificationCodeRepository.findByUserEmailAndCode(request.getEmail(), request.getCode());
        if (optionalCode.isEmpty() || optionalCode.get().getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new AppException("Invalid or expired verification code", HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new Message("Password reset successfully", HttpStatus.OK));
    }

    private String generateVerificationCode() {
        // Generate a random 6-digit code
        return String.format("%06d", new Random().nextInt(999999));
    }
}
