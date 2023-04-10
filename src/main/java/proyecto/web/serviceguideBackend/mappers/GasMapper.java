package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.GasDto;
import proyecto.web.serviceguideBackend.entities.Gas;

@Mapper(componentModel = "spring")
public interface GasMapper {

    GasDto gasDto(Gas gas);

    Gas newGas(GasDto gasDto);
}
