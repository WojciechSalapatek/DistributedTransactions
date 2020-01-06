package coordinator.service;

import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.function.Consumer;

public class CoordinatorListenableFutureCallback implements ListenableFutureCallback {

    private Consumer<Throwable> ex;
    private Consumer<ResponseEntity<String>> success;

    public CoordinatorListenableFutureCallback(Consumer<Throwable> ex, Consumer<ResponseEntity<String>> success) {
        this.ex = ex;
        this.success = success;
    }

    @Override
    public void onFailure(Throwable throwable) {
        ex.accept(throwable);
    }

    @Override
    public void onSuccess(Object o) {
        success.accept((ResponseEntity<String>) o);
    }
}
