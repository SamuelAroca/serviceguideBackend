package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.SewerageDto;
import proyecto.web.serviceguideBackend.entities.Sewerage;

@Mapper(componentModel = "spring")
public interface SewerageMapper {

    SewerageDto waterDto(Sewerage sewerage);

    Sewerage newSewerage(SewerageDto sewerageDto);

}
