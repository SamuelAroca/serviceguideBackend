package proyecto.web.serviceguideBackend.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor;
import com.itextpdf.kernel.pdf.canvas.parser.listener.ITextExtractionStrategy;
import com.itextpdf.kernel.pdf.canvas.parser.listener.SimpleTextExtractionStrategy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import proyecto.web.serviceguideBackend.config.JwtService;
import proyecto.web.serviceguideBackend.exceptions.AppException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class Utils {

    private final JwtService jwtService;

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

    public Long getTokenFromRequest(HttpServletRequest request) {
        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                return jwtService.whoIsMyId(authHeader.substring(7));
            }
        } catch (Exception e) {
            throw new AppException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }
}