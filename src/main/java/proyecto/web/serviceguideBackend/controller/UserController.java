package proyecto.web.serviceguideBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.dto.LoginDTO;
import proyecto.web.serviceguideBackend.dto.UserDTO;
import proyecto.web.serviceguideBackend.response.LoginResponse;
import proyecto.web.serviceguideBackend.service.UserService;

@RestController
@CrossOrigin
@RequestMapping(path = "api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/save")
    public String saveUser(@RequestBody UserDTO userDTO) {
        return userService.addUser(userDTO);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> userLogin(@RequestBody LoginDTO loginDTO) {
        LoginResponse loginResponse = userService.loginUser(loginDTO);
        return ResponseEntity.ok(loginResponse);
    }
}
