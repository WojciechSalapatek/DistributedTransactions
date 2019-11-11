package playground.coordinator;

import coordinator.config.CoordinatorApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.boot.SpringApplication.run;

@Slf4j
@SpringBootApplication
@CoordinatorApplication
@ComponentScan(basePackages = "coordinator")
public class CoordinatorStandaloneApplication {

    public static void main(String[] args) {
        run(CoordinatorStandaloneApplication.class, args);
    }

}
