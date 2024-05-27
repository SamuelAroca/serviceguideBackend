package proyecto.web.serviceguideBackend.emailpassword.verificationCode;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import proyecto.web.serviceguideBackend.user.User;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private String code;

    private LocalDateTime expirationTime;

    public VerificationCode(User user, String code) {
        this.user = user;
        this.code = code;
        this.expirationTime = LocalDateTime.now().plusMinutes(10);
    }
}
