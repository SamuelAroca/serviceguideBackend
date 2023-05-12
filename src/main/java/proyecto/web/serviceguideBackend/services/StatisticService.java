package proyecto.web.serviceguideBackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.dto.StatisticDto;
import proyecto.web.serviceguideBackend.entities.Receipt;
import proyecto.web.serviceguideBackend.entities.Statistic;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.mappers.StatisticMapper;
import proyecto.web.serviceguideBackend.repositories.ReceiptRepository;
import proyecto.web.serviceguideBackend.repositories.StatisticRepository;
import proyecto.web.serviceguideBackend.serviceInterface.StatisticInterface;

import java.util.Collection;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class StatisticService implements StatisticInterface {

    private final StatisticRepository statisticRepository;
    private final ReceiptRepository receiptRepository;
    private final StatisticMapper statisticMapper;

    @Override
    public StatisticDto individualReceipt(Long idReceipt, String type) {

        Optional<Statistic> optionalStatistic = statisticRepository.statistic(idReceipt);
        if (optionalStatistic.isPresent()) {
            StatisticDto statisticDto = new StatisticDto();
            statisticDto.setLabel(optionalStatistic.get().getLabel());
            statisticDto.setData(optionalStatistic.get().getData());
            statisticDto.setStatisticsType(optionalStatistic.get().getStatisticsType());
            statisticDto.setReceipt(optionalStatistic.get().getReceipt());

            return statisticDto;
        }

        Optional<Receipt> optionalReceipt = receiptRepository.findById(idReceipt);
        if (optionalReceipt.isEmpty()) {
            throw new AppException("Not found", HttpStatus.NOT_FOUND);
        }
        StatisticDto statisticDto = new StatisticDto();
        statisticDto.setLabel(new String[] { "Enero", "Febrero", "Marzo" });
        statisticDto.setData(new Double[] {  });

        Long idUser = receiptRepository.findUserByReceiptId(idReceipt);
        Collection<Receipt> receiptCollection = receiptRepository.getAllReceiptsByType(idUser, type);

        for (Receipt receipt : receiptCollection) {
            double amount = receipt.getAmount();
            double price = receipt.getPrice();
            System.out.println("Amount: " + amount + ", Price: " + price);
        }


        Statistic statistic = statisticMapper.statistic();
    }
}
