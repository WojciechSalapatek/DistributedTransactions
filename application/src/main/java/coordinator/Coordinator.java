package coordinator;

import coordinator.config.CoordinatorApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

@SpringBootApplication
@CoordinatorApplication
public class Coordinator {

    public static void main(String[] args) {
        run(Coordinator.class, args);
    }

}
