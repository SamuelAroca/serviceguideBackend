package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Statistic;

@Mapper(componentModel = "spring")
public interface StatisticMapper {

    StatisticDto statisticDto(Statistic statistic);

    Statistic statistic(StatisticDto statisticDto);
}
