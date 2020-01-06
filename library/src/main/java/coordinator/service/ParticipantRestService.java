package coordinator.service;

import coordinator.model.Participant;
import coordinator.model.ParticipantCommand;
import coordinator.model.ParticipantRequestParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

@Slf4j
@Service
public class ParticipantRestService {

    private AsyncRestTemplate restTemplate = new AsyncRestTemplate();

    public ListenableFuture sendCommand(String resourceManagerId, String transactionId, String address, String message, ParticipantCommand command,
                            Consumer<ResponseEntity<String>> successCallback,
                            Consumer<Throwable> errorCallback) {
        CoordinatorListenableFutureCallback callback = new CoordinatorListenableFutureCallback(errorCallback, successCallback);
        ParticipantRequestParams params = new ParticipantRequestParams(transactionId, resourceManagerId, command, message);

        return sendPost(address, params, callback);
    }

    private ListenableFuture sendPost(String address, ParticipantRequestParams params, ListenableFutureCallback<ResponseEntity<String>> callback) {
        log.info("Sending command {} to {}", params.getCommand(), address);
        HttpEntity<ParticipantRequestParams> req = new HttpEntity<>(params);
        ListenableFuture<ResponseEntity<String>> future = restTemplate.postForEntity(address, req, String.class);
        future.addCallback(callback);
        return future;
    }
}
