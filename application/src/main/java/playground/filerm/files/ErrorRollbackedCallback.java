package playground.filerm.files;

import lombok.extern.slf4j.Slf4j;
import resource.model.ErrorCallback;

@Slf4j
public class ErrorRollbackedCallback implements ErrorCallback {
    @Override
    public void handle(String transactionId) {
        log.info("Im handling rollbacked error");
    }
}
