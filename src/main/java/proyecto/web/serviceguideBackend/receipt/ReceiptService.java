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

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
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
        String patronAcueducto = "Acueducto (\\d[\\d.,]*) m3\\n[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronAlcantarillado = "Alcantarillado (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronEnergia = "Energía (\\d[\\d.,]*) kwh[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronGas = "Gas (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronDate = "(\\d{1,2}-[a-zA-Z]{3}-\\d{4})";
        String patronNumeroContrato = "Contrato (\\d+)";
        String patronReceiptName = "Factura [A-Za-z]+ de \\d{4}";

        // Crear los objetos de patrón
        Pattern patternAcueducto = Pattern.compile(patronAcueducto);
        Pattern patternAlcantarillado = Pattern.compile(patronAlcantarillado);
        Pattern patternEnergia = Pattern.compile(patronEnergia);
        Pattern patternGas = Pattern.compile(patronGas);
        Pattern patternDate = Pattern.compile(patronDate);
        Pattern patternContract = Pattern.compile(patronNumeroContrato);
        Pattern patternReceiptName = Pattern.compile(patronReceiptName);

        // Crear el objeto Matcher
        Matcher matcherWater = patternAcueducto.matcher(textoFactura);
        Matcher matcherEnergy = patternEnergia.matcher(textoFactura);
        Matcher matcherSewerage = patternAlcantarillado.matcher(textoFactura);
        Matcher matcherGas = patternGas.matcher(textoFactura);
        Matcher matcherDate = patternDate.matcher(textoFactura);
        Matcher matcherContract = patternContract.matcher(textoFactura);
        Matcher matcherReceiptName = patternReceiptName.matcher(textoFactura);

        float amountWater = 0;
        double priceWater = 0;
        while (matcherWater.find()) {
            String concepto = matcherWater.group(1).replaceAll("[^\\d.]", "."); // Eliminar no dígitos ni puntos
            String valor = matcherWater.group(2).replaceAll("[^\\d.,]", ""); // Eliminar no dígitos ni puntos ni comas

            concepto = concepto.replace(".", "").replace(",", ".");
            valor = valor.replace(".", "").replace(",", ".");

            amountWater = Float.parseFloat(concepto);
            priceWater = Double.parseDouble(valor);
        }

        float amountEnergy = 0;
        double priceEnergy = 0;
        while (matcherEnergy.find()) {
            String concepto = matcherEnergy.group(1).replaceAll("[^\\d.]", "."); // Eliminar no dígitos ni puntos
            String valor = matcherEnergy.group(2).replaceAll("[^\\d.,]", ""); // Eliminar no dígitos ni puntos ni comas

            concepto = concepto.replace(".", "");
            valor = valor.replace(".", "").replace(",", ".");

            amountEnergy = Float.parseFloat(concepto);
            priceEnergy = Double.parseDouble(valor);
        }

        float amountSewerage = 0;
        double priceSewerage = 0;
        while (matcherSewerage.find()) {
            String concepto = matcherSewerage.group(1).replaceAll("[^\\d.]", "."); // Eliminar no dígitos ni puntos
            String valor = matcherSewerage.group(2).replaceAll("[^\\d.,]", ""); // Eliminar no dígitos ni puntos ni comas

            concepto = concepto.replace(".", "");
            valor = valor.replace(".", "").replace(",", ".");

            amountSewerage = Float.parseFloat(concepto);
            priceSewerage = Double.parseDouble(valor);
        }

        float amountGas = 0;
        double priceGas = 0;
        while (matcherGas.find()) {
            String concepto = matcherGas.group(1).replaceAll("[^\\d.]", "."); // Eliminar no dígitos ni puntos
            String valor = matcherGas.group(2).replaceAll("[^\\d.,]", ""); // Eliminar no dígitos ni puntos ni comas

            valor = valor.replace(".", "").replace(",", ".");

            amountGas = Float.parseFloat(concepto);
            priceGas = Double.parseDouble(valor);
        }

        Date fechaFormateada;
        if (matcherDate.find()) {
            String fechaEncontrada = matcherDate.group(1);
            fechaFormateada = formatDate(fechaEncontrada);
        } else {
            throw new AppException("No se encontró la fecha en el texto.", HttpStatus.BAD_REQUEST);
        }

        String receiptName = null;
        if (matcherReceiptName.find()) {
            receiptName = matcherReceiptName.group(0);
        }

        String contract = null;
        if (matcherContract.find()) {
            contract = matcherContract.group(1);
        }

        Receipt receiptWater = new Receipt();
        Receipt receiptEnergy = new Receipt();
        Receipt receiptSewerage = new Receipt();
        Receipt receiptGas = new Receipt();

        assert contract != null;
        Optional<House> optionalHouse = houseRepository.findByContract(contract);
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found with the current contract number: " + contract, HttpStatus.NOT_FOUND);
        }
        receiptWater.setHouse(optionalHouse.get());
        receiptEnergy.setHouse(optionalHouse.get());
        receiptSewerage.setHouse(optionalHouse.get());
        receiptGas.setHouse(optionalHouse.get());

        assert receiptName != null;

        String waterName = receiptName + " " + "Agua";
        String energyName = receiptName + " " + "Energia";
        String sewerageName = receiptName + " " + "Alcantarillado";
        String gasName = receiptName + " " + "Gas";

        receiptWater.setPrice(priceWater);
        receiptEnergy.setPrice(priceEnergy);
        receiptSewerage.setPrice(priceSewerage);
        receiptGas.setPrice(priceGas);

        receiptWater.setAmount(amountWater);
        receiptEnergy.setAmount(amountEnergy);
        receiptSewerage.setAmount(amountSewerage);
        receiptGas.setAmount(amountGas);

        receiptWater.setHouseName(optionalHouse.get().getName());
        receiptEnergy.setHouseName(optionalHouse.get().getName());
        receiptSewerage.setHouseName(optionalHouse.get().getName());
        receiptGas.setHouseName(optionalHouse.get().getName());

        assert fechaFormateada != null;
        receiptWater.setDate(fechaFormateada);
        receiptEnergy.setDate(fechaFormateada);
        receiptSewerage.setDate(fechaFormateada);
        receiptGas.setDate(fechaFormateada);

        Optional<TypeService> optional = typeServiceRepository.findByTypeIgnoreCase("Water");
        if (optional.isEmpty()) {
            throw new AppException("Type Service not found", HttpStatus.NOT_FOUND);
        }
        receiptWater.setTypeService(optional.get());

        Optional<TypeService> optional1 = typeServiceRepository.findByTypeIgnoreCase("Energy");
        if (optional1.isEmpty()) {
            throw new AppException("Type Service not found", HttpStatus.NOT_FOUND);
        }
        receiptEnergy.setTypeService(optional1.get());

        Optional<TypeService> optional2 = typeServiceRepository.findByTypeIgnoreCase("Sewerage");
        if (optional2.isEmpty()) {
            throw new AppException("Type Service not found", HttpStatus.NOT_FOUND);
        }
        receiptSewerage.setTypeService(optional2.get());

        Optional<TypeService> optional3 = typeServiceRepository.findByTypeIgnoreCase("Gas");
        if (optional3.isEmpty()) {
            throw new AppException("Type Service not found", HttpStatus.NOT_FOUND);
        }
        receiptGas.setTypeService(optional3.get());

        Optional<Receipt> optionalReceipt = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), waterName, optional.get());
        if (optionalReceipt.isPresent()) {
            throw new AppException("Receipt already registered with the next name: " + waterName, HttpStatus.BAD_REQUEST);
        }

        Optional<Receipt> optionalReceipt1 = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), energyName, optional.get());
        if (optionalReceipt1.isPresent()) {
            throw new AppException("Receipt already registered with the next name: " + energyName, HttpStatus.BAD_REQUEST);
        }

        Optional<Receipt> optionalReceipt2 = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), sewerageName, optional.get());
        if (optionalReceipt2.isPresent()) {
            throw new AppException("Receipt already registered with the next name: " + sewerageName, HttpStatus.BAD_REQUEST);
        }

        Optional<Receipt> optionalReceipt3 = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), gasName, optional.get());
        if (optionalReceipt3.isPresent()) {
            throw new AppException("Receipt already registered with the next name: " + gasName, HttpStatus.BAD_REQUEST);
        }

        receiptWater.setReceiptName(waterName);
        receiptEnergy.setReceiptName(energyName);
        receiptSewerage.setReceiptName(sewerageName);
        receiptGas.setReceiptName(gasName);

        receiptRepository.save(receiptWater);
        receiptRepository.save(receiptEnergy);
        receiptRepository.save(receiptSewerage);
        receiptRepository.save(receiptGas);

        return "Recibos guardados satisfactoriamente";
    }

    @Override
    public Date formatDate(String date) {
        try {
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd-")
                    .appendText(ChronoField.MONTH_OF_YEAR,
                            new HashMap<>() {
                                {
                                    put(1L, "ene");
                                    put(2L, "feb");
                                    put(3L, "mar");
                                    put(4L, "abr");
                                    put(5L, "may");
                                    put(6L, "jun");
                                    put(7L, "jul");
                                    put(8L, "ago");
                                    put(9L, "sep");
                                    put(10L, "oct");
                                    put(11L, "nov");
                                    put(12L, "dic");
                                }
                            })
                    .appendPattern("-yyyy");

            DateTimeFormatter formatter = builder.toFormatter(Locale.forLanguageTag("es-ES"));

            LocalDate localDate = LocalDate.parse(date, formatter);

            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            throw new AppException("Error al Parsear la fecha " + e.getParsedString(), HttpStatus.BAD_REQUEST);
        }
    }

    private static double parseNumber(String value) {
        try {
            // Obtener el formato de número para el locale actual
            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

            // Parsear la cadena en un número
            Number number = numberFormat.parse(value);

            // Devolver el número como un double
            return number.doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            // Manejar la excepción de parseo, por ejemplo, lanzar una excepción personalizada o devolver un valor predeterminado
            return 0.0;
        }
    }

    @Override
    public String readPDF(MultipartFile archivoPdf) {
        try {
            PdfReader pdfReader = new PdfReader(archivoPdf.getInputStream());

            // Extraer el texto de la página 2
            String textoPagina = PdfTextExtractor.getTextFromPage(pdfReader, 2);

            // Cerrar el lector de PDF
            pdfReader.close();
            return extraerInformacionFactura(textoPagina);
        } catch (IOException e) {
            throw new AppException("Error al procesar el archivo PDF", HttpStatus.BAD_REQUEST);
        }
    }
}
