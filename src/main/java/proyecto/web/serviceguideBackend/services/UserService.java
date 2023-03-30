package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.CredentialsDto;
import proyecto.web.serviceguideBackend.dto.SignUpDto;
import proyecto.web.serviceguideBackend.dto.UserDto;
import proyecto.web.serviceguideBackend.entities.Users;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.UserMapper;
import proyecto.web.serviceguideBackend.repositories.UserRepository;

import java.nio.CharBuffer;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto findByLogin(String login) {
        Users users = userRepository.findByLogin(login)
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(users);
    }

    public UserDto login(CredentialsDto credentialsDto) {
        Users users = userRepository.findByLogin(credentialsDto.getLogin())
                .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), users.getPassword())) {
            return userMapper.toUserDto(users);
        }

        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDto register(SignUpDto userDto) {
        Optional<Users> optionalUsers = userRepository.findByLogin(userDto.getLogin());

        if (optionalUsers.isPresent()) {
            throw new AppException("Login already exist", HttpStatus.BAD_REQUEST);
        }

        Users users = userMapper.signUpToUser(userDto);

        users.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));

        Users savedUser = userRepository.save(users);

        return userMapper.toUserDto(users);
    }
}
