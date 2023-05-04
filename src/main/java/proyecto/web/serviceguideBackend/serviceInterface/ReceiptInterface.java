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
    Collection<Receipt> findByHouse(String house, String token);
    Collection<Receipt> findByTypeServiceAndHouse(String typeService, String house, String token);
    List<List<Receipt>> findAllByUserId(String token);

    List<Receipt> allReceiptsByUserId(String token);
    Optional<Message> updateReceipt(ReceiptDto receiptDto, Long id, String token);
    Message deleteReceipt(Long id);

}
