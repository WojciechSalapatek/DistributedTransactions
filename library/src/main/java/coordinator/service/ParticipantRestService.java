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

    public void sendCommand(Participant participant, String message, ParticipantCommand command,
                            Consumer<ResponseEntity<String>> successCallback,
                            Consumer<Throwable> errorCallback) {
        ListenableFutureCallback<ResponseEntity<String>> callback = new ListenableFutureCallback<>() {

            @Override
            public void onSuccess(ResponseEntity<String> result) {
                successCallback.accept(result);
            }

            @Override
            public void onFailure(Throwable ex) {
                errorCallback.accept(ex);
            }
        };
        ParticipantRequestParams params = new ParticipantRequestParams(participant.getTransactionId(), participant.getManagerId(), command, message);
        try {
            HttpStatus status = sendPost(participant.getAddress(), params, callback);
            if (!status.is2xxSuccessful())
                throw new RuntimeException("Sending " + command + " command unsuccessful, get status code: " + status.value());
        } catch (Throwable e) {
            errorCallback.accept(e);
        }
    }

    private HttpStatus sendPost(String address, ParticipantRequestParams params, ListenableFutureCallback<ResponseEntity<String>> callback) throws ExecutionException, InterruptedException {
        log.info("Sending command {} to {}", params.getCommand(), address);
        HttpEntity<ParticipantRequestParams> req = new HttpEntity<>(params);
        ListenableFuture<ResponseEntity<String>> future = restTemplate.postForEntity(address, req, String.class);
        future.addCallback(callback);
        return future.get().getStatusCode();
    }
}
