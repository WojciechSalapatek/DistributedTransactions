package coordinator.service;

import coordinator.model.ParticipantCommand;
import coordinator.model.ParticipantStatus;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

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
    private ConcurrentMap<String, ParticipantStatus> participants;
    @NonNull
    private String initializerId;

    @Value("${resourceHandler.id}")
    private String address = "http://localhost:8081/manager/rm1";
    @Value("${coordinator.config.sleeptime}")
    private int sleepTime = 2000;
    @Value("${coordinator.config.timeout}")
    private int timeout = 1000;
    private int slept = 0;

    public void run() {
        if (!waitForAllParticipantsToRegister()) return;
        if (!sendStartCommands()) return;
        if (sendCommitCommands())
            sendSuccess();
    }

    public void registerParticipant(String participantId) {
        participants.put(participantId, ParticipantStatus.INIT);
    }

    private boolean waitForAllParticipantsToRegister() {
        if (!sleep(() -> participants.size() < expected_participants)) {
            log.error("Waiting for all participants to register for tansaction {} failed", id);
            participantService.sendCommand(initializerId, id, address, "Waiting for all participants to register failed", ERROR_INITIALIZING,
                    s -> {},
                    th -> {});
            return false;
        }
        return true;
    }

    private boolean sendStartCommands() {
        List<Object> startedParticipants = sendHelper("START command", START, "Start command for {} failed due to {}");

        if (startedParticipants.contains(HttpStatus.REQUEST_TIMEOUT)) {
            timeoutExceeded();
            return false;
        } else {
            participants.keySet().forEach(p -> participants.put(p, WAITING_FOR_COMMIT));
            return true;
        }
    }

    private boolean sendCommitCommands(){
        List<Object> commitedParticipants = sendHelper("COMMIT command", ParticipantCommand.COMMIT, "Sending commit for {} failed due to {}");

        if (commitedParticipants.contains(HttpStatus.REQUEST_TIMEOUT)) {
            timeoutExceeded();
            return false;
        } else {
            participants.keySet().forEach(p -> participants.put(p, COMMITED));
            return true;
        }
    }

    private boolean rollbackAll() {
        log.info("Rollbacking");
        List<Object> rollbackedParticipants = sendHelper("COMMIT command", ParticipantCommand.COMMIT, "Sending commit for {} failed due to {}");
        return !rollbackedParticipants.contains(HttpStatus.REQUEST_TIMEOUT);
    }

    private List<Object> sendHelper(String s2, ParticipantCommand start, String s3) {
        return participants
                .keySet()
                .parallelStream()
                .map(p -> participantService.sendCommand(p, id, address, s2, start,
                        s -> receiveOkStatusForParticipant(p),
                        throwable -> {
                            log.error(s3, address, throwable.getMessage());
                            participants.put(p, ERROR);
                        }))
                .map(p -> p.completable())
                .map(p -> p.completeOnTimeout(HttpStatus.REQUEST_TIMEOUT, timeout, TimeUnit.MILLISECONDS))
                .map(p -> p.join())
                .collect(Collectors.toList()); //TODO: consider this
    }

    private void sendSuccess(){
        log.info("Transaction {} ended successfully", id);
        participantService.sendCommand(initializerId, id, address, "SUCCESS command", SUCCESS,
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

    private void receiveOkStatusForParticipant(String resourceManagerId) {
        log.info("Received ok status from {}", address);
        participants.put(resourceManagerId, WAITING_FOR_COMMIT);
    }

    private void timeoutExceeded() {
        log.error("Waiting timeout {} exceeded, participants states: ", timeout);
        participants.forEach(
                (k, v) -> log.error("{}: {}", address, v.getStatus())
        );
        rollbackAll();
        if (rollbackAll()) {
            log.info("Intermediate changes was successfully rollbacked");
            participantService.sendCommand(initializerId, id, address, "Intermediate changes was successfully rollbacked", ERROR_ROLLBACKED,
                    s -> {},
                    th -> {});
        } else {
            StringBuilder message = new StringBuilder();
            participants.forEach(
                    (k, v) -> message.append(String.format("%s : %s, ", address, v.getStatus()))
            );
            log.error("Error during rollbacking, state: {}", message.toString());
            participantService.sendCommand(initializerId, id, address, message.toString(), ERROR_INCONSISTENT_STATE,
                    s -> {},
                    th -> {});
        }
    }
}