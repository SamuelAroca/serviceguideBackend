package proyecto.web.serviceguideBackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.PublicServicesReceiptDTO;
import proyecto.web.serviceguideBackend.entity.PublicServicesReceipt;
import proyecto.web.serviceguideBackend.repository.PublicServicesReceiptRepository;
import proyecto.web.serviceguideBackend.service.PublicServicesReceiptService;

@Service
public class PublicServicesReceiptImpl implements PublicServicesReceiptService {

    @Autowired
    PublicServicesReceiptRepository publicServicesReceiptRepository;

    @Override
    public String addReceipt(PublicServicesReceiptDTO publicServicesReceiptDTO) {
        PublicServicesReceipt publicServicesReceipt = new PublicServicesReceipt(
                publicServicesReceiptDTO.getId(),
                publicServicesReceiptDTO.getWaterPrice(),
                publicServicesReceiptDTO.getSewerPrice(),
                publicServicesReceiptDTO.getElectricityPrice(),
                publicServicesReceiptDTO.getGasPrice()
        );

        publicServicesReceiptRepository.save(publicServicesReceipt);

        return "Receipt saved successfully";
    }
}
