package proyecto.web.serviceguideBackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.dto.SignUpDto;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.repositories.UserRepository;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/listUsers")
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @PutMapping("/update/{id}")
    @Transactional
    public Optional<ResponseEntity<Message>> updateUser(@RequestBody SignUpDto updateUser, @PathVariable Long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .map(user -> {
                    Optional<User> optionalUser = userRepository.findByEmail(updateUser.getEmail());
                    if (optionalUser.isPresent()) {
                        user.setFirstName(updateUser.getFirstName());
                        user.setLastName(updateUser.getLastName());
                        user.setEmail(updateUser.getEmail());
                        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(updateUser.getPassword())));
                        userRepository.save(user);
                        return ResponseEntity.ok(new Message("User updated successfully"));
                    } else {
                        throw new AppException("Username does not exist", HttpStatus.NOT_FOUND);
                    }
                }).orElseThrow(() -> new AppException("Username does not exist", HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> userById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException("Username does not exist", HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Message> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isEmpty()) {
            throw new AppException("Username does not exist", HttpStatus.NOT_FOUND);
        }
        userRepository.delete(userOptional.get());
        return ResponseEntity.ok(new Message("Delete success"));
    }
}
