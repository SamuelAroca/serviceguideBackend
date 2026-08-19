package proyecto.web.serviceguideBackend.receipt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.web.serviceguideBackend.dto.Message;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.HouseService;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;
import proyecto.web.serviceguideBackend.receipt.dto.ReceiptDto;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptInterface;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptMapper;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptRepository;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeService;
import proyecto.web.serviceguideBackend.statistic.StatisticService;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;
import proyecto.web.serviceguideBackend.utils.Utils;

import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptService implements ReceiptInterface {

    private final ReceiptRepository receiptRepository;
    private final ReceiptMapper receiptMapper;
    private final HouseRepository houseRepository;
    private final HouseService houseService;
    private final StatisticService statisticService;
    private final UserRepository userRepository;
    private final Utils utils;

    // ---------------------------------------------------------------------
    // Patrones de extracción, precompilados una sola vez (no en cada request).
    // (?i) = insensible a mayúsculas/minúsculas: EPM no es consistente entre
    // facturas (ej. "kwh" en mayo, "Kwh" en agosto), y ese fue precisamente
    // el bug que dejaba la energía guardada en 0.
    // ---------------------------------------------------------------------
    private static final Pattern PATTERN_WATER = Pattern.compile("(?i)Acueducto (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)");
    private static final Pattern PATTERN_SEWERAGE = Pattern.compile("(?i)Alcantarillado (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)");
    private static final Pattern PATTERN_ENERGY = Pattern.compile("(?i)Energía (\\d[\\d.,]*) kwh[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)");
    private static final Pattern PATTERN_GAS = Pattern.compile("(?i)Gas (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)");
    private static final Pattern PATTERN_DATE = Pattern.compile("(\\d{1,2}-[a-zA-Z]{3}-\\d{4})");
    private static final Pattern PATTERN_CONTRACT = Pattern.compile("Contrato (\\d+)");
    private static final Pattern PATTERN_RECEIPT_NAME_1 = Pattern.compile("Factura [A-Za-z]+ de \\d{4}");
    private static final Pattern PATTERN_RECEIPT_NAME_2 = Pattern.compile("facturación [a-zA-Z]+ de \\d{4}");

    private static final Map<Long, String> SPANISH_MONTHS = Map.ofEntries(
            Map.entry(1L, "ene"), Map.entry(2L, "feb"), Map.entry(3L, "mar"), Map.entry(4L, "abr"),
            Map.entry(5L, "may"), Map.entry(6L, "jun"), Map.entry(7L, "jul"), Map.entry(8L, "ago"),
            Map.entry(9L, "sep"), Map.entry(10L, "oct"), Map.entry(11L, "nov"), Map.entry(12L, "dic")
    );

    /**
     * Lectura numérica (cantidad + precio) extraída para un servicio.
     */
    private record ServiceReading(float amount, double price, boolean found) {
        static ServiceReading empty() {
            return new ServiceReading(0f, 0d, false);
        }
    }

    @Override
    public ReceiptDto newReceipt(ReceiptDto receiptDto, Long idUser) {

        Receipt receipt = receiptMapper.serviceReceipt(receiptDto);

        Optional<House> optionalHouse = houseService.findByUserAndName(idUser, Objects.requireNonNull(receiptDto.getHouse()).getName());
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }

        receipt.setTypeService(resolveTypeService(receiptDto.getTypeService().name()));

        Calendar newReceiptCalendar = Calendar.getInstance();
        newReceiptCalendar.setTime(receiptDto.getDate());
        int receiptMonth = newReceiptCalendar.get(Calendar.MONTH) + 1;
        int receiptYear = newReceiptCalendar.get(Calendar.YEAR);

        List<Receipt> existingReceipts = receiptRepository.findByHouseAndTypeServiceAndMonthAndYear(optionalHouse.get(), receipt.getTypeService(), receiptMonth, receiptYear);
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
    public Message updateReceipt(ReceiptDto receiptDto, Long idReceipt) {

        if (receiptDto.getReceiptName().isEmpty() || receiptDto.getTypeService().name().isEmpty()
                || receiptDto.getPrice().toString().isEmpty() || receiptDto.getAmount().toString().isEmpty()
                || receiptDto.getDate().toString().isEmpty()) {
            throw new AppException("Check the inputs", HttpStatus.BAD_REQUEST);
        }

        Receipt receipt = receiptRepository.findById(idReceipt)
                .orElseThrow(() -> new AppException("Receipt not found", HttpStatus.NOT_FOUND));

        User user = userRepository.findUserByReceipt(idReceipt)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        House house = houseRepository.findByUserAndName(user, receiptDto.getHouse().getName())
                .orElseThrow(() -> new AppException("House not found", HttpStatus.NOT_FOUND));

        if (!receiptDto.getReceiptName().equals(receipt.getReceiptName())) {
            boolean nameTaken = receiptRepository
                    .findByHouseAndReceiptNameAndTypeService(house, receiptDto.getReceiptName(), receiptDto.getTypeService())
                    .isPresent();
            if (nameTaken) {
                throw new AppException("Receipt Name By Type Service Already registered", HttpStatus.BAD_REQUEST);
            }
            receipt.setReceiptName(receiptDto.getReceiptName());
        }

        Calendar newDate = Calendar.getInstance();
        newDate.setTime(receiptDto.getDate());
        int newMonth = newDate.get(Calendar.MONTH) + 1;
        int newYear = newDate.get(Calendar.YEAR);

        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(receipt.getDate());
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);

        if (newMonth != currentMonth || newYear != currentYear) {
            List<Receipt> existingReceipts = receiptRepository
                    .findByHouseAndTypeServiceAndMonthAndYear(house, receiptDto.getTypeService(), newMonth, newYear);
            if (!existingReceipts.isEmpty()) {
                throw new AppException("Receipt already exists for the given month and year", HttpStatus.BAD_REQUEST);
            }
            receipt.setDate(receiptDto.getDate());
        }

        if (!receiptDto.getTypeService().equals(receipt.getTypeService())) {
            receipt.setTypeService(resolveTypeService(receiptDto.getTypeService().name()));
        }

        if (!receiptDto.getPrice().equals(receipt.getPrice())) {
            receipt.setPrice(receiptDto.getPrice());
        }

        if (!receiptDto.getAmount().equals(receipt.getAmount())) {
            receipt.setAmount(receiptDto.getAmount());
        }

        if (!receiptDto.getHouse().equals(receipt.getHouse())) {
            receipt.setHouse(house);
            receipt.setHouseName(house.getName());
        }

        receiptRepository.save(receipt);
        return new Message("Receipt Updated successfully", HttpStatus.OK);
    }

    @Override
    public Message deleteReceipt(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new AppException("Receipt not found", HttpStatus.NOT_FOUND));
        receiptRepository.delete(receipt);
        return new Message("Received deleted successfully", HttpStatus.OK);
    }

    @Override
    public Long getTwoReceiptById(Long idReceipt) {
        return receiptRepository.findUserByReceiptId(idReceipt);
    }

    @Override
    public Message extractReceiptInformation(String receiptText, Long idUser) {

        String contract = findFirst(receiptText)
                .orElseThrow(() -> new AppException("Contract number not found in receipt", HttpStatus.BAD_REQUEST));

        Optional<House> optionalHouse = houseRepository.findByContractAndUser(contract, idUser);
        House house = optionalHouse.orElseGet(() -> houseService.extractReceiptInformation(receiptText, idUser));

        ServiceReading waterReading = extractServiceReading(receiptText, PATTERN_WATER, "Acueducto");
        ServiceReading energyReading = extractServiceReading(receiptText, PATTERN_ENERGY, "Energía");
        ServiceReading sewerageReading = extractServiceReading(receiptText, PATTERN_SEWERAGE, "Alcantarillado");
        ServiceReading gasReading = extractServiceReading(receiptText, PATTERN_GAS, "Gas");

        String receiptName = resolveReceiptName(receiptText);
        Date formattedDate = resolveDate(receiptText, receiptName);
        String nameCapitalized = receiptName.substring(0, 1).toUpperCase() + receiptName.substring(1);

        // type, sufijo del nombre, lectura extraída
        record ServiceDefinition(TypeService type, String suffix, ServiceReading reading) {
        }

        List<ServiceDefinition> services = List.of(
                new ServiceDefinition(TypeService.WATER, "Agua", waterReading),
                new ServiceDefinition(TypeService.ENERGY, "Energia", energyReading),
                new ServiceDefinition(TypeService.SEWERAGE, "Alcantarillado", sewerageReading),
                new ServiceDefinition(TypeService.GAS, "Gas", gasReading)
        );

        List<Receipt> receiptsToSave = new ArrayList<>();
        for (ServiceDefinition service : services) {
            String receiptFullName = nameCapitalized + " " + service.suffix();

            boolean alreadyExists = receiptRepository
                    .findByHouseAndReceiptNameAndTypeService(house, receiptFullName, service.type())
                    .isPresent();
            if (alreadyExists) {
                throw new AppException("Receipt already registered with the next name: " + receiptFullName, HttpStatus.BAD_REQUEST);
            }

            Receipt receipt = new Receipt();
            receipt.setHouse(house);
            receipt.setHouseName(house.getName());
            receipt.setDate(formattedDate);
            receipt.setTypeService(service.type());
            receipt.setReceiptName(receiptFullName);
            receipt.setAmount(service.reading().amount());
            receipt.setPrice(service.reading().price());
            receiptsToSave.add(receipt);
        }

        receiptRepository.saveAll(receiptsToSave);

        Pageable pageable = PageRequest.of(0, 4);
        List<Receipt> receiptList = receiptCollection(idUser, pageable);
        Collections.reverse(receiptList);

        updateStatistics(receiptList);

        return new Message("Recibos guardados satisfactoriamente", HttpStatus.OK);
    }

    /**
     * Extrae cantidad y precio de un servicio a partir de su patrón.
     * Si el patrón aparece varias veces en el texto (ej. dos tramos de tarifa
     * de acueducto), se queda con la última coincidencia, igual que el
     * comportamiento original.
     * Si no encuentra ninguna coincidencia, deja constancia en el log en vez
     * de guardar silenciosamente un 0 sin que nadie se entere.
     */
    private ServiceReading extractServiceReading(String receiptText, Pattern pattern, String serviceLabel) {
        Matcher matcher = pattern.matcher(receiptText);

        float amount = 0f;
        double price = 0d;
        boolean found = false;

        while (matcher.find()) {
            amount = Float.parseFloat(toPlainNumber(matcher.group(1)));
            price = Double.parseDouble(toPlainNumber(matcher.group(2)));
            found = true;
        }

        if (!found) {
            log.warn("No se pudo extraer la lectura de '{}' del recibo; se guardará como 0. " +
                    "Es posible que EPM haya cambiado el formato del texto.", serviceLabel);
        }

        return new ServiceReading(amount, price, found);
    }

    /**
     * Normaliza números en formato colombiano (punto = separador de miles,
     * coma = separador decimal) a formato plano parseable ("1.234,56" -> "1234.56").
     */
    private static String toPlainNumber(String raw) {
        return raw.replace(".", "").replace(",", ".");
    }

    private static Optional<String> findFirst(String text) {
        Matcher matcher = ReceiptService.PATTERN_CONTRACT.matcher(text);
        return matcher.find() ? Optional.of(matcher.group(1)) : Optional.empty();
    }

    private String resolveReceiptName(String receiptText) {
        Matcher matcherReceiptName1 = PATTERN_RECEIPT_NAME_1.matcher(receiptText);
        if (matcherReceiptName1.find()) {
            return matcherReceiptName1.group(0);
        }
        Matcher matcherReceiptName2 = PATTERN_RECEIPT_NAME_2.matcher(receiptText);
        if (matcherReceiptName2.find()) {
            return matcherReceiptName2.group(0);
        }
        return "Factura Genérica";
    }

    private Date resolveDate(String receiptText, String receiptName) {
        Matcher matcherDate = PATTERN_DATE.matcher(receiptText);
        if (matcherDate.find()) {
            return formatDate(matcherDate.group(1), receiptName);
        }
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        return formatDate(today.format(formatter), receiptName);
    }

    private void updateStatistics(List<Receipt> receiptList) {
        if (receiptList.isEmpty()) {
            return;
        }
        try {
            Receipt firstReceipt = receiptList.remove(0);
            statisticService.individualReceipt(firstReceipt.getTypeService().name(), firstReceipt.getId(), "BAR");
        } catch (Exception e) {
            log.error("Error al procesar el primer Receipt: {}", e.getMessage());
        }

        for (Receipt receipt : receiptList) {
            statisticService.individualReceipt(receipt.getTypeService().name(), receipt.getId(), "BAR");
        }
    }

    private TypeService resolveTypeService(String name) {
        return switch (name) {
            case "WATER" -> TypeService.WATER;
            case "ENERGY" -> TypeService.ENERGY;
            case "SEWERAGE" -> TypeService.SEWERAGE;
            case "GAS" -> TypeService.GAS;
            default -> throw new AppException("Type Service not found", HttpStatus.NOT_FOUND);
        };
    }

    @Override
    public Date formatDate(String date, String receiptName) {
        try {
            DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd-")
                    .appendText(ChronoField.MONTH_OF_YEAR, SPANISH_MONTHS)
                    .appendPattern("-yyyy");

            DateTimeFormatter formatter = builder.toFormatter(Locale.forLanguageTag("es-ES"));
            LocalDate localDate = LocalDate.parse(date, formatter);

            // Factura diciembre de 2023
            String receiptMonth = receiptName.substring(receiptName.indexOf(" ") + 1, receiptName.indexOf(" de")).toLowerCase();
            int indexOfDe = receiptName.lastIndexOf(" de");
            int receiptYear = Integer.parseInt(receiptName.substring(indexOfDe + 4));

            String monthName = localDate.getMonth().getDisplayName(TextStyle.FULL, Locale.of("es", "ES")).toLowerCase();

            if (!receiptMonth.equals(monthName)) {
                DateFormatSymbols symbols = new DateFormatSymbols(Locale.of("es", "ES"));
                String[] monthNames = symbols.getMonths();
                int monthIndex = Arrays.asList(monthNames).indexOf(receiptMonth);

                if (monthIndex == -1) {
                    throw new AppException("Error al Parsear la fecha " + receiptMonth, HttpStatus.BAD_REQUEST);
                }

                Month adjustedMonth = Month.of(monthIndex + 1);
                localDate = localDate.withMonth(adjustedMonth.getValue()).withDayOfMonth(5);
                if (receiptYear != localDate.getYear()) {
                    localDate = localDate.withYear(receiptYear);
                }
            }

            return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException e) {
            throw new AppException("Error al Parsear la fecha " + e.getParsedString(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Message readPDF(MultipartFile file, HttpServletRequest request) {
        Long idUser = utils.getTokenFromRequest(request);
        return extractReceiptInformation(utils.readPdf(file), idUser);
    }

    @Override
    public List<Receipt> receiptCollection(Long idUser, Pageable pageable) {
        List<Receipt> receiptList = receiptRepository.findLastFourReceipt(idUser, pageable);
        if (receiptList.isEmpty()) {
            throw new AppException("No tienes recibos", HttpStatus.NOT_FOUND);
        }
        return receiptList;
    }
}