package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.NewReceiptDto;
import proyecto.web.serviceguideBackend.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.ReceiptMapper;
import proyecto.web.serviceguideBackend.repositories.ReceiptsRepository;
import proyecto.web.serviceguideBackend.repositories.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReceiptService {

    private final UserRepository userRepository;
    private final ReceiptsRepository receiptsRepository;
    private final ReceiptMapper receiptMapper;

    public ReceiptDto addNewReceipt(NewReceiptDto newReceiptDto) {
        Optional<User> optionalUser = userRepository.findByIdUser(newReceiptDto.getUser().getId());

        if (optionalUser.isEmpty()) {
            throw new AppException("User not exists", HttpStatus.BAD_REQUEST);
        }

        Receipt receipt = receiptMapper.newReceipts(newReceiptDto);
        receipt.setUser(optionalUser.get());

        Receipt savedReceipt = receiptsRepository.save(receipt);

        return receiptMapper.receiptDto(savedReceipt);
    }
}
