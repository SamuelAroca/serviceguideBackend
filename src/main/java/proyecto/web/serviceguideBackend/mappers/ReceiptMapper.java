package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.NewReceiptDto;
import proyecto.web.serviceguideBackend.entities.Receipt;

@Mapper(componentModel = "spring")
public interface ReceiptMapper {

    NewReceiptDto receiptDto(Receipt receipt);

    Receipt newReceipts(NewReceiptDto newReceiptDto);
}
