package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.entities.Water;

@Mapper(componentModel = "spring")
public interface WaterMapper {

    WaterDto waterDto (Water water);

    Water newWater(WaterDto waterDto);

}
