package proyecto.web.serviceguideBackend.emailpassword.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.emailpassword.verificationCode.VerificationCode;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUserEmailAndCode(String email, String code);
}
