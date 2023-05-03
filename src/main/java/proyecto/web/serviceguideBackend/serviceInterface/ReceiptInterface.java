package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.TypeService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReceiptInterface {

    ReceiptDto newReceipt(ReceiptDto receiptDto, String token);
    Collection<Receipt> findByHouse(House house);
    Collection<Receipt> findByTypeServiceAndHouse(TypeService typeService, House house);
    List<List<Receipt>> findAllByUserId(String token);
    Optional<Message> updateReceipt(ReceiptDto receiptDto, Long id);
    Message deleteReceipt(Long id);

}
