package proyecto.web.serviceguideBackend.statistic;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;

import com.itextpdf.layout.properties.BorderRadius;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import proyecto.web.serviceguideBackend.exceptions.AppException;
import proyecto.web.serviceguideBackend.house.House;
import proyecto.web.serviceguideBackend.house.interfaces.HouseRepository;
import proyecto.web.serviceguideBackend.receipt.Receipt;
import proyecto.web.serviceguideBackend.receipt.interfaces.ReceiptRepository;
import proyecto.web.serviceguideBackend.receipt.typeService.TypeService;
import proyecto.web.serviceguideBackend.statistic.dto.StatisticDto;
import proyecto.web.serviceguideBackend.statistic.dto.SumOfReceiptDto;
import proyecto.web.serviceguideBackend.statistic.interfaces.StatisticInterface;
import proyecto.web.serviceguideBackend.statistic.interfaces.StatisticMapper;
import proyecto.web.serviceguideBackend.statistic.interfaces.StatisticRepository;
import proyecto.web.serviceguideBackend.statistic.statisticType.StatisticType;
import proyecto.web.serviceguideBackend.user.User;
import proyecto.web.serviceguideBackend.user.interfaces.UserRepository;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatisticService implements StatisticInterface {

    private final ReceiptRepository receiptRepository;
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper;
    private final UserRepository userRepository;
    private final HouseRepository houseRepository;

    @Override
    public StatisticDto individualReceipt(String typeReceipt, Long idReceipt, String typeGraphic) {
        List<Statistic> statistics = statisticRepository.getStatisticByReceipt(idReceipt);
        if (!statistics.isEmpty()) {
            Statistic lastStatistic = statistics.get(statistics.size() - 1);
            StatisticDto statisticDto = new StatisticDto();
            statisticDto.setPrice(lastStatistic.getPrice());
            statisticDto.setAmount(lastStatistic.getAmount());
            statisticDto.setLabel(lastStatistic.getLabel());
            statisticDto.setId(lastStatistic.getId());
            statisticDto.setStatisticsType(lastStatistic.getStatisticsType());
            return statisticDto;
        }

        Receipt currentReceipt = receiptRepository.findById(idReceipt)
                .orElseThrow(() -> new AppException("Receipt not found", HttpStatus.NOT_FOUND));

        Long idUser = receiptRepository.findUserByReceiptId(idReceipt);
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        TypeService typeService = TypeService.valueOf(typeReceipt);
        List<Long> collectionId = receiptRepository.getIdByUser(user.getId(), typeService);

        List<Long> idList = new ArrayList<>();
        for (int i = 0; i < 2 && i < collectionId.size(); i++) {
            idList.add(collectionId.get(i));
        }

        if (idList.size() < 2) {
            throw new AppException("Recibo creado pero no se puede generar la estadistica", HttpStatus.OK);
        }

        List<Receipt> receiptList = receiptRepository.getTwoReceiptsById(idList.get(0), idList.get(1));

        Double[] price = {receiptList.get(0).getPrice(), receiptList.get(1).getPrice()};
        Float[] amount = {receiptList.get(0).getAmount(), receiptList.get(1).getAmount()};

        String monthCapitalize1 = capitalizedSpanishMonth(receiptList.get(0).getDate());
        String monthCapitalize2 = capitalizedSpanishMonth(receiptList.get(1).getDate());
        String[] label = {monthCapitalize1, monthCapitalize2};

        StatisticType statisticType = StatisticType.valueOf(typeGraphic);

        Statistic statistic = new Statistic();
        statistic.setLabel(label);
        statistic.setPrice(price);
        statistic.setAmount(amount);
        statistic.setStatisticsType(statisticType);
        statisticRepository.save(statistic);

        currentReceipt.setStatistics(new ArrayList<>(List.of(statistic)));
        receiptRepository.save(currentReceipt);

        return statisticMapper.statisticDto(statistic);
    }

    @Override
    public double[] sumStatisticByType(Long idUser, String house) {
        Collection<Receipt> receipts = receiptRepository.getAllReceiptsByHouse(idUser, house);

        if (receipts.isEmpty()) {
            throw new AppException("No se encontraron recibos", HttpStatus.NOT_FOUND);
        }

        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        houseRepository.findByUserAndName(user, house)
                .orElseThrow(() -> new AppException("House not found", HttpStatus.NOT_FOUND));

        double waterSum = 0;
        double energySum = 0;
        double gasSum = 0;
        double sewerageSum = 0;

        for (Receipt receipt : receipts) {
            double receiptAmount = receipt.getAmount().doubleValue();
            switch (receipt.getTypeService().name()) {
                case "WATER" -> waterSum += receiptAmount;
                case "ENERGY" -> energySum += receiptAmount;
                case "GAS" -> gasSum += receiptAmount;
                case "SEWERAGE" -> sewerageSum += receiptAmount;
            }
        }

        return new double[]{waterSum, energySum, gasSum, sewerageSum};
    }

    @Override
    public SumOfReceiptDto sumOfReceiptDto(Long idHouse) {
        House house = houseRepository.findById(idHouse)
                .orElseThrow(() -> new AppException("Casa no encontrada", HttpStatus.NOT_FOUND));

        Collection<Receipt> receiptList = receiptRepository.findByHouse(house);

        Comparator<YearMonth> byYearThenMonth = Comparator
                .comparing(YearMonth::getYear)
                .thenComparing(YearMonth::getMonthValue);

        List<YearMonth> sortedYearMonths = receiptList.stream()
                .map(receipt -> toLocalDate(receipt.getDate()))
                .map(localDate -> YearMonth.of(localDate.getYear(), localDate.getMonth()))
                .distinct()
                .sorted(byYearThenMonth)
                .toList();

        SumOfReceiptDto sumOfReceiptDto = new SumOfReceiptDto();

        if (sortedYearMonths.size() == 1) {
            double sumPriceLatest = receiptList.stream().mapToDouble(Receipt::getPrice).sum();
            sumOfReceiptDto.setSumMonth((float) sumPriceLatest);
            sumOfReceiptDto.setPercentage(0F);
            sumOfReceiptDto.setDifference(0F);
            sumOfReceiptDto.setLastSumMonth(0F);
            return sumOfReceiptDto;
        }

        List<YearMonth> lastTwoMonths = sortedYearMonths.subList(Math.max(0, sortedYearMonths.size() - 2), sortedYearMonths.size());

        // receiptList ya trae todos los recibos de la casa; se agrupan en memoria
        // en vez de volver a consultar la BD por cada uno de los últimos dos meses.
        Map<YearMonth, Double> sumByYearMonth = receiptList.stream()
                .collect(Collectors.groupingBy(
                        receipt -> YearMonth.from(toLocalDate(receipt.getDate())),
                        Collectors.summingDouble(Receipt::getPrice)));

        double sumPriceLast = sumByYearMonth.getOrDefault(lastTwoMonths.get(0), 0D);
        double sumPriceLatest = sumByYearMonth.getOrDefault(lastTwoMonths.get(1), 0D);

        double difference = sumPriceLast - sumPriceLatest;
        double percentage = (difference / sumPriceLast) * 100;

        sumOfReceiptDto.setSumMonth((float) sumPriceLatest);
        sumOfReceiptDto.setDifference((float) difference);
        sumOfReceiptDto.setLastSumMonth((float) sumPriceLast);
        sumOfReceiptDto.setPercentage((float) percentage);

        return sumOfReceiptDto;
    }

    @Override
    public ResponseEntity<ByteArrayResource> generateReportPDF(Long userId, Long houseId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (Document document = new Document(new PdfDocument(new PdfWriter(baos)))) {
            PdfDocument pdf = document.getPdfDocument();

            Paragraph title = new Paragraph("REPORTE ESTADÍSTICO");
            title.setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            Paragraph subtitle = new Paragraph("Fecha: " + obtenerFechaActual());
            subtitle.setTextAlignment(TextAlignment.RIGHT);
            document.add(subtitle);
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("Información de evaluación"));
            document.add(new Paragraph("\n"));

            DeviceRgb borderColor = new DeviceRgb(10, 152, 197);
            Table tableParagraph = new Table(UnitValue.createPercentArray(new float[]{200f}));
            Cell infoCell = new Cell().add(new Paragraph(
                    "La información que encontrará a continuación fue tomada teniendo en cuenta los siguiente datos"));
            infoCell.setTextAlignment(TextAlignment.CENTER);
            infoCell.setBorderRadius(new BorderRadius(10));
            infoCell.setBorder(new SolidBorder(borderColor, 2));
            tableParagraph.addCell(infoCell);
            document.add(tableParagraph);
            document.add(new Paragraph("\n"));

            List<Receipt> lastTwoMonthsReceipts = obtenerUltimosRecibos(userId, houseId);
            agregarTablaRecibos(document, lastTwoMonthsReceipts);
            document.add(new Paragraph("\n"));

            Paragraph logo = new Paragraph("ServiceGuide");
            Paragraph slogan = new Paragraph("'Producción y consumo responsable'");
            logo.setTextAlignment(TextAlignment.CENTER);
            slogan.setTextAlignment(TextAlignment.CENTER);
            document.add(logo);
            document.add(slogan);

            DeviceRgb lineColor = new DeviceRgb(10, 152, 197);
            SolidLine solidLine = new SolidLine(2);
            solidLine.setColor(lineColor);
            document.add(new LineSeparator(solidLine));

            pdf.addNewPage();
            document.add(title);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "reporte_estadistico.pdf");

            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
            return ResponseEntity.ok().headers(headers).body(resource);

        } catch (Exception e) {
            log.error("Error generando el reporte PDF para userId={}, houseId={}", userId, houseId, e);
            return ResponseEntity.status(500).body(null);
        }
    }

    private String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale.of("es", "ES"));
        return dateFormat.format(new Date());
    }

    private List<Receipt> obtenerUltimosRecibos(Long userId, Long houseId) {
        LocalDate startDate = LocalDate.now().minusMonths(2);
        Date startDateParam = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDate endDate = LocalDate.now();
        Date endDateParam = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return receiptRepository.findLastTwoMonthsReceipts(userId, houseId, startDateParam, endDateParam);
    }

    private void agregarTablaRecibos(Document document, List<Receipt> lastTwoMonthsReceipts) {
        Table table = new Table(new float[]{2, 2, 2, 2, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setTextAlignment(TextAlignment.CENTER);

        Stream.of("Receipt Name", "Date", "Amount", "Price", "Type Service", "House Name")
                .forEach(header -> table.addCell(new Cell().add(new Paragraph(header))));

        for (Receipt receipt : lastTwoMonthsReceipts) {
            table.addCell(new Cell().add(new Paragraph(receipt.getReceiptName())));
            table.addCell(new Cell().add(new Paragraph(receipt.getDate().toString())));
            table.addCell(new Cell().add(new Paragraph(receipt.getAmount().toString())));
            table.addCell(new Cell().add(new Paragraph(receipt.getPrice().toString())));
            table.addCell(new Cell().add(new Paragraph(receipt.getTypeService().toString())));
            table.addCell(new Cell().add(new Paragraph(receipt.getHouse().getName())));
        }

        document.add(table);
    }

    private static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static String capitalizedSpanishMonth(Date date) {
        String monthName = toLocalDate(date).getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es-ES"));
        return monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
    }
}