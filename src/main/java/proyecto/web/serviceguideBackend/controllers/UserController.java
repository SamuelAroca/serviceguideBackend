package proyecto.web.serviceguideBackend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.dto.SignUpDto;
import proyecto.web.serviceguideBackend.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.services.UserService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/listUsers")
    public ResponseEntity<Collection<User>> listUsers() {
        return ResponseEntity.ok(userService.listAll());
    }

    @PutMapping("/update/{token}")
    @Transactional
    public Optional<UpdateUserDto> updateUser(@RequestBody SignUpDto updateUser, @PathVariable String token) {
        return userService.updateUser(updateUser, token);
    }

    @GetMapping("/findById/{token}")
    public ResponseEntity<Optional<User>> userById(@PathVariable String token) {
        return ResponseEntity.ok(userService.findById(token));
    }

    @DeleteMapping("/delete/{token}")
    @Transactional
    public ResponseEntity<Message> deleteUser(@PathVariable String token) {
        return ResponseEntity.ok(userService.delete(token));
    }
}
