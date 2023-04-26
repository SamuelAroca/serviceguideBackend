package proyecto.web.serviceguideBackend.mappers;

import org.mapstruct.Mapper;
import proyecto.web.serviceguideBackend.dto.ServiceReceiptDto;
import proyecto.web.serviceguideBackend.entities.ServiceReceipt;

@Mapper(componentModel = "spring")
public interface ServiceReceiptMapper {

    ServiceReceiptDto serviceReceiptDto(ServiceReceipt serviceReceipt);

    ServiceReceipt serviceReceipt(ServiceReceiptDto serviceReceiptDto);

}
