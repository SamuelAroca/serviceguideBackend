package proyecto.web.serviceguideBackend.house.interfaces;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.HouseDto;

@Mapper(componentModel = "spring")
public interface HouseMapper {

    HouseDto houseDto(House house);

    @Mapping(target = "receipts", ignore = true)
    House newHouse(HouseDto houseDto);
}
