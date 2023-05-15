package proyecto.web.serviceguideBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.dto.SignUpDto;
import proyecto.web.serviceguideBackend.dto.UserDto;
import proyecto.web.serviceguideBackend.services.UserService;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/auth")
public class AuthController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserDto userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto.getEmail()));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpDto user) {
        UserDto createdUser = userService.register(user);
        createdUser.setToken(userAuthenticationProvider.createToken(user.getEmail()));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdUser.getId()).toUri();
        return ResponseEntity.created(location).body(createdUser);
    }

    @GetMapping("/whoiam/{token}")
    public ResponseEntity<String> whoIAm(@PathVariable String token) {
        return ResponseEntity.ok(userAuthenticationProvider.whoIAm(token));
    }

    @GetMapping("/whoismyid/{token}")
    public ResponseEntity<Long> whoIsMyId(@PathVariable String token) {
        return ResponseEntity.ok(userAuthenticationProvider.whoIsMyId(token));
    }

    @GetMapping("/myName/{token}")
    public ResponseEntity<String> myName(@PathVariable String token) {
        return ResponseEntity.ok(userAuthenticationProvider.myName(token));
    }
}