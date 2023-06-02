package proyecto.web.serviceguideBackend.receipt.interfaces;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.ReceiptDto;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {

    ReceiptDto serviceReceiptDto(Receipt receipt);

    Receipt serviceReceipt(ReceiptDto receiptDto);

}
