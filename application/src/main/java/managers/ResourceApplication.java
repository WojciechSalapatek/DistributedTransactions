package managers;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import resource.configuration.EnableResourceManager;

import static org.springframework.boot.SpringApplication.run;

@EnableResourceManager
@SpringBootApplication
@ComponentScan(basePackages = {"resource", "managers"})
public class ResourceApplication {

    public static void main(String[] args) {
        run(ResourceApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
