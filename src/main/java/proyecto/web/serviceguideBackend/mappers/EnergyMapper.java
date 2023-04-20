package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.EnergyDto;
import proyecto.web.serviceguideBackend.entities.EnergyReceipt;

@Mapper(componentModel = "spring")
public interface EnergyMapper {

    EnergyDto energyDto(EnergyReceipt energyReceipt);

    EnergyReceipt newEnergy(EnergyDto energyDto);

}

