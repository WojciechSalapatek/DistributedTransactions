package coordinator.service;

import coordinator.model.ParticipantCommand;
import coordinator.model.ParticipantRequestParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.function.Consumer;

@Slf4j
@Service
public class ParticipantRestService {

    private AsyncRestTemplate restTemplate = new AsyncRestTemplate();

    public ListenableFuture<ResponseEntity<String>> sendCommand(String resourceManagerId, String transactionId, String address, String message, ParticipantCommand command,
                            Consumer<ResponseEntity<String>> successCallback,
                            Consumer<Throwable> errorCallback) {
        CoordinatorListenableFutureCallback callback = new CoordinatorListenableFutureCallback(errorCallback, successCallback);
        ParticipantRequestParams params = new ParticipantRequestParams(transactionId, resourceManagerId, command, message);

        return sendPost(address, params, callback);
    }

    private ListenableFuture<ResponseEntity<String>> sendPost(String address, ParticipantRequestParams params, ListenableFutureCallback<ResponseEntity<String>> callback) {
        log.debug("Sending command {} to {} with id {}", params.getCommand(), address, params.getManagerId());
        HttpEntity<ParticipantRequestParams> req = new HttpEntity<>(params);
        ListenableFuture<ResponseEntity<String>> future = restTemplate.postForEntity(address, req, String.class);
        future.addCallback(callback);
        return future;
    }
}
