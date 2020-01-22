package coordinator.service;

import coordinator.command.Command;
import coordinator.command.CommitCommand;
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

    public synchronized ListenableFuture<ResponseEntity<String>> sendCommand(String address, Command command,
                                                                Consumer<ResponseEntity<String>> successCallback,
                                                                Consumer<Throwable> errorCallback) {
        CoordinatorListenableFutureCallback callback = new CoordinatorListenableFutureCallback(errorCallback, successCallback);
        return sendPost(address, command, callback);
    }

    private ListenableFuture<ResponseEntity<String>> sendPost(String address, Command command, ListenableFutureCallback<ResponseEntity<String>> callback) {
        log.debug("Sending command {} to {} with id {}", command.getClass().getSimpleName(), address, command.getManagerId());
        HttpEntity<Command> req = new HttpEntity<>(command);
        ListenableFuture<ResponseEntity<String>> future = restTemplate.postForEntity(address, req, String.class);
        future.addCallback(callback);
        return future;
    }
}
