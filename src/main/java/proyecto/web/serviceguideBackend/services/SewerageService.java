package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.SewerageDto;
import proyecto.web.serviceguideBackend.entities.SewerageReceipt;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.SewerageMapper;
import proyecto.web.serviceguideBackend.repositories.SewerageRepository;
import proyecto.web.serviceguideBackend.repositories.UserRepository;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SewerageService {

    private final UserRepository userRepository;
    private final SewerageRepository sewerageRepository;
    private final SewerageMapper sewerageMapper;

    public SewerageDto newSewerage(SewerageDto sewerageDto) {
        Optional<User> optionalUser = userRepository.findById(sewerageDto.getUser().getId());

        if (optionalUser.isEmpty()) {
            throw new AppException("User does not exist", HttpStatus.NOT_FOUND);
        }

        SewerageReceipt sewerageReceipt = sewerageMapper.newSewerage(sewerageDto);
        sewerageReceipt.setUser(optionalUser.get());

        SewerageReceipt sewerageReceiptSaved = sewerageRepository.save(sewerageReceipt);

        return sewerageMapper.sewerageDto(sewerageReceiptSaved);
    }

    public Collection<SewerageReceipt> listAll() {
        return sewerageRepository.findAll();
    }

    public Collection<SewerageReceipt> findAllByUser(User userId) {
        return sewerageRepository.findAllByUser(userId);
    }
}
