package proyecto.web.serviceguideBackend.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.auth.dto.SignUpDto;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.dto.*;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.user.dto.UpdateUserDto;
import proyecto.web.serviceguideBackend.user.dto.UserLoadDto;
import proyecto.web.serviceguideBackend.user.interfaces.UserInterface;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.nio.CharBuffer;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserInterface {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthenticationProvider authenticationProvider;

    @Override
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UpdateUserDto> updateUser(SignUpDto updateUser, String token) {
        Long id = authenticationProvider.whoIsMyId(token);
        return Optional.ofNullable(userRepository.findById(id)
                .map(user -> {
                    Optional<User> optionalUser = userRepository.findByEmail(updateUser.getEmail());
                    if (optionalUser.isPresent()) {
                        if (optionalUser.get().getEmail().equals(updateUser.getEmail())) {
                            user.setFirstName(updateUser.getFirstName());
                            user.setLastName(updateUser.getLastName());
                            user.setEmail(updateUser.getEmail());
                            user.setPassword(passwordEncoder.encode(CharBuffer.wrap(updateUser.getPassword())));
                            userRepository.save(user);
                            String newToken = authenticationProvider.createToken(updateUser.getEmail());
                            return new UpdateUserDto("User updated", HttpStatus.OK, newToken);
                        }
                        throw new AppException("Email already exist", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    user.setFirstName(updateUser.getFirstName());
                    user.setLastName(updateUser.getLastName());
                    user.setEmail(updateUser.getEmail());
                    user.setPassword(passwordEncoder.encode(CharBuffer.wrap(updateUser.getPassword())));
                    userRepository.save(user);
                    String newToken = authenticationProvider.createToken(updateUser.getEmail());
                    return new UpdateUserDto("User updated", HttpStatus.OK, newToken);
                }).orElseThrow(() -> new AppException("Username does not exist", HttpStatus.NOT_FOUND)));
    }

    @Override
    public UserLoadDto loadById(String token) {
        Long id = authenticationProvider.whoIsMyId(token);
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
    public Optional<User> findById(String token) {
        Long id = authenticationProvider.whoIsMyId(token);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        return optionalUser;
    }

    @Override
    public Optional<User> findByTokenPassword(String tokenPassword) {
        return userRepository.findByTokenPassword(tokenPassword);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Message delete(String token) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        Optional<User> userOptional = userRepository.findById(idUser);

        if (userOptional.isEmpty()) {
            throw new AppException("Username does not exist", HttpStatus.NOT_FOUND);
        }
        userRepository.delete(userOptional.get());
        return new Message("Delete success", HttpStatus.NO_CONTENT);
    }
}