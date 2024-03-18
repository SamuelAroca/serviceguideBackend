package proyecto.web.serviceguideBackend.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyecto.web.serviceguideBackend.exceptions.AppException;

import java.io.IOException;

@Service
public class Utils {

    public String readPdf(MultipartFile file) {
        try {
            PdfReader pdfReader = new PdfReader(file.getInputStream());
            PdfDocument pdfDocument = new PdfDocument(pdfReader);

            // Obtener la página 2
            PdfPage page = pdfDocument.getPage(2);

            // Configurar la estrategia de extracción de texto
            ITextExtractionStrategy strategy = new SimpleTextExtractionStrategy();

            // Procesar la página usando la estrategia
            PdfCanvasProcessor processor = new PdfCanvasProcessor(strategy);
            processor.processPageContent(page);

            // Obtener el texto de la estrategia
            String textoPagina = strategy.getResultantText();

            // Cerrar el lector y el documento de PDF
            pdfDocument.close();
            pdfReader.close();

            return textoPagina;
        } catch (IOException e) {
            throw new AppException("Error al procesar el archivo PDF", HttpStatus.BAD_REQUEST);
        }
    }
}