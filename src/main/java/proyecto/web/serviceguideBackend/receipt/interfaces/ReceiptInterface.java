package proyecto.web.serviceguideBackend.receipt.interfaces;

import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReceiptInterface {

    ReceiptDto newReceipt(ReceiptDto receiptDto, Long idUser);
    Collection<Receipt> findByHouse(String house, Long idUser);
    Collection<Receipt> findByTypeServiceAndHouse(String typeService, String house, Long idUser);
    List<Receipt> allReceiptsByUserId(Long idUser);
    Optional<Receipt> findById(Long id);
    Optional<Receipt> getLastReceipt(Long idUser);
    Optional<Message> updateReceipt(ReceiptDto receiptDto, Long id);
    Collection<Receipt> getAllReceiptsByType(Long idUser, String type);
    Message deleteReceipt(Long id);
    Long getTwoReceiptById(Long idReceipt);
    Long findIdByName(String name);
    Collection<Receipt> getAllReceiptsByHouse(Long idUser, String houseName);
    Collection<Receipt> getReceiptByHouse(String houseName);

}
