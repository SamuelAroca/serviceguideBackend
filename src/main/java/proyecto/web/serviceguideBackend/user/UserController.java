package proyecto.web.serviceguideBackend.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.user.dto.UpdateResponse;
import proyecto.web.serviceguideBackend.user.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.user.dto.UserLoadDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PutMapping("/update/{idUser}")
    @Transactional
    public UpdateResponse updateUser(@Valid @RequestBody UpdateUserDto updateUser, @PathVariable Long idUser,
                                      @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        return userService.updateUser(updateUser, idUser);
    }

    @GetMapping("/findById")
    public ResponseEntity<UserLoadDto> userById(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(userService.loadById(token));
    }

    @DeleteMapping("/delete/{idUser}")
    @Transactional
    public ResponseEntity<Message> deleteUser(@PathVariable Long idUser, @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        return ResponseEntity.ok(userService.delete(idUser));
    }

    @GetMapping("/loadUser/{idUser}")
    public ResponseEntity<UserLoadDto> userById(@PathVariable Long idUser, @AuthenticationPrincipal User currentUser) {
        requireSelf(idUser, currentUser);
        return ResponseEntity.ok(userService.loadUser(idUser));
    }

    // El JWT solo prueba quien es el llamante, no que idUser (del path) sea su
    // propia cuenta. Sin este chequeo, cualquier usuario autenticado podia
    // leer, actualizar o borrar la cuenta de cualquier otro con solo cambiar
    // el id en la URL.
    private void requireSelf(Long idUser, User currentUser) {
        if (currentUser == null || !currentUser.getId().equals(idUser)) {
            throw new AppException("Forbidden", HttpStatus.FORBIDDEN);
        }
    }
}
