package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.GasDto;
import proyecto.web.serviceguideBackend.entities.GasReceipt;

@Mapper(componentModel = "spring")
public interface GasMapper {

    GasDto gasDto(GasReceipt gasReceipt);

    GasReceipt newGas(GasDto gasDto);

}
