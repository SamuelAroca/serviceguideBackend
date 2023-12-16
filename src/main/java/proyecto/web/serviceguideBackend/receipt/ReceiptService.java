package proyecto.web.serviceguideBackend.receipt;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.house.HouseService;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptInterface;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptMapper;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptRepository;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeService;
import proyecto.web.serviceguideBackend.statistic.StatisticService;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;

import java.io.IOException;
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
    private final HouseService houseService;
    private final StatisticService statisticService;

    @Override
    public ReceiptDto newReceipt(ReceiptDto receiptDto, Long idUser) {

        Receipt receipt = receiptMapper.serviceReceipt(receiptDto);

        Optional<House> optionalHouse = houseService.findByUserAndName(idUser, Objects.requireNonNull(receiptDto.getHouse()).getName());
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }

        switch (receiptDto.getTypeService().name()) {
            case "WATER" -> receipt.setTypeService(TypeService.WATER);
            case "ENERGY" -> receipt.setTypeService(TypeService.ENERGY);
            case "SEWERAGE" -> receipt.setTypeService(TypeService.SEWERAGE);
            case "GAS" -> receipt.setTypeService(TypeService.GAS);
            default -> throw new AppException("Type Service not found", HttpStatus.NOT_FOUND);
        }

        Calendar newReceiptCalendar = Calendar.getInstance();
        newReceiptCalendar.setTime(receiptDto.getDate());
        int receiptMonth = newReceiptCalendar.get(Calendar.MONTH) + 1;
        int receiptYear = newReceiptCalendar.get(Calendar.YEAR);

        List<Receipt> existingReceipts = receiptRepository.findByHouseAndTypeServiceAndMonthAndYear(optionalHouse.get(), receipt.getTypeService().name(), receiptMonth, receiptYear);
        if (!existingReceipts.isEmpty()) {
            throw new AppException("Receipt already exists for the given month and year", HttpStatus.BAD_REQUEST);
        }

        Optional<Receipt> optionalReceipt = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), receiptDto.getReceiptName(), receipt.getTypeService());
        if (optionalReceipt.isPresent()) {
            throw new AppException("Receipt name already registered", HttpStatus.BAD_REQUEST);
        }


        receipt.setHouse(optionalHouse.get());
        receipt.setHouseName(optionalHouse.get().getName());

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
        return null;//Falta reformatear
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
    public Message extractReceiptInformation(String receiptText) {
        String patronWater = "Acueducto (\\d[\\d.,]*) m3\\n[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronSewerage = "Alcantarillado (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronEnergy = "Energía (\\d[\\d.,]*) kwh[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronGas = "Gas (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronDate = "(\\d{1,2}-[a-zA-Z]{3}-\\d{4})";
        String patronContractNumber = "Contrato (\\d+)";
        String patronReceiptName = "Factura [A-Za-z]+ de \\d{4}";

        // Crear los objetos de patrón
        Pattern patternWater = Pattern.compile(patronWater);
        Pattern patternSewerage = Pattern.compile(patronSewerage);
        Pattern patternEnergy = Pattern.compile(patronEnergy);
        Pattern patternGas = Pattern.compile(patronGas);
        Pattern patternDate = Pattern.compile(patronDate);
        Pattern patternContract = Pattern.compile(patronContractNumber);
        Pattern patternReceiptName = Pattern.compile(patronReceiptName);

        // Crear el objeto Matcher
        Matcher matcherWater = patternWater.matcher(receiptText);
        Matcher matcherEnergy = patternEnergy.matcher(receiptText);
        Matcher matcherSewerage = patternSewerage.matcher(receiptText);
        Matcher matcherGas = patternGas.matcher(receiptText);
        Matcher matcherDate = patternDate.matcher(receiptText);
        Matcher matcherContract = patternContract.matcher(receiptText);
        Matcher matcherReceiptName = patternReceiptName.matcher(receiptText);

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

        Date formatedDate;
        if (matcherDate.find()) {
            String dateFound = matcherDate.group(1);
            formatedDate = formatDate(dateFound);
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

        assert formatedDate != null;
        receiptWater.setDate(formatedDate);
        receiptEnergy.setDate(formatedDate);
        receiptSewerage.setDate(formatedDate);
        receiptGas.setDate(formatedDate);

        receiptWater.setTypeService(TypeService.WATER);

        receiptEnergy.setTypeService(TypeService.ENERGY);

        receiptSewerage.setTypeService(TypeService.SEWERAGE);

        receiptGas.setTypeService(TypeService.GAS);

        Optional<Receipt> optionalReceipt = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), waterName, TypeService.WATER);
        if (optionalReceipt.isPresent()) {
            throw new AppException("Receipt already registered with the next name: " + waterName, HttpStatus.BAD_REQUEST);
        }

        Optional<Receipt> optionalReceipt1 = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), energyName, TypeService.ENERGY);
        if (optionalReceipt1.isPresent()) {
            throw new AppException("Receipt already registered with the next name: " + energyName, HttpStatus.BAD_REQUEST);
        }

        Optional<Receipt> optionalReceipt2 = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), sewerageName, TypeService.SEWERAGE);
        if (optionalReceipt2.isPresent()) {
            throw new AppException("Receipt already registered with the next name: " + sewerageName, HttpStatus.BAD_REQUEST);
        }

        Optional<Receipt> optionalReceipt3 = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), gasName, TypeService.GAS);
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

        Long idUser = optionalHouse.get().getUser().getId();
        Pageable pageable = PageRequest.of(0,4);

        List<Receipt> receiptList = receiptCollection(idUser, pageable);

        Collections.reverse(receiptList);

        try {
            Receipt firstReceipt = receiptList.remove(0);
            Long idFirstReceipt = firstReceipt.getId();
            String typeFirstReceipt = firstReceipt.getTypeService().name();
            statisticService.individualReceipt(typeFirstReceipt, idFirstReceipt, "BAR");
        } catch (Exception e) {
            // Manejar la excepción (puedes personalizar esto según tus necesidades)
            System.err.println("Error al procesar el primer Receipt: " + e.getMessage());
        }

// Continuar con el procesamiento de los demás Receipts
        for (Receipt receipt : receiptList) {
            Long idReceipt = receipt.getId();
            String typeReceipt = String.valueOf(receipt.getTypeService());
            statisticService.individualReceipt(typeReceipt, idReceipt, "BAR");
        }

        return new Message("Recibos guardados satisfactoriamente", HttpStatus.OK);
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

    @Override
    public Message readPDF(MultipartFile archivoPdf) {
        try {
            PdfReader pdfReader = new PdfReader(archivoPdf.getInputStream());

            // Extraer el texto de la página 2
            String textoPagina = PdfTextExtractor.getTextFromPage(pdfReader, 2);

            // Cerrar el lector de PDF
            pdfReader.close();
            return extractReceiptInformation(textoPagina);
        } catch (IOException e) {
            throw new AppException("Error al procesar el archivo PDF", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<Receipt> receiptCollection(Long idUser, Pageable pageable) {
        List<Receipt> receiptList = receiptRepository.findLastFourReceipt(idUser,pageable);
        if (receiptList.isEmpty()) {
            throw new AppException("No tienes recibos", HttpStatus.NOT_FOUND);
        }
        return receiptList;
    }
}
