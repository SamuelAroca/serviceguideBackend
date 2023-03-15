package proyecto.web.serviceguideBackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import proyecto.web.serviceguideBackend.dto.PublicServicesReceiptDTO;
import proyecto.web.serviceguideBackend.service.PublicServicesReceiptService;

@RestController
@CrossOrigin
@RequestMapping(path = "api/receipts")
public class PublicServicesReceiptController {

    @Autowired
    private PublicServicesReceiptService publicServicesReceiptService;

    @PostMapping(path = "/save")
    public String saveReceipt(@RequestBody PublicServicesReceiptDTO receiptDTO) {
        return publicServicesReceiptService.addReceipt(receiptDTO);
    }
}
