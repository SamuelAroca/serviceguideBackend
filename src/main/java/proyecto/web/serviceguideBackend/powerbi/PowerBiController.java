package proyecto.web.serviceguideBackend.powerbi;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PowerBiController {

    private final PowerBiService powerBiService;

    @GetMapping("/api/powerbi/embed-token")
    public String getEmbedToken() {
        return powerBiService.getEmbedToken();
    }
}
