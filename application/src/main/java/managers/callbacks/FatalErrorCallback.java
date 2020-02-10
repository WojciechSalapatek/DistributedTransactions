package managers.callbacks;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.controlsfx.control.Notifications;
import resource.model.ErrorCallback;

@Slf4j
public class FatalErrorCallback implements ErrorCallback {

    @Override
    public void handle(String transactionId) {
        log.info("Im handling fatal error");
        Platform.runLater(() -> {
            Notifications.create()
                    .title("Transaction unsuccessful!")
                    .text("An attempt to rollback transaction was unsuccessful, data may be corrupt")
                    .showError();
        });
    }
}
