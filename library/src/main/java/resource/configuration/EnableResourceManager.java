package resource.configuration;

import org.springframework.context.annotation.Import;

@Import(Config.class)
public @interface EnableResourceManager {
}
