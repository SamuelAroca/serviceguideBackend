package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.SewerageDto;
import proyecto.web.serviceguideBackend.entities.SewerageReceipt;

@Mapper(componentModel = "spring")
public interface SewerageMapper {

    SewerageDto sewerageDto(SewerageReceipt sewerageReceipt);

    SewerageReceipt newSewerage(SewerageDto sewerageDto);

}
