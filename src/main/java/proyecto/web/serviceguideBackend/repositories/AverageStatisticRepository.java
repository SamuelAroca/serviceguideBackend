package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.entities.AverageStatistic;
import proyecto.web.serviceguideBackend.entities.Receipt;

import java.util.Collection;

public interface AverageStatisticRepository extends JpaRepository<AverageStatistic, Long> {

    AverageStatistic findTopByOrderByTimestampDesc();

    @Query("select a from AverageStatistic a where a.houseName = ?1 order by a.timestamp DESC")
    AverageStatistic findTopByHouseNameOrderByTimestampDesc(String houseName);

}
