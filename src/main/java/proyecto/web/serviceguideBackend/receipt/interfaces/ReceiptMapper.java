package proyecto.web.serviceguideBackend.receipt.interfaces;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;

@Mapper(componentModel = "spring")
@Component
public interface ReceiptMapper {

    ReceiptDto serviceReceiptDto(Receipt receipt);

    Receipt serviceReceipt(ReceiptDto receiptDto);

}
