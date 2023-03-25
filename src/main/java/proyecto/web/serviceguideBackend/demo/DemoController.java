package proyecto.web.serviceguideBackend.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.web.serviceguideBackend.user.UserRepository;
import proyecto.web.serviceguideBackend.user.Users;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/demo")
public class DemoController {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/query")
    public ResponseEntity<List<Users>> listAll() {
        return ResponseEntity.ok(userRepository.listSAll());
    }

    @GetMapping("/hola/{id}")
    public ResponseEntity<Optional<Users>> holis(@PathVariable Long id) {
        return ResponseEntity.ok(userRepository.findById(id));
    }
}
