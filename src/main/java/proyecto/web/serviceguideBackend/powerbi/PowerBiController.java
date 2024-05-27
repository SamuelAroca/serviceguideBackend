package proyecto.web.serviceguideBackend.powerbi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PowerBiController {

    private final PowerBiService powerBiService;

    public PowerBiController(PowerBiService powerBiService) {
        this.powerBiService = powerBiService;
    }

    @GetMapping("/api/powerbi/embed-token")
    public String getEmbedToken() {
        return powerBiService.getEmbedToken();
    }
}
