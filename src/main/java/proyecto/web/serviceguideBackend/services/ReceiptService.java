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
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final HouseRepository houseRepository;
    private final TypeServiceRepository typeServiceRepository;

    public ReceiptDto newReceipt(ReceiptDto receiptDto) {
        Optional<Receipt> optionalReceipt = receiptRepository.findByReceiptName(receiptDto.getReceiptName());
        if (optionalReceipt.isPresent()) {
            throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
        }
        /*Optional<TypeService> optionalTypeService = typeServiceRepository.findByType(receiptDto.getTypeService());
        if (optionalTypeService.isEmpty()) {
            throw new AppException("TypeService not found", HttpStatus.NOT_FOUND);
            TOCA REVISAR
        }*/
        Optional<House> optionalHouse = houseRepository.findByName(Objects.requireNonNull(receiptDto.getHouse()).getName());
        if (optionalHouse.isPresent()) {
            Optional<House> houseOptional = houseRepository.findById(optionalHouse.get().getId());
            if (houseOptional.isEmpty()) {
                throw new AppException("House not found", HttpStatus.NOT_FOUND);
            }
            Optional<TypeService> serviceOptional = typeServiceRepository.findById(Objects.requireNonNull(receiptDto.getTypeService()).getId());
            if (serviceOptional.isEmpty()) {
                throw new AppException("TypeService not found", HttpStatus.NOT_FOUND);
            }

            Receipt receipt = receiptMapper.serviceReceipt(receiptDto);
            receipt.setTypeService(serviceOptional.get());
            receipt.setHouse(houseOptional.get());

            Receipt receiptSaved = receiptRepository.save(receipt);

            return receiptMapper.serviceReceiptDto(receiptSaved);
        } else {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
    }

    public Collection<Receipt> findByHouse(House house) {
        return receiptRepository.findByHouse(house);
    }

    public Collection<Receipt> findByTypeServiceAndHouse(TypeService typeService, House house) {
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
                        throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
                    }
                    Optional<TypeService> optionalTypeService = typeServiceRepository.findByType(receiptDto.getTypeService());
                    if (optionalTypeService.isEmpty()) {
                        throw new AppException("TypeService not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<House> optionalHouse = houseRepository.findByName(Objects.requireNonNull(receiptDto.getHouse()).getName());
                    if (optionalHouse.isEmpty()) {
                        throw new AppException("House not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<House> houseOptional = houseRepository.findById(optionalHouse.get().getId());
                    if (houseOptional.isEmpty()) {
                        throw new AppException("House not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<TypeService> serviceOptional = typeServiceRepository.findById(optionalTypeService.get().getId());
                    if (serviceOptional.isEmpty()) {
                        throw new AppException("TypeService not found", HttpStatus.NOT_FOUND);
                    }
                    receipt.setReceiptName(receiptDto.getReceiptName());
                    receipt.setPrice(receiptDto.getPrice());
                    receipt.setAmount(receiptDto.getAmount());
                    receipt.setDate(receiptDto.getDate());
                    receipt.setTypeService(serviceOptional.get());
                    receipt.setHouse(houseOptional.get());
                    receiptRepository.save(receipt);
                    return new Message("Receipt Updated successfully", HttpStatus.OK);

                }).orElseThrow(() -> new AppException("Receipt not found", HttpStatus.NOT_FOUND)));
    }

    public Message deleteReceipt(Long id) {
        Optional<Receipt> optionalReceipt = receiptRepository.findById(id);
        if (optionalReceipt.isEmpty()) {
            throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
        }
        receiptRepository.delete(optionalReceipt.get());
        return new Message("Received deleted successfully", HttpStatus.OK);
    }
}
