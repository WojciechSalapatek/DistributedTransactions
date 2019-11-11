package coordinator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(CoordinatorBeansConfiguration.class)
public @interface CoordinatorApplication {
}
