package proyecto.web.serviceguideBackend.powerbi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PowerBiService {

    @Value("${powerbi.username}")
    private String username;

    @Value("${powerbi.password}")
    private String password;

    @Value("${powerbi.workspace-id}")
    private String workspaceId;

    @Value("${powerbi.report-id}")
    private String reportId;

    private final RestTemplate restTemplate;

    public PowerBiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getEmbedToken() {
        String url = "https://api.powerbi.com/v1.0/myorg/groups/" + workspaceId + "/reports/" + reportId + "/GenerateToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return response.getBody();
    }
}
