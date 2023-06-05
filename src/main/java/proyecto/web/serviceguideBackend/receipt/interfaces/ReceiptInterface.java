package proyecto.web.serviceguideBackend.receipt.interfaces;

import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReceiptInterface {

    ReceiptDto newReceipt(ReceiptDto receiptDto, String token);
    Collection<Receipt> findByHouse(String house, String token);
    Collection<Receipt> findByTypeServiceAndHouse(String typeService, String house, String token);
    List<Receipt> allReceiptsByUserId(String token);
    Optional<Receipt> findById(Long id);
    Optional<Receipt> getLastReceipt(String token);
    Optional<Message> updateReceipt(ReceiptDto receiptDto, Long id);
    Collection<Receipt> getAllReceiptsByType(String token, String type);
    Message deleteReceipt(Long id);
    Long getTwoReceiptById(Long idReceipt);
    Long findIdByName(String name);
    Collection<Receipt> getAllReceiptsByHouse(String token, String houseName);
    Collection<Receipt> getReceiptByHouse(String houseName);

}
