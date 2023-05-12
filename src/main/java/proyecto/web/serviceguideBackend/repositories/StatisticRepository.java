package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import proyecto.web.serviceguideBackend.entities.Statistic;

import java.util.Optional;

public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    @Query(value = "select s from Statistic s inner join Receipt r on r.id = s.receipt.id where r.id = ?1")
    Optional<Statistic> statistic(Long idReceipt);
}
