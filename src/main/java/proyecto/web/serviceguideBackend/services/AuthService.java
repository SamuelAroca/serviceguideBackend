package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.UserDto;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.UserMapper;
import proyecto.web.serviceguideBackend.repositories.UserRepository;
import proyecto.web.serviceguideBackend.serviceInterface.AuthInterface;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Wrong email or password", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }
}
