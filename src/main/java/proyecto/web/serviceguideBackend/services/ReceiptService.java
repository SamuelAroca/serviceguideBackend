package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
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
import proyecto.web.serviceguideBackend.serviceInterface.ReceiptInterface;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReceiptService implements ReceiptInterface {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final HouseRepository houseRepository;
    private final TypeServiceRepository typeServiceRepository;
    private final HouseService houseService;
    private final UserRepository userRepository;
    private final UserAuthenticationProvider authenticationProvider;

    @Override
    public ReceiptDto newReceipt(ReceiptDto receiptDto, String token) {
        Optional<House> optionalHouse = houseService.findByUserAndName(token, Objects.requireNonNull(receiptDto.getHouse()).getName());
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
        Optional<TypeService> typeService = typeServiceRepository.findByTypeIgnoreCase(Objects.requireNonNull(receiptDto.getTypeService()).getType());
        if (typeService.isEmpty()) {
            throw new AppException("Receipt Type not found", HttpStatus.NOT_FOUND);
        }
        Optional<Receipt> optionalReceipt = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), receiptDto.getReceiptName(),typeService.get());
        if (optionalReceipt.isPresent()) {
            throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
        }

        Receipt receipt = receiptMapper.serviceReceipt(receiptDto);
        receipt.setHouse(optionalHouse.get());
        receipt.setHouseName(optionalHouse.get().getName());
        receipt.setTypeService(typeService.get());
        Date date = new Date();
        receipt.setCreationDate(date);

        Receipt receiptSaved = receiptRepository.save(receipt);
        return receiptMapper.serviceReceiptDto(receiptSaved);
    }

    @Override
    public Collection<Receipt> findByHouse(String house, String token) {
        Long id = authenticationProvider.whoIsMyId(token);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        Optional<House> optionalHouse = houseRepository.findByUserAndName(optionalUser.get(), house);
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
        return receiptRepository.findByHouse(optionalHouse.get());
    }

    @Override
    public Collection<Receipt> findByTypeServiceAndHouse(String typeService, String house, String token) {
        Long id = authenticationProvider.whoIsMyId(token);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        Optional<TypeService> optionalTypeService = typeServiceRepository.findByTypeIgnoreCase(typeService);
        if (optionalTypeService.isEmpty()) {
            throw new AppException("Type service not found", HttpStatus.NOT_FOUND);
        }
        Optional<House> optionalHouse = houseRepository.findByUserAndName(optionalUser.get(), house);
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }
        return receiptRepository.findByTypeServiceAndHouse(optionalTypeService.get(), optionalHouse.get());
    }

    @Override
    public List<List<Receipt>> findAllByUserId(String token) {
        Long user = authenticationProvider.whoIsMyId(token);
        Optional<User> optionalUser = userRepository.findById(user);
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

    @Override
    public List<Receipt> allReceiptsByUserId(String token) {
        Long id = authenticationProvider.whoIsMyId(token);
        return receiptRepository.getReceiptByUser(id);
    }

    @Override
    public Optional<Message> updateReceipt(ReceiptDto receiptDto, Long id, String token) {
        return Optional.of(receiptRepository.findById(id)
                .map(receipt -> {
                    Optional<TypeService> typeService = typeServiceRepository.findByTypeIgnoreCase(Objects.requireNonNull(receiptDto.getTypeService()).getType());
                    if (typeService.isEmpty()) {
                        throw new AppException("Receipt Type not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<Receipt> optionalReceipt = receiptRepository.findById(id);
                    if (optionalReceipt.isEmpty()) {
                        throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<House> optionalHouse = houseRepository.findById(optionalReceipt.get().getHouse().getId());
                    if (optionalHouse.isEmpty()) {
                        throw new AppException("House not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<House> houseOptional = houseService.findByUserAndName(token, Objects.requireNonNull(receiptDto.getHouse()).getName());
                    if (houseOptional.isEmpty()) {
                        throw new AppException("House not found", HttpStatus.NOT_FOUND);
                    }
                    Optional<Receipt> receiptOptional = receiptRepository.findByHouseAndReceiptNameAndTypeService(houseOptional.get(), receiptDto.getReceiptName(), typeService.get());
                    if (receiptOptional.isPresent()) {
                        throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
                    }
                    receipt.setReceiptName(receiptDto.getReceiptName());
                    receipt.setPrice(receiptDto.getPrice());
                    receipt.setAmount(receiptDto.getAmount());
                    receipt.setDate(receiptDto.getDate());
                    receipt.setTypeService(typeService.get());
                    receipt.setHouseName(houseOptional.get().getName());
                    receipt.setHouse(houseOptional.get());
                    receiptRepository.save(receipt);
                    return new Message("Receipt Updated successfully", HttpStatus.OK);
                }).orElseThrow(() -> new AppException("Receipt not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public Message deleteReceipt(Long id) {
        Optional<Receipt> optionalReceipt = receiptRepository.findById(id);
        if (optionalReceipt.isEmpty()) {
            throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
        }
        receiptRepository.delete(optionalReceipt.get());
        return new Message("Received deleted successfully", HttpStatus.OK);
    }
}
