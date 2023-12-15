package proyecto.web.serviceguideBackend.receipt.interfaces;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReceiptInterface {

    ReceiptDto newReceipt(ReceiptDto receiptDto, Long idUser);
    List<Receipt> allReceiptsByUserId(Long idUser);
    Optional<Receipt> getLastReceipt(Long idUser);
    Optional<Message> updateReceipt(ReceiptDto receiptDto, Long id);
    Message deleteReceipt(Long id);
    Long getTwoReceiptById(Long idReceipt);
    Message extractReceiptInformation(String receiptText);
    Date formatDate(String date);
    Message readPDF(MultipartFile multipartFile);
    List<Receipt> receiptCollection(Long idUser, Pageable pageable);

}
