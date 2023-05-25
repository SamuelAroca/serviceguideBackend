package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.Statistic;
import proyecto.web.serviceguideBackend.entities.StatisticType;
import proyecto.web.serviceguideBackend.entities.User;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.StatisticMapper;
import proyecto.web.serviceguideBackend.repositories.ReceiptRepository;
import proyecto.web.serviceguideBackend.repositories.StatisticRepository;
import proyecto.web.serviceguideBackend.repositories.StatisticTypeRepository;
import proyecto.web.serviceguideBackend.repositories.UserRepository;
import proyecto.web.serviceguideBackend.serviceInterface.StatisticInterface;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@RequiredArgsConstructor
@Service
public class StatisticService implements StatisticInterface{

    private final ReceiptRepository receiptRepository;
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;
    private final StatisticTypeRepository statisticTypeRepository;
    private final UserRepository userRepository;

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
    public StatisticDto getStatisticByType(String typeReceipt, Long idUser, String typeGraphic) {
        Collection<Receipt> receipts = receiptRepository.getAllReceiptsByType(idUser, typeReceipt);

        if (receipts.isEmpty()) {
            throw new AppException("No se encontraron recibos para el usuario y tipo especificado", HttpStatus.NOT_FOUND);
        }

        List<Double> receiptIds = new ArrayList<>();
        List<Double[]> prices = new ArrayList<>();
        List<Double[]> amounts = new ArrayList<>();
        List<String[]> labels = new ArrayList<>();

        for (Receipt receipt : receipts) {
            receiptIds.add(Double.valueOf(receipt.getId()));
            prices.add(new Double[]{receipt.getPrice()});
            amounts.add(new Double[]{receipt.getAmount()});
            labels.add(new String[]{receipt.getReceiptName()});
        }

        List<Double> allPrices = new ArrayList<>();
        List<Double> allAmounts = new ArrayList<>();
        List<String> allNames = new ArrayList<>();

        for (Double[] priceArray : prices) {
            allPrices.addAll(Arrays.asList(priceArray));
        }

        for (Double[] amountArray : amounts) {
            allAmounts.addAll(Arrays.asList(amountArray));
        }

        for (String[] namesArray : labels) {
            allNames.addAll(Arrays.asList(namesArray));
        }

        Double[] allPricesArray = allPrices.toArray(new Double[0]);
        Double[] allAmountsArray = allAmounts.toArray(new Double[0]);
        String[] allNamesArray = allNames.toArray(new String[0]);

        Statistic statistic = new Statistic();
        statistic.setLabel(labels.toArray(new String[0]));
        statistic.setPrice(allPricesArray);
        statistic.setAmount(allAmountsArray);
        statistic.setLabel(allNamesArray);

        Optional<StatisticType> optionalStatisticType = statisticTypeRepository.findByTypeIgnoreCase(typeGraphic);
        if (optionalStatisticType.isEmpty()) {
            throw new AppException("Tipo no encontrado", HttpStatus.NOT_FOUND);
        }

        statistic.setStatisticsType(optionalStatisticType.get());
        statisticRepository.save(statistic);

        List<Statistic> statisticList = new ArrayList<>();
        statisticList.add(statistic);

        for (Receipt receipt : receipts) {
            receipt.setStatistics(statisticList);
            receiptRepository.save(receipt);
        }

        StatisticDto statisticDto = statisticMapper.statisticDto(statistic);
        return statisticDto;
    }


}
