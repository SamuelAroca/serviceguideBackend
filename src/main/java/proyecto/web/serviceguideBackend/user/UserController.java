package proyecto.web.serviceguideBackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.user.dto.UpdateResponse;
import proyecto.web.serviceguideBackend.user.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.user.dto.UserLoadDto;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/update/{idUser}")
    @Transactional
    public Optional<UpdateResponse> updateUser(@RequestBody UpdateUserDto updateUser, @PathVariable Long idUser) {
        return userService.updateUser(updateUser, idUser);
    }

    @GetMapping("/findById/{token}")
    public ResponseEntity<UserLoadDto> userById(@PathVariable String token) {
        return ResponseEntity.ok(userService.loadById(token));
    }

    @DeleteMapping("/delete/{idUser}")
    @Transactional
    public ResponseEntity<Message> deleteUser(@PathVariable Long idUser) {
        return ResponseEntity.ok(userService.delete(idUser));
    }
}
