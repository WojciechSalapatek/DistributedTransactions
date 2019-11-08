package com.elmachos.distransactions.library.coordinator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static org.springframework.boot.SpringApplication.run;

@Slf4j
@EnableWebMvc
@EnableSwagger2
@SpringBootApplication
public class CoordinatorApplication {

    public static void main(String[] args) {
        run(CoordinatorApplication.class, args);
        log.info("Started Coordinator Service");
    }

}
