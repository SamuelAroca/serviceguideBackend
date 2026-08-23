package proyecto.web.serviceguideBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ServiceguideBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceguideBackendApplication.class, args);
    }
}