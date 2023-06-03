package proyecto.web.serviceguideBackend.statistic.interfaces;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.statistic.Statistic;
import proyecto.web.serviceguideBackend.statistic.StatisticDto;

@Mapper(componentModel = "spring")
public interface StatisticMapper {

    StatisticDto statisticDto(Statistic statistic);

    Statistic statistic(StatisticDto statisticDto);
}
