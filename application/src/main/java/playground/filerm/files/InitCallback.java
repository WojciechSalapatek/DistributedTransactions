package playground.filerm.files;

import lombok.extern.slf4j.Slf4j;
import resource.model.ErrorCallback;

@Slf4j
public class InitCallback implements ErrorCallback {
    @Override
    public void handle(String transactionId) {
        log.info("Im handling init error");
    }
}
