package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.EnergyDto;
import proyecto.web.serviceguideBackend.entities.Energy;

@Mapper(componentModel = "spring")
public interface EnergyMapper {

    EnergyDto energyDto(Energy energy);

    Energy newEnergy(EnergyDto energyDto);

}
