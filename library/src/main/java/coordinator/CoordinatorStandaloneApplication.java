package coordinator;

import coordinator.config.CoordinatorApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import static org.springframework.boot.SpringApplication.run;

@Slf4j
@SpringBootApplication
@CoordinatorApplication
@PropertySource("classpath:application-coordinator.properties")
public class CoordinatorStandaloneApplication {

    public static void main(String[] args) {
        run(CoordinatorStandaloneApplication.class, args);
    }

}
