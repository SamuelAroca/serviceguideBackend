package proyecto.web.serviceguideBackend.receipt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

        if (!receiptDto.getReceiptName().isEmpty() && !receiptDto.getTypeService().name().isEmpty() && !receiptDto.getPrice().toString().isEmpty() && !receiptDto.getAmount().toString().isEmpty() && !receiptDto.getDate().toString().isEmpty()) {

            Optional<Receipt> receiptOptional = receiptRepository.findById(idReceipt);
            if (receiptOptional.isEmpty()) {
                throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
            }

            Receipt receipt = receiptOptional.get();

            Optional<User> optionalUser = userRepository.findUserByReceipt(idReceipt);
            if (optionalUser.isEmpty()) {
                throw new AppException("User not found", HttpStatus.NOT_FOUND);
            }

            Optional<House> optionalHouse = houseRepository.findByUserAndName(optionalUser.get(), receiptDto.getHouse().getName());
            if (optionalHouse.isEmpty()) {
                throw new AppException("House not found", HttpStatus.NOT_FOUND);
            }

            if (!receiptDto.getReceiptName().equals(receiptOptional.get().getReceiptName())) {
                Optional<Receipt> optionalReceipt = receiptRepository.findByHouseAndReceiptNameAndTypeService(optionalHouse.get(), receiptDto.getReceiptName(), receiptDto.getTypeService());
                if (optionalReceipt.isPresent()) {
                    throw new AppException("Receipt Name By Type Service Already registered", HttpStatus.BAD_REQUEST);
                }
                receipt.setReceiptName(receiptDto.getReceiptName());
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(receiptDto.getDate());
            int receiptMonth = calendar.get(Calendar.MONTH) + 1;
            int receiptYear = calendar.get(Calendar.YEAR);

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(receipt.getDate());
            int receiptMonthActual = calendar1.get(Calendar.MONTH) + 1;
            int receiptYearActual = calendar1.get(Calendar.YEAR);

            if (receiptMonth != receiptMonthActual || receiptYear != receiptYearActual) {
                List<Receipt> existingReceipts = receiptRepository.findByHouseAndTypeServiceAndMonthAndYear(optionalHouse.get(), receiptDto.getTypeService(), receiptMonth, receiptYear);
                if (!existingReceipts.isEmpty()) {
                    throw new AppException("Receipt already exists for the given month and year", HttpStatus.BAD_REQUEST);
                }
                receipt.setDate(receiptDto.getDate());
            }

            if (!receiptDto.getTypeService().equals(receiptOptional.get().getTypeService())) {
                switch (receiptDto.getTypeService().name()) {
                    case "WATER" -> receipt.setTypeService(TypeService.WATER);
                    case "ENERGY" -> receipt.setTypeService(TypeService.ENERGY);
                    case "SEWERAGE" -> receipt.setTypeService(TypeService.SEWERAGE);
                    case "GAS" -> receipt.setTypeService(TypeService.GAS);
                    default -> throw new AppException("Type Service not found", HttpStatus.NOT_FOUND);
                }
            }

            if (!receiptDto.getPrice().equals(receipt.getPrice())) {
                receipt.setPrice(receiptDto.getPrice());
            }

            if (!receiptDto.getAmount().equals(receipt.getAmount())) {
                receipt.setAmount(receiptDto.getAmount());
            }

            if (!receiptDto.getHouse().equals(receipt.getHouse())) {
                receipt.setHouse(optionalHouse.get());
                receipt.setHouseName(optionalHouse.get().getName());
            }
            receiptRepository.save(receipt);
            return new Message("Receipt Updated successfully", HttpStatus.OK);
        }
        throw new AppException("Check the inputs", HttpStatus.BAD_REQUEST);
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
    public Message extractReceiptInformation(String receiptText, Long idUser) {
        String patronWater = "Acueducto (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronSewerage = "Alcantarillado (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronEnergy = "Energía (\\d[\\d.,]*) kwh[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronGas = "Gas (\\d[\\d.,]*) m3[\\s\\t]*\\$[\\s\\t]*([\\d.,]+)";
        String patronDate = "(\\d{1,2}-[a-zA-Z]{3}-\\d{4})";
        String patronContractNumber = "Contrato (\\d+)";
        String patronReceiptName1 = "Factura [A-Za-z]+ de \\d{4}";
        String patronReceiptName2 = "facturación [a-zA-Z]+ de \\d{4}";


        // Crear los objetos de patrón
        Pattern patternWater = Pattern.compile(patronWater);
        Pattern patternSewerage = Pattern.compile(patronSewerage);
        Pattern patternEnergy = Pattern.compile(patronEnergy);
        Pattern patternGas = Pattern.compile(patronGas);
        Pattern patternDate = Pattern.compile(patronDate);
        Pattern patternContract = Pattern.compile(patronContractNumber);
        Pattern patternReceiptName1 = Pattern.compile(patronReceiptName1);
        Pattern patternReceiptName2 = Pattern.compile(patronReceiptName2);

        // Crear el objeto Matcher
        Matcher matcherWater = patternWater.matcher(receiptText);
        Matcher matcherEnergy = patternEnergy.matcher(receiptText);
        Matcher matcherSewerage = patternSewerage.matcher(receiptText);
        Matcher matcherGas = patternGas.matcher(receiptText);
        Matcher matcherDate = patternDate.matcher(receiptText);
        Matcher matcherContract = patternContract.matcher(receiptText);
        Matcher matcherReceiptName1 = patternReceiptName1.matcher(receiptText);
        Matcher matcherReceiptName2 = patternReceiptName2.matcher(receiptText);

        String contract = null;
        if (matcherContract.find()) {
            contract = matcherContract.group(1);
        }

        assert contract != null;
        Optional<House> optionalHouse = houseRepository.findByContractAndUser(contract, idUser);
        if (optionalHouse.isEmpty()) {
            //throw new AppException("House not found with the current contract number: " + contract, HttpStatus.NOT_FOUND);
            House houseSaved = houseService.extractReceiptInformation(receiptText, idUser);
            optionalHouse = Optional.of(houseSaved);
        }

        float amountWater = 0;
        double priceWater = 0;

        while (matcherWater.find()) {
            String quantity = matcherWater.group(1).replaceAll("[^\\d.]", "."); // Eliminar no dígitos ni puntos
            String price = matcherWater.group(2).replaceAll("[^\\d.,]", ""); // Eliminar no dígitos ni puntos ni comas

            quantity = quantity.replace(".", "").replace(",", ".");
            price = price.replace(".", "").replace(",", ".");

            amountWater = Float.parseFloat(quantity);
            priceWater = Double.parseDouble(price);
        }
        float amountEnergy = 0;
        double priceEnergy = 0;
        while (matcherEnergy.find()) {
            String quantity = matcherEnergy.group(1).replaceAll("[^\\d.]", "."); // Eliminar no dígitos ni puntos
            String price = matcherEnergy.group(2).replaceAll("[^\\d.,]", ""); // Eliminar no dígitos ni puntos ni comas

            quantity = quantity.replace(".", "");
            price = price.replace(".", "").replace(",", ".");

            amountEnergy = Float.parseFloat(quantity);
            priceEnergy = Double.parseDouble(price);
        }

        float amountSewerage = 0;
        double priceSewerage = 0;
        while (matcherSewerage.find()) {
            String quantity = matcherSewerage.group(1).replaceAll("[^\\d.]", "."); // Eliminar no dígitos ni puntos
            String price = matcherSewerage.group(2).replaceAll("[^\\d.,]", ""); // Eliminar no dígitos ni puntos ni comas

            quantity = quantity.replace(".", "");
            price = price.replace(".", "").replace(",", ".");

            amountSewerage = Float.parseFloat(quantity);
            priceSewerage = Double.parseDouble(price);
        }

        float amountGas = 0;
        double priceGas = 0;
        while (matcherGas.find()) {
            String quantity = matcherGas.group(1).replaceAll("[^\\d.]", "."); // Eliminar no dígitos ni puntos
            String price = matcherGas.group(2).replaceAll("[^\\d.,]", ""); // Eliminar no dígitos ni puntos ni comas

            price = price.replace(".", "").replace(",", ".");

            amountGas = Float.parseFloat(quantity);
            priceGas = Double.parseDouble(price);
        }

        String receiptName;
        if (matcherReceiptName1.find()) {
            receiptName = matcherReceiptName1.group(0);
        } else if (matcherReceiptName2.find()) {
            receiptName = matcherReceiptName2.group(0);
        } else {
            receiptName = "Factura Genérica";
        }

        Date formatedDate;
        if (matcherDate.find()) {
            String dateFound = matcherDate.group(1);
            assert receiptName != null;
            formatedDate = formatDate(dateFound, receiptName);
        } else {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy"); // Ajusta el formato si es necesario
            String currentDateStr = today.format(formatter);

            // Formatear la fecha actual usando tu función existente
            formatedDate = formatDate(currentDateStr, receiptName);
        }

        Receipt receiptWater = new Receipt();
        Receipt receiptEnergy = new Receipt();
        Receipt receiptSewerage = new Receipt();
        Receipt receiptGas = new Receipt();

        receiptWater.setHouse(optionalHouse.get());
        receiptEnergy.setHouse(optionalHouse.get());
        receiptSewerage.setHouse(optionalHouse.get());
        receiptGas.setHouse(optionalHouse.get());

        String NameCapitalize = receiptName.substring(0, 1).toUpperCase() + receiptName.substring(1);

        String waterName = NameCapitalize + " " + "Agua";
        String energyName = NameCapitalize + " " + "Energia";
        String sewerageName = NameCapitalize + " " + "Alcantarillado";
        String gasName = NameCapitalize + " " + "Gas";

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
    public Date formatDate(String date, String receiptName) {
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

            // Extraer el nombre del mes del recibo
            // Factura diciembre de 2023
            String receiptMonth = receiptName.substring(receiptName.indexOf(" ") + 1, receiptName.indexOf(" de"));
            receiptMonth = receiptMonth.toLowerCase();

            int indexOfDe = receiptName.lastIndexOf(" de");
            // Extraer el año desde el índice siguiente al espacio
            String receiptYearStr = receiptName.substring(indexOfDe + 4);
            int receiptYear = Integer.parseInt(receiptYearStr);

            // Comparar con el mes de la fecha obtenida
            Month month = localDate.getMonth();
            String monthName = month.getDisplayName(TextStyle.FULL, Locale.of("es", "ES")).toLowerCase();

            // Si los nombres de los meses no coinciden, ajustar la fecha al primer día del mes del recibo
            try {
                if (!receiptMonth.equals(monthName)) {
                    // Normalizar el nombre del mes a minúsculas
                    String normalizedMonth = receiptMonth.toLowerCase();

                    // Obtener el nombre del mes en inglés
                    DateFormatSymbols symbols = new DateFormatSymbols(Locale.of("es", "ES"));
                    String[] monthNames = symbols.getMonths();
                    int monthIndex = Arrays.asList(monthNames).indexOf(normalizedMonth);

                    // Verificar si se encontró el nombre del mes
                    if (monthIndex != -1) {
                        // Parsear el nombre del mes en inglés
                        Month adjustedMonth = Month.of(monthIndex + 1); // El índice comienza en 0, pero Month.of() espera un número de mes basado en 1
                        localDate = localDate.withMonth(adjustedMonth.getValue());
                        localDate = localDate.withDayOfMonth(5); // Ajustar al primer día del mes
                    if (receiptYear != localDate.getYear()) {
                        localDate = localDate.withYear(receiptYear);
                    }
                    } else {
                        // Manejar el caso en que no se encontró el nombre del mes
                        throw new DateTimeParseException("Nombre del mes no válido: " + receiptMonth, receiptMonth, 0);
                    }
                }
            } catch (DateTimeParseException e) {
                // Manejar la excepción DateTimeParseException aquí
                // Por ejemplo, lanzar una nueva excepción, imprimir un mensaje de error, etc.
                throw new AppException("Error al Parsear la fecha " + e.getParsedString(), HttpStatus.BAD_REQUEST);
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
        List<Receipt> receiptList = receiptRepository.findLastFourReceipt(idUser,pageable);
        if (receiptList.isEmpty()) {
            throw new AppException("No tienes recibos", HttpStatus.NOT_FOUND);
        }
        return receiptList;
    }
}
