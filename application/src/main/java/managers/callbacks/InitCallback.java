package managers.callbacks;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.Notifications;
import resource.model.ErrorCallback;

@Slf4j
public class InitCallback implements ErrorCallback {
    @Override
    public void handle(String transactionId) {
        log.info("Im handling init error");
        Platform.runLater(() -> {
            Notifications.create()
                    .title("Init Error!")
                    .text("Transaction has not been started as not all required resources registered for it, no changes has been made")
                    .showWarning();
        });
    }
}
