package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.UserDto;

public interface AuthInterface {
    UserDto findByEmail(String email);
}
