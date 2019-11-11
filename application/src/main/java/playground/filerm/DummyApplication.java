package playground.filerm;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import resource.configuration.EnableResourceManager;

import static org.springframework.boot.SpringApplication.run;

@EnableResourceManager
@SpringBootApplication
@ImportResource
@ComponentScan(basePackages = {"resource", "playground.filerm"})
public class DummyApplication {

    public static void main(String[] args) {
        run(DummyApplication.class, args);
    }

}
