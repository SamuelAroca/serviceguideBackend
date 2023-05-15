package proyecto.web.serviceguideBackend.serviceInterface;

import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.entities.Receipt;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReceiptInterface {

    ReceiptDto newReceipt(ReceiptDto receiptDto, String token);
    Collection<Receipt> findByHouse(String house, String token);
    Collection<Receipt> findByTypeServiceAndHouse(String typeService, String house, String token);
    List<List<Receipt>> findAllByUserId(String token);
    List<Receipt> allReceiptsByUserId(String token);
    Optional<Receipt> findById(Long id);
    Optional<Receipt> getLastReceipt(String token);
    Optional<Message> updateReceipt(ReceiptDto receiptDto, Long id);
    Collection<Receipt> getAllReceiptsByType(String token, String type);
    Message deleteReceipt(Long id);
    Long getTwoReceiptById(Long idReceipt);

}
