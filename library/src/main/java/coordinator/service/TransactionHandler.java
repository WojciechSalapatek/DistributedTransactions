package coordinator.service;

import coordinator.model.Participant;
import coordinator.model.ParticipantCommand;
import coordinator.model.ParticipantStatus;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static coordinator.model.ParticipantCommand.*;
import static coordinator.model.ParticipantStatus.*;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TransactionHandler extends Thread {

    @NonNull
    private String id;
    @NonNull
    private int expected_participants;
    @NonNull
    private ParticipantRestService participantService;
    @NonNull
    private ConcurrentMap<Participant, ParticipantStatus> participants;
    @NonNull
    private String initializerId;
    @NonNull
    private String initializerAddress;

    @Value("${coordinator.config.sleeptime}")
    private int sleepTime = 2000;
    @Value("${coordinator.config.timeout}")
    private int timeout = 1000;
    private int slept = 0;

    public static final ResponseEntity<String> ERROR_STATUS =  ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();

    public void run() {
        if (!waitForAllParticipantsToRegister()) return;
        if (!sendStartCommands()) return;
        if (sendCommitCommands())
            sendSuccess();
    }

    public void registerParticipant(Participant participant) {
        participants.put(participant, ParticipantStatus.INIT);
    }

    private boolean participantsOk(){
        return participants.values().stream().noneMatch((v) -> v == ERROR);
    }

    private boolean waitForAllParticipantsToRegister() {
        if (!sleep(() -> participants.size() < expected_participants)) {
            log.error("[transaction {}] Waiting for all participants to register for tansaction failed", id);
            participantService.sendCommand(initializerId, id, initializerAddress, "Waiting for all participants to register failed", ERROR_INITIALIZING,
                    s -> {},
                    th -> {});
            return false;
        }
        log.info("[transaction {}] All participants registered", id);
        return true;
    }

    private boolean sendStartCommands() {
        log.info("[transaction {}] Preparing", id);
        List<ResponseEntity<String>> startedParticipants = sendHelper("START command", START,
                "[transaction {}] Start command for {} failed due to {}", STARTED);

        if (startedParticipants.contains(ERROR_STATUS) || !participantsOk()) {
            timeoutExceeded();
            return false;
        } else {
            participants.keySet().forEach(p -> participants.put(p, WAITING_FOR_COMMIT));
            return true;
        }
    }

    private boolean sendCommitCommands(){
        log.info("[transaction {}] Committing", id);
        List<ResponseEntity<String>> commitedParticipants = sendHelper("COMMIT command", ParticipantCommand.COMMIT,
                "[transaction {}] Commit for {} failed due to {}", COMMITED);

        if (commitedParticipants.contains(ERROR_STATUS) || !participantsOk()) {
            timeoutExceeded();
            return false;
        } else {
            participants.keySet().forEach(p -> participants.put(p, COMMITED));
            return true;
        }
    }

    private boolean rollbackAll() {
        log.warn("[transaction {}] Rollbacking", id);
        List<ResponseEntity<String>> rollbackedParticipants = sendHelper("Rollback command", ParticipantCommand.ROLLBACK,
                "[transaction {}] Rollback for {} failed due to {}", ROLLBACKED);
        return !rollbackedParticipants.contains(ERROR_STATUS);
    }

    private List<ResponseEntity<String>> sendHelper(String message, ParticipantCommand command,
                                                    String errorMessage, ParticipantStatus status) {
        return participants
                .keySet()
                .parallelStream()
                .map(p -> participantService.sendCommand(p.getManagerId(), id, p.getAddress(), message, command,
                        s -> receiveOkStatusForParticipant(p, status),
                        throwable -> {
                            log.error(errorMessage, id, p.getAddress(), throwable.getMessage());
                            participants.put(p, ERROR);
                        }))
                .map(ListenableFuture::completable)
                .map(p -> p.completeOnTimeout(ERROR_STATUS, timeout, TimeUnit.MILLISECONDS))
                .map(CompletableFuture::join)
                .collect(Collectors.toList()); //TODO: consider this
    }

    private void sendSuccess(){
        log.info("[transaction {}] Transaction ended successfully", id);
        participantService.sendCommand(initializerId, id, initializerAddress, "SUCCESS command", SUCCESS,
                s -> {},
                th -> {});
    }

    private boolean sleep(Supplier<Boolean> condition) {
        while (condition.get()) {
            if (slept > timeout) {
                return false;
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            slept += sleepTime;
        }
        return true;
    }

    private void receiveOkStatusForParticipant(Participant participant, ParticipantStatus status) {
        log.debug("[transaction {}] Received ok status from {} - id {}",
                participant.getAddress(), participant.getManagerId(), id);
        participants.put(participant, status);
    }

    private void timeoutExceeded() {
        log.error("[transaction {}] Waiting timeout {} exceeded, participants states: ", id, timeout);
        participants.forEach(
                (k, v) -> log.error("{}: {}", k.getAddress(), v.getStatus())
        );
        if (rollbackAll()) {
            log.info("[transaction {}] Intermediate changes was successfully rollbacked", id);
            participantService.sendCommand(initializerId, id, initializerAddress, "Intermediate changes was successfully rollbacked", ERROR_ROLLBACKED,
                    s -> {},
                    th -> {});
        } else {
            StringBuilder message = new StringBuilder();
            participants.forEach(
                    (k, v) -> message.append(String.format("%s : %s, ", k.getAddress(), v.getStatus()))
            );
            log.error("[transaction {}] Error during rollbacking, state: {}", id ,message.toString());
            participantService.sendCommand(initializerId, id, initializerAddress, message.toString(), ERROR_INCONSISTENT_STATE,
                    s -> {},
                    th -> {});
        }
    }
}