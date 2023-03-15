package proyecto.web.serviceguideBackend.service;

import proyecto.web.serviceguideBackend.dto.LoginDTO;
import proyecto.web.serviceguideBackend.dto.UserDTO;
import proyecto.web.serviceguideBackend.response.LoginResponse;

public interface UserService {

    String addUser(UserDTO userDTO);

    LoginResponse loginUser(LoginDTO loginDTO);
}
