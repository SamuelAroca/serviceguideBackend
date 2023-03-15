package proyecto.web.serviceguideBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import proyecto.web.serviceguideBackend.entity.PublicServicesReceipt;

@EnableJpaRepositories
@Repository
public interface PublicServicesReceiptRepository extends JpaRepository<PublicServicesReceipt, Long> {
}
