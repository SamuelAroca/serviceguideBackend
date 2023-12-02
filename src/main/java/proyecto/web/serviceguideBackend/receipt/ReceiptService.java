package proyecto.web.serviceguideBackend.receipt;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ReceiptService implements ReceiptInterface {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final HouseRepository houseRepository;
    private final TypeServiceRepository typeServiceRepository;
    private final HouseService houseService;
    private final UserRepository userRepository;

    @Override
    public ReceiptDto newReceipt(ReceiptDto receiptDto, Long idUser) {
        Optional<House> optionalHouse = houseService.findByUserAndName(idUser, Objects.requireNonNull(receiptDto.getHouse()).getName());
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
    public List<Receipt> allReceiptsByUserId(Long idUser) {
        return receiptRepository.getReceiptByUser(idUser);
    }

    @Override
    public Optional<Receipt> getLastReceipt(Long idUser) {
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
                            if (!optionalReceipt.get().getDate().equals(receiptDto.getDate())) {
                                Calendar newReceiptCalendar = Calendar.getInstance();
                                newReceiptCalendar.setTime(receiptDto.getDate());
                                int receiptMonth = newReceiptCalendar.get(Calendar.MONTH) + 1;
                                int receiptYear = newReceiptCalendar.get(Calendar.YEAR);

                                List<Receipt> existingReceipts = receiptRepository.findByHouseAndTypeServiceAndMonthAndYear(optionalHouse.get(), optionalTypeService.get(), receiptMonth, receiptYear);
                                if (!existingReceipts.isEmpty()) {
                                    throw new AppException("Receipt already exists for the given month and year", HttpStatus.BAD_REQUEST);
                                }
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
    public String extraerInformacionFactura(String textoFactura) {
        String patronAcueducto = "Acueducto (.*?)\\n[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronAlcantarillado = "Alcantarillado (.*?)\\$ (\\d[\\d.,]*)";
        String patronEnergia = "Energía (.*?)\\$ (\\d[\\d.,]*)";
        String patronGas = "Gas (.*?)\\$ (\\d[\\d.,]*)";

        // Crear los objetos de patrón
        Pattern patternAcueducto = Pattern.compile(patronAcueducto);
        Pattern patternAlcantarillado = Pattern.compile(patronAlcantarillado);
        Pattern patternEnergia = Pattern.compile(patronEnergia);
        Pattern patternGas = Pattern.compile(patronGas);

        // Crear el objeto Matcher
        Matcher matcherWater = patternAcueducto.matcher(textoFactura);
        Matcher matcherEnergy = patternEnergia.matcher(textoFactura);
        Matcher matcherSewerage = patternAlcantarillado.matcher(textoFactura);
        Matcher matcherGas = patternGas.matcher(textoFactura);

        // Extraer información
        StringBuilder resultado = new StringBuilder();
        while (matcherWater.find()) {
            String concepto = matcherWater.group(1);
            String valor = matcherWater.group(2);
            resultado.append("Concepto Agua: ").append(concepto).append(", Valor: $").append(valor).append("\n");
        }

        while (matcherEnergy.find()) {
            String concepto = matcherEnergy.group(1);
            String valor = matcherEnergy.group(2);
            resultado.append("Concepto Energia: ").append(concepto).append(", Valor: $").append(valor).append("\n");
        }

        while (matcherSewerage.find()) {
            String concepto = matcherSewerage.group(1);
            String valor = matcherSewerage.group(2);
            resultado.append("Concepto Alcantarillado: ").append(concepto).append(", Valor: $").append(valor).append("\n");
        }

        while (matcherGas.find()) {
            String concepto = matcherGas.group(1);
            String valor = matcherGas.group(2);
            resultado.append("Concepto Gas: ").append(concepto).append(", Valor: $").append(valor).append("\n");
        }

        // Puedes agregar más patrones y lógica según sea necesario
        System.out.println(resultado);
        return resultado.toString();
    }

    @Override
    public String readPDF(MultipartFile archivoPdf) {
        try {
            PdfReader pdfReader = new PdfReader(archivoPdf.getInputStream());

            // Extraer el texto de la página 2
            String textoPagina = PdfTextExtractor.getTextFromPage(pdfReader, 2);

            // Cerrar el lector de PDF
            pdfReader.close();
            System.out.println("Texto de la factura:\n" + textoPagina);
            return extraerInformacionFactura(textoPagina);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error al procesar el archivo PDF";
        }
    }
}
