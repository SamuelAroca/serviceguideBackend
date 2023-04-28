package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.TypeService;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.ReceiptMapper;
import proyecto.web.serviceguideBackend.repositories.HouseRepository;
import proyecto.web.serviceguideBackend.repositories.ReceiptRepository;
import proyecto.web.serviceguideBackend.repositories.TypeServiceRepository;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final HouseRepository houseRepository;
    private final TypeServiceRepository typeServiceRepository;

    public ReceiptDto newReceipt(ReceiptDto receiptDto) {
        TypeService typeService = receiptDto.getTypeService();
        House house = receiptDto.getHouse();
        Optional<TypeService> optionalType = typeServiceRepository.findByTypeOrId(typeService, typeService);
        Optional<House> optionalHouse = houseRepository.findByNameOrId(house, house);
        Optional<Receipt> optionalReceipt = receiptRepository.findByReceiptName(receiptDto.getReceiptName());
        if (optionalReceipt.isPresent()) {
            throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
        }
        if (optionalType.isEmpty()) {
            throw new AppException("Type does not exist", HttpStatus.NOT_FOUND);
        }
        if (optionalHouse.isEmpty()) {
            throw new AppException("House does not exist", HttpStatus.NOT_FOUND);
        }
        Receipt receipt = receiptMapper.serviceReceipt(receiptDto);
        receipt.setTypeService(optionalType.get());
        receipt.setHouse(optionalHouse.get());

        Receipt receiptSaved = receiptRepository.save(receipt);

        return receiptMapper.serviceReceiptDto(receiptSaved);
    }

    public Collection<Receipt> findByHouse(House house) {
        return receiptRepository.findByHouse(house);
    }

    public Collection<Receipt> findByTypeService(TypeService typeService, House house) {
        return receiptRepository.findByTypeServiceAndHouse(typeService, house);
    }

    public Collection<Receipt> findAllById(Long id) {
        return receiptRepository.findAllById(id);
    }

    public Optional<Message> updateReceipt(ReceiptDto receiptDto, Long id) {
        return Optional.of(receiptRepository.findById(id)
                .map(receipt -> {
                    Optional<Receipt> optionalReceipt = receiptRepository.findByReceiptName(receiptDto.getReceiptName());
                    if (optionalReceipt.isPresent()) {
                        receipt.setReceiptName(receiptDto.getReceiptName());
                        receipt.setPrice(receiptDto.getPrice());
                        receipt.setAmount(receiptDto.getAmount());
                        receipt.setDate(receiptDto.getDate());
                        receipt.setTypeService(receiptDto.getTypeService());
                        receipt.setHouse(receiptDto.getHouse());
                        receiptRepository.save(receipt);
                        return new Message("User updated successfully");
                    } else {
                        throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
                    }
                }).orElseThrow(() -> new AppException("Receipt not found", HttpStatus.NOT_FOUND)));
    }

    public Message deleteReceipt(Long id) {
        Optional<Receipt> optionalReceipt = receiptRepository.findById(id);
        if (optionalReceipt.isEmpty()) {
            throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
        }
        receiptRepository.delete(optionalReceipt.get());
        return new Message("Received deleted successfully");
    }
}
