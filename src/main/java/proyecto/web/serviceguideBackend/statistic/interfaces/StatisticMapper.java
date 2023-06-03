package proyecto.web.serviceguideBackend.statistic.interfaces;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import proyecto.web.serviceguideBackend.statistic.Statistic;
import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;

@Mapper(componentModel = "spring")
@Component
public interface StatisticMapper {

    StatisticDto statisticDto(Statistic statistic);

    Statistic statistic(StatisticDto statisticDto);
}
