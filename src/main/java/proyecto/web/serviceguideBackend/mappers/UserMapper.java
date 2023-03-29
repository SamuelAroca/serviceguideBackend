package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.UserDto;
import proyecto.web.serviceguideBackend.entities.Users;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(Users users);
}
