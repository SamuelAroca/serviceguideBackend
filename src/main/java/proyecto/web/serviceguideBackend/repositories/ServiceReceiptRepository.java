package proyecto.web.serviceguideBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.web.serviceguideBackend.entities.ServiceReceipt;

public interface ServiceReceiptRepository extends JpaRepository<ServiceReceipt, Long> {
}
