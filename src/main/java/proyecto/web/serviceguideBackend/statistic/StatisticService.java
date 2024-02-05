package proyecto.web.serviceguideBackend.statistic;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageRequest;
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
            Collection<Receipt> list = receiptRepository.listReceiptByHouseAndMonth(idHouse, lastTwoMonths.get(i).getMonthValue());
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
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setFullCompression();

            document.open();

            document.setMargins(0, 0, 0, 0);

            // Agregar el título del informe
            Paragraph title = new Paragraph("REPORTE ESTADÍSTICO");
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Agregar espacio
            document.add(Chunk.NEWLINE);

            // Agregar el subtítulo con la fecha a la derecha
            Paragraph subtitle = new Paragraph("Fecha: " + obtenerFechaActual());
            subtitle.setAlignment(Element.ALIGN_RIGHT);
            document.add(subtitle);

            document.add(Chunk.NEWLINE);

            Paragraph subtitleInformation = new Paragraph("Información de evaluación");
            document.add(subtitleInformation);

            document.add(Chunk.NEWLINE);

            /* Agregar el cuadro con la frase centrada
            float rectWidth = PageSize.A4.getWidth() - 134 * 2;
            float rectHeight = 80;
            float rectX = 134;
            float rectY = 620;

            Rectangle rect = new Rectangle(rectX, rectY, rectX + rectWidth, rectY + rectHeight);
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(10);
            //rect.setBorderColor(new BaseColor(10, 152, 197));
            rect.setBorderColor(BaseColor.RED);

            PdfContentByte cb = writer.getDirectContent();

            Phrase phrase = new Phrase("La información que encontrará a continuación fue tomada teniendo en cuenta el siguiente rango de tiempo", new Font(Font.FontFamily.HELVETICA, 14)); // Ajusta el tamaño de la fuente según sea necesario

            ColumnText columnText = new ColumnText(cb);
            columnText.setSimpleColumn(rect);
            columnText.addElement(phrase);
            columnText.setAlignment(Element.ALIGN_CENTER);
            columnText.go(); */

            // Agregar el cuadro con la frase centrada
            float columnWidth[] = {200f};
            PdfPTable table = new PdfPTable(columnWidth);
            PdfPCell cell_1 = new PdfPCell();
            Paragraph paragraph = new Paragraph("La información que encontrará a continuación fue tomada teniendo en cuenta el siguiente rango de tiempo");
            cell_1.addElement(paragraph);
            table.addCell(cell_1);


            // Obtener la información de los últimos dos recibos
            List<Receipt> lastTwoReceipts = obtenerUltimosDosRecibos(userId, houseId);

            // Agregar la tabla con la información de los recibos
            try {
                agregarTablaRecibos(document, lastTwoReceipts);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            document.close();

            // Configurar encabezados para la respuesta HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "reporte_estadistico.pdf");

            // Crear un recurso ByteArray para la respuesta
            ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

            // Devolver una respuesta con el recurso y los encabezados
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(500)
                    .body(null);
        }
    }

    private String obtenerFechaActual() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'de' MMMM 'del' yyyy", new Locale("es", "ES"));
        return dateFormat.format(new Date());
    }

    private List<Receipt> obtenerUltimosDosRecibos(Long userId, Long houseId) {
        // Obtener el año y mes actuales
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Sumamos 1 porque los meses comienzan desde 0

        // Obtener el mes y año del mes pasado
        calendar.add(Calendar.MONTH, -1);
        int previousYear = calendar.get(Calendar.YEAR);
        int previousMonth = calendar.get(Calendar.MONTH) + 1;

        return receiptRepository.findLastTwoMonthsReceiptsForUserAndHouse(userId, houseId, currentYear, currentMonth, previousYear, previousMonth, PageRequest.of(0, 2));
    }

    // Método para agregar la tabla con la información de los recibos al documento
    private void agregarTablaRecibos(Document document, List<Receipt> receipts) throws DocumentException {
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        // Agregar encabezados de columna
        agregarCeldaEncabezado(table, "ID");
        agregarCeldaEncabezado(table, "Amount");
        agregarCeldaEncabezado(table, "Date");
        agregarCeldaEncabezado(table, "House Name");
        agregarCeldaEncabezado(table, "Price");
        agregarCeldaEncabezado(table, "Receipt Name");
        agregarCeldaEncabezado(table, "Type Service");

        // Agregar filas con información de los recibos
        for (Receipt receipt : receipts) {
            agregarCelda(table, receipt.getId().toString());
            agregarCelda(table, receipt.getAmount().toString());
            agregarCelda(table, obtenerFechaFormateada(receipt.getDate()));
            agregarCelda(table, receipt.getHouseName());
            agregarCelda(table, receipt.getPrice().toString());
            agregarCelda(table, receipt.getReceiptName());
            agregarCelda(table, receipt.getTypeService().toString());
        }

        document.add(table);
    }

    private void agregarCeldaEncabezado(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

    private void agregarCelda(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 12)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private String obtenerFechaFormateada(Date fecha) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES"));
        return dateFormat.format(fecha);
    }
    
}