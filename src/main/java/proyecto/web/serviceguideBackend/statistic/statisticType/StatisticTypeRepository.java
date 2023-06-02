package proyecto.web.serviceguideBackend.statistic.statisticType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatisticTypeRepository extends JpaRepository<StatisticType, Long> {

    Optional<StatisticType> findByTypeIgnoreCase(String type);
}
