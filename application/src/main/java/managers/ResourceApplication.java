package managers;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;
import resource.configuration.EnableResourceManager;

import static org.springframework.boot.SpringApplication.run;

@EnableResourceManager
@SpringBootApplication
@ComponentScan(basePackages = {"resource", "managers"})
public class ResourceApplication extends javafx.application.Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ResourceApplication.class);
        context = builder.run(getParameters().getRaw().toArray(new String[0]));
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/window.fxml"));
        Parent root = (Parent)fxmlLoader.load();

        stage.setScene(new Scene(root, 300, 250));
        ((GUIController) fxmlLoader.getController()).init();
        stage.show();
    }
}
