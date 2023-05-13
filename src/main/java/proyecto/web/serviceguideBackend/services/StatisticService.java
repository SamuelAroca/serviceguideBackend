package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.Statistic;
import proyecto.web.serviceguideBackend.entities.StatisticType;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.StatisticMapper;
import proyecto.web.serviceguideBackend.repositories.ReceiptRepository;
import proyecto.web.serviceguideBackend.repositories.StatisticRepository;
import proyecto.web.serviceguideBackend.repositories.StatisticTypeRepository;
import proyecto.web.serviceguideBackend.serviceInterface.StatisticInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StatisticService implements StatisticInterface{

    private final ReceiptRepository receiptRepository;
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;
    private final StatisticTypeRepository statisticTypeRepository;

    @Override
    public StatisticDto individualReceipt(String type, Long idReceipt) {
        /*Collection<Statistic> statistics = statisticRepository.getStatisticByTwoReceipt();
        if (!statistics.isEmpty()) {
            StatisticDto statisticDto = new StatisticDto();
            List<Double[]> prices = new ArrayList<>();
            for (Statistic statistic : statistics) {
                prices.add(statistic.getData());
            }
            statisticDto.setData(prices.get(0));
            return statisticDto;
        }*/
        Optional<StatisticType> optionalStatisticType = statisticTypeRepository.findByTypeIgnoreCase(type);
        if (optionalStatisticType.isEmpty()) {
            throw new AppException("Type not found", HttpStatus.NOT_FOUND);
        }
        Optional<Receipt> optionalReceipt = receiptRepository.findById(idReceipt);
        if (optionalReceipt.isEmpty()) {
            throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
        }
        StatisticDto statisticDto = new StatisticDto();
        Double[] number = {1d, 2d};
        String[] label = {"enero", "febrero"};

        Statistic statistic = new Statistic();
        statistic.setLabel(label);
        statistic.setData(number);
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
}
