package proyecto.web.serviceguideBackend.statistic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.averageStatistic.AverageStatistic;
import proyecto.web.serviceguideBackend.averageStatistic.AverageStatisticRepository;
import proyecto.web.serviceguideBackend.averageStatistic.dto.PercentageStatisticDto;
import proyecto.web.serviceguideBackend.averageStatistic.dto.SumStatisticDto;
import proyecto.web.serviceguideBackend.config.UserAuthenticationProvider;
import proyecto.web.serviceguideBackend.averageStatistic.dto.StatisticAverageDto;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptRepository;
import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;
import proyecto.web.serviceguideBackend.statistic.interfaces.StatisticInterface;
import proyecto.web.serviceguideBackend.statistic.interfaces.StatisticMapper;
import proyecto.web.serviceguideBackend.statistic.interfaces.StatisticRepository;
import proyecto.web.serviceguideBackend.statistic.statisticType.StatisticType;
import proyecto.web.serviceguideBackend.statistic.statisticType.StatisticTypeRepository;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@RequiredArgsConstructor
@Service
public class StatisticService implements StatisticInterface {

    private final ReceiptRepository receiptRepository;
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;
    private final StatisticTypeRepository statisticTypeRepository;
    private final UserRepository userRepository;
    private final UserAuthenticationProvider authenticationProvider;
    private final HouseRepository houseRepository;
    private final AverageStatisticRepository averageStatisticRepository;

    @Override
    public StatisticDto individualReceipt(String typeReceipt, Long idReceipt, String typeGraphic) {
        List<Statistic> statistics = statisticRepository.getStatisticByReceipt(idReceipt);
        if (!statistics.isEmpty()) {
            StatisticDto statisticDto = new StatisticDto();
            List<Double[]> prices = new ArrayList<>();
            List<Double[]> amount = new ArrayList<>();
            List<String[]> label = new ArrayList<>();
            for (int i = 0; i < statistics.size(); i++) {
                prices.add(statistics.get(i).getPrice());
                amount.add(statistics.get(i).getAmount());
                label.add(statistics.get(i).getLabel());

                statisticDto.setPrice(prices.get(i));
                statisticDto.setAmount(amount.get(i));
                statisticDto.setLabel(label.get(i));
                statisticDto.setId(statistics.get(i).getId());
                statisticDto.setStatisticsType(statistics.get(i).getStatisticsType());
            }
            return statisticDto;
        }
        Optional<StatisticType> optionalStatisticType = statisticTypeRepository.findByTypeIgnoreCase(typeGraphic);
        if (optionalStatisticType.isEmpty()) {
            throw new AppException("Type not found", HttpStatus.NOT_FOUND);
        }
        Optional<Receipt> optionalReceipt = receiptRepository.findById(idReceipt);
        if (optionalReceipt.isEmpty()) {
            throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
        }
        Long idUser = receiptRepository.findUserByReceiptId(idReceipt);
        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        List<Long> collectionId = receiptRepository.getIdByUser(optionalUser.get().getId(), typeReceipt);
        /*Se declara el array y se extraen 2 id*/
        List<Double> idList = new ArrayList<>();
        for (int i = 0; i < 2 && i < collectionId.size(); i++) {
            idList.add(Double.valueOf(collectionId.get(i)));
        }
        /*Se verifica si la lista tiene solo un dato y si es verdadero lanza una excepcion*/
        if (idList.size() == 1) {
            throw new AppException("Recibo creado pero no se puede generar la estadistica", HttpStatus.OK);
        }
        List<Receipt> receiptList = receiptRepository.getTwoReceiptsById(idList.get(0).longValue(), idList.get(1).longValue());

        Double[] price = {receiptList.get(0).getPrice(), receiptList.get(1).getPrice()};
        Double[] amount = {receiptList.get(0).getAmount(), receiptList.get(1).getAmount()};

        /*Se extrae el mes y se pone la primera letra en Mayuscula*/
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        String dateStr1 = String.valueOf(receiptList.get(0).getDate());
        LocalDate date1 = LocalDate.parse(dateStr1, formatter);
        String monthName1 = date1.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES"));
        String monthCapitalize1 = monthName1.substring(0, 1).toUpperCase() + monthName1.substring(1);

        String dateStr2 = String.valueOf(receiptList.get(1).getDate());
        LocalDate date2 = LocalDate.parse(dateStr2);
        String monthName2 = date2.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES"));
        String monthCapitalize2 = monthName2.substring(0, 1).toUpperCase() + monthName2.substring(1);

        String[] label = {monthCapitalize1, monthCapitalize2};

        /*Se crea la nueva estadistica y se le pasan los datos generados*/
        Statistic statistic = new Statistic();
        statistic.setLabel(label);
        statistic.setPrice(price);
        statistic.setAmount(amount);
        statistic.setStatisticsType(optionalStatisticType.get());
        statisticRepository.save(statistic);

        List<Statistic> statisticList = new ArrayList<>();
        statisticList.add(statistic);
        optionalReceipt.get().setStatistics(statisticList);
        Receipt receipt = optionalReceipt.get();
        receipt.setStatistics(statisticList);
        receiptRepository.save(receipt);
        return statisticMapper.statisticDto(statistic);
    }

    @Override
    public List<Statistic> getStatisticByReceipt(Long idReceipt) {
        return statisticRepository.getStatisticByReceipt(idReceipt);
    }

    @Override
    public StatisticAverageDto getStatisticByTypeAndHouse(String typeReceipt, String token, String house) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        Collection<Receipt> receipts = receiptRepository.getAllReceiptsByTypeAndHouse(idUser, typeReceipt, house);

        if (receipts.isEmpty()) {
            throw new AppException("No se encontraron recibos", HttpStatus.NOT_FOUND);
        }

        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }

        Optional<House> optionalHouse = houseRepository.findByUserAndName(optionalUser.get(), house);
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }

        String houseName =optionalHouse.get().getName();

        double totalPrices = 0;
        double totalAmounts = 0;

        for (Receipt receipt : receipts) {
            totalPrices += receipt.getPrice();
            totalAmounts += receipt.getAmount();
        }

        double averagePrice = totalPrices / receipts.size();
        double averageAmount = totalAmounts / receipts.size();

        StatisticAverageDto statisticAverageDto = new StatisticAverageDto();
        statisticAverageDto.setHouseName(houseName);
        statisticAverageDto.setAmount(totalAmounts);
        statisticAverageDto.setPrice(totalPrices);
        statisticAverageDto.setAveragePrice(averagePrice);
        statisticAverageDto.setAverageAmount(averageAmount);

        AverageStatistic averageStatistic = new AverageStatistic();
        averageStatistic.setHouseName(statisticAverageDto.getHouseName());
        averageStatistic.setAmount(statisticAverageDto.getAmount());
        averageStatistic.setPrice(statisticAverageDto.getPrice());
        averageStatistic.setAveragePrice(statisticAverageDto.getAveragePrice());
        averageStatistic.setAverageAmount(statisticAverageDto.getAverageAmount());
        averageStatistic.setTimestamp(Timestamp.from(Instant.now())); // Asigna la marca de tiempo actual

        averageStatisticRepository.save(averageStatistic);

        AverageStatistic lastEntry = averageStatisticRepository.findTopByHouseNameOrderByTimestampDesc(houseName);
        if (lastEntry != null) {
            double lastAveragePrice = lastEntry.getAveragePrice();
            double lastAverageAmount = lastEntry.getAverageAmount();

            if (averagePrice > lastAveragePrice) {
                System.out.println("El promedio del precio ha subido");
            } else if (averagePrice < lastAveragePrice) {
                System.out.println("El promedio del precio ha disminuido");
            }

            if (averageAmount > lastAverageAmount) {
                System.out.println("El promedio de la cantidad ha subido");
            } else if (averageAmount < lastAverageAmount) {
                System.out.println("El promedio de la cantidad ha disminuido");
            }
        }

        return statisticAverageDto;

    }

    @Override
    public StatisticAverageDto getStatisticByTypeAndYear(String typeReceipt, String token, int year) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        Collection<Receipt> receipts = receiptRepository.getAllReceiptByTypeAndYear(idUser, typeReceipt, year);

        if (receipts.isEmpty()) {
            throw new AppException("No se encontraron recibos", HttpStatus.NOT_FOUND);
        }

        double totalPrices = 0;
        double totalAmounts = 0;

        String yearString =String.valueOf(year);

        for (Receipt receipt : receipts) {
            totalPrices += receipt.getPrice();
            totalAmounts += receipt.getAmount();
        }

        double averagePrice = totalPrices / receipts.size();
        double averageAmount = totalAmounts / receipts.size();

        StatisticAverageDto statisticAverageDto = new StatisticAverageDto();
        statisticAverageDto.setAmount(totalAmounts);
        statisticAverageDto.setPrice(totalPrices);
        statisticAverageDto.setAveragePrice(averagePrice);
        statisticAverageDto.setAverageAmount(averageAmount);
        statisticAverageDto.setYear(yearString);

        return statisticAverageDto;
    }

    @Override
    public StatisticAverageDto getStatisticByQuarter(String token, String typeReceipt, int quarter, int year) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        Collection<Receipt> receipts = receiptRepository.getReceiptsByQuarter(idUser, typeReceipt, quarter, year);

        if (receipts.isEmpty()) {
            throw new AppException("No se encontraron recibos", HttpStatus.NOT_FOUND);
        }

        double totalPrices = 0;
        double totalAmounts = 0;

        String yearString =String.valueOf(year);

        for (Receipt receipt : receipts) {
            totalPrices += receipt.getPrice();
            totalAmounts += receipt.getAmount();
        }

        double averagePrice = totalPrices / receipts.size();
        double averageAmount = totalAmounts / receipts.size();

        StatisticAverageDto statisticAverageDto = new StatisticAverageDto();
        statisticAverageDto.setAmount(totalAmounts);
        statisticAverageDto.setPrice(totalPrices);
        statisticAverageDto.setAveragePrice(averagePrice);
        statisticAverageDto.setAverageAmount(averageAmount);
        statisticAverageDto.setYear(yearString);

        return statisticAverageDto;
    }

    @Override
    public StatisticAverageDto getStatisticBySemester(String token, String typeReceipt, int semester, int receiptYear) {
        Long idUser = authenticationProvider.whoIsMyId(token);

        List<Integer> months;
        if (semester == 1) {
            months = Arrays.asList(1, 2, 3, 4, 5, 6);
        } else if (semester == 2) {
            months = Arrays.asList(7, 8, 9, 10, 11, 12);
        } else {
            throw new IllegalArgumentException("El número de semestre debe ser 1 o 2");
        }
        Collection<Receipt> receipts = receiptRepository.getReceiptsBySemester(idUser, typeReceipt, months, receiptYear);

        if (receipts.isEmpty()) {
            throw new AppException("No se encontraron recibos", HttpStatus.NOT_FOUND);
        }

        double totalPrices = 0;
        double totalAmounts = 0;

        String yearString =String.valueOf(receiptYear);

        for (Receipt receipt : receipts) {
            totalPrices += receipt.getPrice();
            totalAmounts += receipt.getAmount();
        }

        double averagePrice = totalPrices / receipts.size();
        double averageAmount = totalAmounts / receipts.size();

        StatisticAverageDto statisticAverageDto = new StatisticAverageDto();
        statisticAverageDto.setAmount(totalAmounts);
        statisticAverageDto.setPrice(totalPrices);
        statisticAverageDto.setAveragePrice(averagePrice);
        statisticAverageDto.setAverageAmount(averageAmount);
        statisticAverageDto.setYear(yearString);

        return statisticAverageDto;
    }

    @Override
    public StatisticAverageDto getStatisticByMonth(String token, String typeReceipt, int startMonth, int endMonth, int receiptYear) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        Collection<Receipt> receipts = receiptRepository.getReceiptsByMonth(idUser, typeReceipt, startMonth, endMonth, receiptYear);

        if (receipts.isEmpty()) {
            throw new AppException("No se encontraron recibos", HttpStatus.NOT_FOUND);
        }

        double totalPrices = 0;
        double totalAmounts = 0;

        String yearString =String.valueOf(receiptYear);

        for (Receipt receipt : receipts) {
            totalPrices += receipt.getPrice();
            totalAmounts += receipt.getAmount();
        }

        double averagePrice = totalPrices / receipts.size();
        double averageAmount = totalAmounts / receipts.size();

        StatisticAverageDto statisticAverageDto = new StatisticAverageDto();
        statisticAverageDto.setAmount(totalAmounts);
        statisticAverageDto.setPrice(totalPrices);
        statisticAverageDto.setAveragePrice(averagePrice);
        statisticAverageDto.setAverageAmount(averageAmount);
        statisticAverageDto.setYear(yearString);

        return statisticAverageDto;
    }

    @Override
    public double[] sumStatisticByType(String token, String house) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        Collection<Receipt> receipts = receiptRepository.getAllReceiptsByHouse(idUser, house);

        if (receipts.isEmpty()) {
            throw new AppException("No se encontraron recibos", HttpStatus.NOT_FOUND);
        }

        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }

        Optional<House> optionalHouse = houseRepository.findByUserAndName(optionalUser.get(), house);
        if (optionalHouse.isEmpty()) {
            throw new AppException("House not found", HttpStatus.NOT_FOUND);
        }

        double waterSum = 0;
        double energySum = 0;
        double gasSum = 0;
        double sewerageSum = 0;

        for (Receipt receipt : receipts) {
            double receiptAmount = receipt.getAmount().doubleValue();
            String typeServiceName = receipt.getTypeService().getType();

            if (typeServiceName.equals("WATER")) {
                waterSum += receiptAmount;
            } else if (typeServiceName.equals("ENERGY")) {
                energySum += receiptAmount;
            } else if (typeServiceName.equals("GAS")) {
                gasSum += receiptAmount;
            } else if (typeServiceName.equals("SEWERAGE")) {
                sewerageSum += receiptAmount;
            }
        }

        // Crear un array de double y almacenar las sumas por tipo
        double[] sumStatistics = { waterSum, energySum, gasSum, sewerageSum };

        return sumStatistics;
    }


    @Override
    public PercentageStatisticDto getPercentage(String token, String houseName) {
        Long idUser = authenticationProvider.whoIsMyId(token);
        Collection<Receipt> receipts = receiptRepository.getAllReceiptsByHouse(idUser, houseName);

        // Obtener la fecha actual
        Calendar currentMonthCalendar = Calendar.getInstance();
        currentMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        currentMonthCalendar.set(Calendar.HOUR_OF_DAY, 0);
        currentMonthCalendar.set(Calendar.MINUTE, 0);
        currentMonthCalendar.set(Calendar.SECOND, 0);
        currentMonthCalendar.set(Calendar.MILLISECOND, 0);

        // Obtener la fecha del último mes ingresado en la base de datos
        Calendar lastMonthCalendar = (Calendar) currentMonthCalendar.clone();
        lastMonthCalendar.add(Calendar.MONTH, -1);

        // Crear listas para almacenar los recibos del último mes y el mes anterior
        List<Receipt> lastMonthReceipts = new ArrayList<>();
        List<Receipt> previousMonthReceipts = new ArrayList<>();

        // Clasificar los recibos según su fecha en las listas correspondientes
        for (Receipt receipt : receipts) {
            Date receiptDate = receipt.getDate();
            Calendar receiptCalendar = Calendar.getInstance();
            receiptCalendar.setTime(receiptDate);

            // Establecer la hora, minuto, segundo y milisegundo en cero
            receiptCalendar.set(Calendar.HOUR_OF_DAY, 0);
            receiptCalendar.set(Calendar.MINUTE, 0);
            receiptCalendar.set(Calendar.SECOND, 0);
            receiptCalendar.set(Calendar.MILLISECOND, 0);

            // Comparar la fecha del recibo con la fecha del último mes
            if (receiptCalendar.get(Calendar.YEAR) == lastMonthCalendar.get(Calendar.YEAR)
                    && receiptCalendar.get(Calendar.MONTH) == lastMonthCalendar.get(Calendar.MONTH)) {
                lastMonthReceipts.add(receipt);
            } else if (receiptCalendar.get(Calendar.YEAR) == lastMonthCalendar.get(Calendar.YEAR)
                    && receiptCalendar.get(Calendar.MONTH) == lastMonthCalendar.get(Calendar.MONTH) - 1) {
                previousMonthReceipts.add(receipt);
            }
        }

        // Calcular la suma de los precios del último mes y el mes anterior
        Double sumLastMonth = 0D;
        for (Receipt receipt : lastMonthReceipts) {
            sumLastMonth += receipt.getPrice();
        }

        Double sumPreviousMonth = 0D;
        for (Receipt receipt : previousMonthReceipts) {
            sumPreviousMonth += receipt.getPrice();
        }

        // Calcular la diferencia y el porcentaje
        Double difference;
        if (sumLastMonth == 0D || sumPreviousMonth == 0D) {
            difference = 0D;
        } else {
            difference = sumLastMonth - sumPreviousMonth;
        }

        Double percentage;
        if (sumPreviousMonth == 0.0) {
            percentage = 0.0;
        } else {
            percentage = Math.abs((difference / sumPreviousMonth) * 100);
        }

        PercentageStatisticDto percentageStatisticDto = new PercentageStatisticDto();
        percentageStatisticDto.setSumLastMonth(sumLastMonth);
        percentageStatisticDto.setSumCurrentMonth(sumPreviousMonth);
        percentageStatisticDto.setDifference(difference);
        percentageStatisticDto.setPercentage(percentage);

        return percentageStatisticDto;
    }

}