package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.entities.Receipt;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {

    ReceiptDto serviceReceiptDto(Receipt receipt);

    Receipt serviceReceipt(ReceiptDto receiptDto);

}
