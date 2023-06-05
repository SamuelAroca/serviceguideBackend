package proyecto.web.serviceguideBackend.house.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.dto.HouseDto;

@Mapper(componentModel = "spring")
@Component
public interface HouseMapper {

    HouseDto houseDto(House house);

    @Mapping(target = "receipts", ignore = true)
    House newHouse(HouseDto houseDto);

}
