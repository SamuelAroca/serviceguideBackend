package proyecto.web.serviceguideBackend.receipt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeService;
import proyecto.web.serviceguideBackend.house.HouseService;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptInterface;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptMapper;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptRepository;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeServiceRepository;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

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

        Calendar newReceiptCalendar = Calendar.getInstance();
        newReceiptCalendar.setTime(receiptDto.getDate());
        int receiptMonth = newReceiptCalendar.get(Calendar.MONTH) + 1;
        int receiptYear = newReceiptCalendar.get(Calendar.YEAR);

        List<Receipt> existingReceipts = receiptRepository.findByHouseAndTypeServiceAndMonthAndYear(optionalHouse.get(), typeService.get(), receiptMonth, receiptYear);
        if (!existingReceipts.isEmpty()) {
            throw new AppException("Receipt already exists for the given month and year", HttpStatus.BAD_REQUEST);
        }

        Optional<Receipt> optionalReceipt = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), receiptDto.getReceiptName(),typeService.get());
        if (optionalReceipt.isPresent()) {
            throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
        }

        Receipt receipt = receiptMapper.serviceReceipt(receiptDto);
        receipt.setHouse(optionalHouse.get());
        receipt.setHouseName(optionalHouse.get().getName());
        receipt.setTypeService(typeService.get());

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
    public List<Receipt> allReceiptsByUserId(String token) {
        Long id = authenticationProvider.whoIsMyId(token);
        return receiptRepository.getReceiptByUser(id);
    }

    @Override
    public Optional<Receipt> findById(Long id) {
        Optional<Receipt> optionalReceipt = receiptRepository.findById(id);
        if (optionalReceipt.isEmpty()) {
            throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
        }
        return optionalReceipt;
    }

    @Override
    public Optional<Receipt> getLastReceipt(String token) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        return receiptRepository.getLastReceipt(idUser);
    }

    @Override
    public Optional<Message> updateReceipt(ReceiptDto receiptDto, Long idReceipt) {
        return Optional.of(receiptRepository.findById(idReceipt)
                .map(receipt -> {
                    Optional<TypeService> optionalTypeService = typeServiceRepository.findByTypeIgnoreCase(receiptDto.getTypeService().getType());
                    if (optionalTypeService.isEmpty()) {
                        throw new AppException("Type Receipt Not Found", HttpStatus.NOT_FOUND);
                    }
                    Long idUser = receiptRepository.findUserByReceiptId(idReceipt);
                    Optional<User> optionalUser = userRepository.findById(idUser);
                    if (optionalUser.isEmpty()) {
                        throw new AppException("User Not Found", HttpStatus.NOT_FOUND);
                    }
                    Optional<House> optionalHouse = houseRepository.findByUserAndName(optionalUser.get(), receiptDto.getHouse().getName());
                    if (optionalHouse.isEmpty()) {
                        throw new AppException("House Not Found", HttpStatus.NOT_FOUND);
                    }
                    Optional<Receipt> optionalReceipt = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), receiptDto.getReceiptName(), optionalTypeService.get());
                    if (optionalReceipt.isPresent()) {
                        if (optionalReceipt.get().getReceiptName().equals(receiptDto.getReceiptName())) {
                            receipt.setReceiptName(receiptDto.getReceiptName());
                            receipt.setPrice(receiptDto.getPrice());
                            receipt.setAmount(receiptDto.getAmount());
                            receipt.setDate(receiptDto.getDate());
                            receipt.setTypeService(optionalTypeService.get());
                            receipt.setHouse(optionalHouse.get());
                            receipt.setHouseName(optionalHouse.get().getName());
                            receiptRepository.save(receipt);
                            return new Message("Receipt Updated successfully", HttpStatus.OK);
                        }
                        throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
                    }
                    receipt.setReceiptName(receiptDto.getReceiptName());
                    receipt.setPrice(receiptDto.getPrice());
                    receipt.setAmount(receiptDto.getAmount());
                    receipt.setDate(receiptDto.getDate());
                    receipt.setTypeService(optionalTypeService.get());
                    receipt.setHouse(optionalHouse.get());
                    receipt.setHouseName(optionalHouse.get().getName());
                    receiptRepository.save(receipt);
                    return new Message("Receipt Updated successfully", HttpStatus.OK);
                }).orElseThrow(() -> new AppException("Receipt not found", HttpStatus.NOT_FOUND)));
    }

    @Override
    public Collection<Receipt> getAllReceiptsByType(String token, String type) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        return receiptRepository.getAllReceiptsByType(idUser, type);
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

    @Override
    public Long getTwoReceiptById(Long idReceipt) {
        return receiptRepository.findUserByReceiptId(idReceipt);
    }

    @Override
    public Long findIdByName(String name) {
        return receiptRepository.findIdByName(name);
    }

    @Override
    public Collection<Receipt> getAllReceiptsByHouse(String token, String houseName) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        return receiptRepository.getAllReceiptsByHouse(idUser, houseName);
    }

    @Override
    public Collection<Receipt> getReceiptByHouse(String houseName) {
        return receiptRepository.findReceiptByHouse(houseName);
    }
}
