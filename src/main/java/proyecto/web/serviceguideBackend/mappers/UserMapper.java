package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import proyecto.web.serviceguideBackend.dto.SignUpDto;
import proyecto.web.serviceguideBackend.dto.UserDto;
import proyecto.web.serviceguideBackend.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "token", ignore = true)
    UserDto toUserDto(User user);

    @Mapping(target = "waterReceipts", ignore = true)
    @Mapping(target = "sewerageReceipt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "gasReceipts", ignore = true)
    @Mapping(target = "energyReceipts", ignore = true)
    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

}
