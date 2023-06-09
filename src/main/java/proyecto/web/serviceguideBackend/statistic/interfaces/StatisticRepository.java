package proyecto.web.serviceguideBackend.statistic.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.statistic.Statistic;

import java.util.List;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    @Query(value = "select s from Statistic s join s.receipts r on r.id = ?1")
    List<Statistic> getStatisticByReceipt(Long idReceipt);

}
