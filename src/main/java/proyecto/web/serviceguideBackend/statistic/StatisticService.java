package proyecto.web.serviceguideBackend.statistic;

import com.itextpdf.io.exceptions.IOException;
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
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
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

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
            StatisticDto statisticDto = new StatisticDto();
            List<Double[]> prices = new ArrayList<>();
            List<Float[]> amount = new ArrayList<>();
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

        Optional<Receipt> optionalReceipt = receiptRepository.findById(idReceipt);
        if (optionalReceipt.isEmpty()) {
            throw new AppException("Receipt not found", HttpStatus.NOT_FOUND);
        }
        Long idUser = receiptRepository.findUserByReceiptId(idReceipt);
        Optional<User> optionalUser = userRepository.findById(idUser);
        if (optionalUser.isEmpty()) {
            throw new AppException("User not found", HttpStatus.NOT_FOUND);
        }
        TypeService typeService = TypeService.valueOf(typeReceipt);
        List<Long> collectionId = receiptRepository.getIdByUser(optionalUser.get().getId(), typeService);
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
        Float[] amount = {receiptList.get(0).getAmount(), receiptList.get(1).getAmount()};

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

        StatisticType statisticType = StatisticType.valueOf(typeGraphic);

        /*Se crea la nueva estadistica y se le pasan los datos generados*/
        Statistic statistic = new Statistic();
        statistic.setLabel(label);
        statistic.setPrice(price);
        statistic.setAmount(amount);
        statistic.setStatisticsType(statisticType);
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
    public double[] sumStatisticByType(Long idUser, String house) {
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
            String typeServiceName = receipt.getTypeService().name();

            switch (typeServiceName) {
                case "WATER" -> waterSum += receiptAmount;
                case "ENERGY" -> energySum += receiptAmount;
                case "GAS" -> gasSum += receiptAmount;
                case "SEWERAGE" -> sewerageSum += receiptAmount;
            }
        }

        // Crear un array de double y almacenar las sumas por tipo

        return new double[]{ waterSum, energySum, gasSum, sewerageSum };
    }

    @Override
    public SumOfReceiptDto sumOfReceiptDto(Long idHouse) {
        Optional<House> optionalHouse = houseRepository.findById(idHouse);
        if (optionalHouse.isEmpty()) {
            throw new AppException("Casa no encontrada", HttpStatus.NOT_FOUND);
        }
        Collection<Receipt> receiptList = receiptRepository.findByHouse(optionalHouse.get());
        List<YearMonth> yearMonths = new ArrayList<>();

        for (Receipt receipt : receiptList) {
            Date receiptDate = receipt.getDate();
            Date utilDate = new Date(receiptDate.getTime());
            Instant instant = utilDate.toInstant();
            LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            YearMonth yearMonth = YearMonth.of(localDate.getYear(), localDate.getMonth());
            yearMonths.add(yearMonth);
        }

        Comparator<YearMonth> customComparator = Comparator
                .comparing(YearMonth::getYear)
                .thenComparing(YearMonth::getMonthValue);

        List<YearMonth> sortedYearMonths = yearMonths.stream()
                .distinct()
                .sorted(customComparator)
                .collect(Collectors.toList());

        if (sortedYearMonths.size() == 1) {
            double sumPriceLatest = 0D;
            for (Receipt receipt : receiptList) {
                sumPriceLatest += receipt.getPrice();
            }
            SumOfReceiptDto sumOfReceiptDto = new SumOfReceiptDto();
            sumOfReceiptDto.setSumMonth((float) sumPriceLatest);
            sumOfReceiptDto.setPercentage(0F);
            sumOfReceiptDto.setDifference(0F);
            sumOfReceiptDto.setLastSumMonth(0F);
            return sumOfReceiptDto;
        }

        double sumPriceLatest = 0D;
        double sumPriceLast = 0D;
        double percentage;
        double difference;

        // Obtener los últimos dos elementos de la lista ordenada
        List<YearMonth> lastTwoMonths = sortedYearMonths.subList(Math.max(0, sortedYearMonths.size() - 2), sortedYearMonths.size());

        for (int i = 0; i < lastTwoMonths.size(); i++) {
            Collection<Receipt> list = receiptRepository.listReceiptByHouseAndMonthAndYear(idHouse, lastTwoMonths.get(i).getMonthValue(), lastTwoMonths.get(i).getYear());
            for (Receipt receipt : list) {
                if (i == 0) {
                    sumPriceLast += receipt.getPrice();
                } else {
                    sumPriceLatest += receipt.getPrice();
                }
            }
        }

        difference = sumPriceLast - sumPriceLatest;
        percentage = (difference / sumPriceLast) * 100;

        SumOfReceiptDto sumOfReceiptDto = new SumOfReceiptDto();
        sumOfReceiptDto.setSumMonth((float) sumPriceLatest);
        sumOfReceiptDto.setDifference((float) difference);
        sumOfReceiptDto.setLastSumMonth((float) sumPriceLast);
        sumOfReceiptDto.setPercentage((float) percentage);

        return sumOfReceiptDto;
    }

    @Override
    public ResponseEntity<ByteArrayResource> generateReportPDF(Long userId, Long houseId) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Crear el documento PDF
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Agregar el título del informe
            Paragraph title = new Paragraph("REPORTE ESTADÍSTICO");
            title.setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Agregar espacio
            document.add(new Paragraph("\n"));

            // Agregar el subtítulo con la fecha a la derecha
            Paragraph subtitle = new Paragraph("Fecha: " + obtenerFechaActual());
            subtitle.setTextAlignment(TextAlignment.RIGHT);
            document.add(subtitle);

            document.add(new Paragraph("\n"));

            Paragraph subtitleInformation = new Paragraph("Información de evaluación");
            document.add(subtitleInformation);

            document.add(new Paragraph("\n"));

            // Agregar el cuadro con la frase centrada
            DeviceRgb borderColor = new DeviceRgb(10, 152, 197);
            Table tableParagraph = new Table(UnitValue.createPercentArray(new float[]{200f}));
            Cell cell_1 = new Cell().add(new Paragraph("La información que encontrará a continuación fue tomada teniendo en cuenta los siguiente datos"));
            cell_1.setTextAlignment(TextAlignment.CENTER);
            cell_1.setBorderRadius(new BorderRadius(10));
            cell_1.setBorder(new SolidBorder(borderColor, 2));
            tableParagraph.addCell(cell_1);

            document.add(tableParagraph);

            document.add(new Paragraph("\n"));

            // Agregar tabla con la informacaión de los recibos
            List<Receipt> lastTwoMonthsReceipts = obtenerUltimosRecibos(userId, houseId);
            agregarTablaRecibos(document, lastTwoMonthsReceipts);

            document.add(new Paragraph("\n"));

            // Agregar el título del informe
            Paragraph logo = new Paragraph("ServiceGuide");
            Paragraph slogan = new Paragraph("'Producción y consumo responsable'");
            logo.setTextAlignment(TextAlignment.CENTER);
            slogan.setTextAlignment(TextAlignment.CENTER);
            document.add(logo);
            document.add(slogan);

            // Agregar línea después del título
            DeviceRgb deviceRgb = new DeviceRgb(10, 152, 197);
            SolidLine solidLine = new SolidLine(2);
            solidLine.setColor(deviceRgb);

            LineSeparator line = new LineSeparator(solidLine);

            document.add(line);

            //NUEVA PÁGINA
            pdf.addNewPage();

            document.add(title);

            

            document.close();

            // Configurar encabezados para la respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "reporte_estadistico.pdf");

            // Crear un recurso ByteArray para la respuesta
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

            // Devolver una respuesta con el recurso y los encabezados
            return ResponseEntity.ok().headers(headers).body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    private String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es", "ES"));
        return dateFormat.format(new Date());
    }

    private List<Receipt> obtenerUltimosRecibos(Long userId, Long houseId) {
        // Obtener la fecha de inicio (hace dos meses desde la fecha actual)
        LocalDate startDate = LocalDate.now().minusMonths(2);
        Date startDateParam = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Obtener la fecha de fin (fecha actual)
        LocalDate endDate = LocalDate.now();
        Date endDateParam = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return receiptRepository.findLastTwoMonthsReceipts(userId, houseId, startDateParam, endDateParam);
    }

    private void agregarTablaRecibos(Document document, List<Receipt> lastTwoMonthsReceipts) {
        Table table = new Table(new float[]{2, 2, 2, 2, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));
        table.setTextAlignment(TextAlignment.CENTER);

        // Agregar encabezados de la tabla
        table.addCell(new Cell().add(new Paragraph("Receipt Name")));
        table.addCell(new Cell().add(new Paragraph("Date")));
        table.addCell(new Cell().add(new Paragraph("Amount")));
        table.addCell(new Cell().add(new Paragraph("Price")));
        table.addCell(new Cell().add(new Paragraph("Type Service")));
        table.addCell(new Cell().add(new Paragraph("House Name")));

        // Agregar filas con la información de los recibos
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
    
}