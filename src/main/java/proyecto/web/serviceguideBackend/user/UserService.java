package proyecto.web.serviceguideBackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.auth.AuthService;
import proyecto.web.serviceguideBackend.config.JwtService;
import proyecto.web.serviceguideBackend.dto.*;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.user.dto.UpdateResponse;
import proyecto.web.serviceguideBackend.user.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.user.dto.UserLoadDto;
import proyecto.web.serviceguideBackend.user.interfaces.UserInterface;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthService authService;

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UpdateResponse updateUser(UpdateUserDto updateUser, Long idUser) {

        if (updateUser.getFirstName().isEmpty() || updateUser.getLastName().isEmpty() || updateUser.getEmail().isEmpty()) {
            throw new AppException("First name, last name or email can't be empty", HttpStatus.BAD_REQUEST);
        }

        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }

        User user = optionalUser.get();

        if (!updateUser.getEmail().equals(user.getEmail())) {
            Optional<User> optionalUser1 = userRepository.findByEmail(updateUser.getEmail());
            if (optionalUser1.isPresent()) {
                throw new AppException("Email already registered", HttpStatus.BAD_REQUEST);
            }
            user.setEmail(updateUser.getEmail());
        }

        if (updateUser.getPassword() != null) {
            if (!updateUser.getPassword().isEmpty() || !passwordEncoder.matches(updateUser.getPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(updateUser.getPassword()));
            }
        }

        var savedUser = userRepository.save(user);
        var token = jwtService.getToken(savedUser);
        authService.revokedAllUserTokens(savedUser);
        authService.saveUserToken(savedUser, token);

        return UpdateResponse.builder()
                .message("User updated successfully")
                .status(HttpStatus.OK)
                .token(token)
                .build();
    }

    @Override
    public UserLoadDto loadById(String token) {
        Long id = jwtService.whoIsMyId(token);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        UserLoadDto userLoadDto = new UserLoadDto();
        userLoadDto.setId(optionalUser.get().getId());
        userLoadDto.setFirstName(optionalUser.get().getFirstName());
        userLoadDto.setLastName(optionalUser.get().getLastName());
        userLoadDto.setEmail(optionalUser.get().getEmail());
        return userLoadDto;
    }

    @Override
    public Optional<User> findByTokenPassword(String tokenPassword) {
        return userRepository.findByTokenPassword(tokenPassword);
    }

    @Override
    public UserLoadDto loadUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        UserLoadDto userLoadDto = new UserLoadDto();
        userLoadDto.setId(optionalUser.get().getId());
        userLoadDto.setFirstName(optionalUser.get().getFirstName());
        userLoadDto.setLastName(optionalUser.get().getLastName());
        userLoadDto.setEmail(optionalUser.get().getEmail());
        return userLoadDto;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Message delete(Long idUser) {
        Optional<User> userOptional = userRepository.findById(idUser);

        if (userOptional.isEmpty()) {
            throw new AppException("Username does not exist", HttpStatus.NOT_FOUND);
        }
        userRepository.delete(userOptional.get());
        return new Message("Delete success", HttpStatus.NO_CONTENT);
    }
}