package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import proyecto.web.serviceguideBackend.dto.SignUpDto;
import proyecto.web.serviceguideBackend.dto.UserDto;
import proyecto.web.serviceguideBackend.entities.Users;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(Users users);

    @Mapping(target = "password", ignore = true)
    Users signUpToUser(SignUpDto userDto);
}
