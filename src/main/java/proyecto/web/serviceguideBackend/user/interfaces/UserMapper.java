package proyecto.web.serviceguideBackend.user.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import proyecto.web.serviceguideBackend.auth.SignUpDto;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "token", ignore = true)
    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

}
