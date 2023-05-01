package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.entities.House;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.TypeService;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.ReceiptMapper;
import proyecto.web.serviceguideBackend.repositories.HouseRepository;
import proyecto.web.serviceguideBackend.repositories.ReceiptRepository;
import proyecto.web.serviceguideBackend.repositories.TypeServiceRepository;
import proyecto.web.serviceguideBackend.repositories.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final HouseRepository houseRepository;
    private final TypeServiceRepository typeServiceRepository;
    private final HouseService houseService;
    private final UserRepository userRepository;

    public ReceiptDto newReceipt(ReceiptDto receiptDto, User idUser) {
        Optional<House> optionalHouse = houseService.findByUserAndName(idUser, Objects.requireNonNull(receiptDto.getHouse()).getName());
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
        Optional<Receipt> optionalReceipt = receiptRepository.findByHouseAndReceiptName(optionalHouse.get(), receiptDto.getReceiptName());
        if (optionalReceipt.isPresent()) {
            throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
        }
        Optional<TypeService> optionalTypeService = typeServiceRepository.findByTypeIgnoreCase(Objects.requireNonNull(receiptDto.getTypeService()).getType());
        if (optionalTypeService.isEmpty()) {
            throw new AppException("Type service not found", HttpStatus.NOT_FOUND);
        }

        Receipt receipt = receiptMapper.serviceReceipt(receiptDto);
        receipt.setHouse(optionalHouse.get());
        receipt.setHouseName(optionalHouse.get().getName());
        receipt.setTypeService(optionalTypeService.get());

        Receipt receiptSaved = receiptRepository.save(receipt);
        return receiptMapper.serviceReceiptDto(receiptSaved);
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

    public List<List<Receipt>> findAllByUserId(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        Collection<House> optionalHouse = houseRepository.findByUser(optionalUser.get());
        List<List<Receipt>> receipts = new ArrayList<>();
        for(House house: optionalHouse) {
            receipts.add(house.getReceipts());
        }
        return receipts;
    }

    public Optional<Message> updateReceipt(ReceiptDto receiptDto, Long id) {
        return Optional.of(receiptRepository.findById(id)
                .map(receipt -> {
                    Optional<Receipt> optionalReceipt = receiptRepository.findById(id);
                    if (optionalReceipt.isEmpty()) {
                        throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<House> optionalHouse = houseRepository.findById(optionalReceipt.get().getHouse().getId());
                    if (optionalHouse.isEmpty()) {
                        throw new AppException("House not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<User> optionalUser = userRepository.findById(optionalHouse.get().getUser().getId());
                    if (optionalUser.isEmpty()) {
                        throw new AppException("User not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<House> houseOptional = houseService.findByUserAndName(optionalUser.get(), Objects.requireNonNull(receiptDto.getHouse()).getName());
                    if (houseOptional.isEmpty()) {
                        throw new AppException("House not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<Receipt> receiptOptional = receiptRepository.findByHouseAndReceiptName(houseOptional.get(), receiptDto.getReceiptName());
                    if (receiptOptional.isPresent()) {
                        throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
                    }
                    Optional<TypeService> optionalTypeService = typeServiceRepository.findByTypeIgnoreCase(Objects.requireNonNull(receiptDto.getTypeService()).getType());
                    if (optionalTypeService.isEmpty()) {
                        throw new AppException("Type service not found", HttpStatus.NOT_FOUND);
                    }
                    receipt.setReceiptName(receiptDto.getReceiptName());
                    receipt.setPrice(receiptDto.getPrice());
                    receipt.setAmount(receiptDto.getAmount());
                    receipt.setDate(receiptDto.getDate());
                    receipt.setTypeService(optionalTypeService.get());
                    receipt.setHouseName(houseOptional.get().getName());
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
