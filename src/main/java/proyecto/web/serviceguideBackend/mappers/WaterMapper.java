package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.WaterDto;
import proyecto.web.serviceguideBackend.entities.WaterReceipt;

@Mapper(componentModel = "spring")
public interface WaterMapper {

    WaterDto waterDto(WaterReceipt waterReceipt);

    WaterReceipt newWater(WaterDto waterDto);

}
