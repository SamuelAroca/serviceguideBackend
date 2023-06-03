package proyecto.web.serviceguideBackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.user.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.user.dto.UserLoadDto;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/update/{token}")
    @Transactional
    public Optional<UpdateUserDto> updateUser(@RequestBody SignUpDto updateUser, @PathVariable String token) {
        return userService.updateUser(updateUser, token);
    }

    @GetMapping("/findById/{token}")
    public ResponseEntity<UserLoadDto> userById(@PathVariable String token) {
        return ResponseEntity.ok(userService.loadById(token));
    }

    @DeleteMapping("/delete/{token}")
    @Transactional
    public ResponseEntity<Message> deleteUser(@PathVariable String token) {
        return ResponseEntity.ok(userService.delete(token));
    }
}
