package proyecto.web.serviceguideBackend.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import proyecto.web.serviceguideBackend.house.House;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    @Size(max = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Size(max = 100)
    private String lastName;

    @Column(nullable = false)
    @Size(max = 100)
    private String email;

    @Column(nullable = false)
    @Size(max = 100)
    private String password;

    private String tokenPassword;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<House> house = new ArrayList<>();

}
