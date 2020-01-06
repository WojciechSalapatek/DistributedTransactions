package coordinator.service;

import coordinator.model.ParticipantCommand;
import coordinator.model.ParticipantStatus;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

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
    private int timeout = 10000;
    private int slept = 0;

    public void run() {
        if (!waitForAllParticipantsToRegister()) return;
        if (!sendStartCommands()) return;
        if(sendCommitCommands())
            sendSuccess();
    }

    public void registerParticipant(String participantId) {
        participants.put(participantId, ParticipantStatus.INIT);
    }

    private boolean waitForAllParticipantsToRegister(){
        if (!sleep(() -> participants.size() < expected_participants)) {
            log.error("Waiting for all participants to register for tansaction {} failed", id);
            participantService.sendCommand(initializerId, id, address, "Waiting for all participants to register failed", ERROR_INITIALIZING,
                    s -> {},
                    th -> {});
            return false;
        }
        return true;
    }

    private boolean sendStartCommands(){
        participants.keySet().forEach(
                (p) -> {
                    participants.put(p, ParticipantStatus.STARTED);
                    participantService.sendCommand(p, id, address, "START command", START,
                            s -> receiveOkStatusForParticipant(p),
                            throwable -> {
                                log.error("Start command for {} failed due to {}", address, throwable.getMessage());
                                participants.put(p, ERROR);
                            });
                });

        boolean startUnsuccessful = !sleep(
                () -> !areParticipants(WAITING_FOR_COMMIT)
        );

        if (startUnsuccessful) {
            timeoutExceeded();
            return false;
        }
        return true;
    }

    private boolean sendCommitCommands(){
        participants.keySet().forEach(
                (p) -> {
                    participantService.sendCommand(p, id, address, "COMMIT command", ParticipantCommand.COMMIT,
                            s -> participants.put(p, ParticipantStatus.COMMITED),
                            throwable -> {
                                log.error("Sending commit for {} failed due to {}", address, throwable.getMessage());
                                participants.put(p, ERROR);
                            }
                    );
                });
        boolean commitUnsuccessful = !sleep(
                () -> !areParticipants(COMMITED)
        );
        if (commitUnsuccessful) {
            timeoutExceeded();
            return false;
        }
        return true;
    }

    private boolean areParticipants(ParticipantStatus participantStatus) {
        return participants
                .values()
                .stream()
                .allMatch(v -> v == participantStatus);
    }

    private boolean rollbackAll() {
        log.info("Rollbacking");
        participants.keySet()
                .forEach((p) ->
                        participantService.sendCommand(p, id, address, "ROLLBACK comand", ParticipantCommand.ROLLBACK,
                                s -> participants.put(p, ROLLBACKED),
                                throwable -> {
                                    log.error("Rollbacking for {} failed due to {}", address, throwable.getMessage());
                                    participants.put(p, ERROR);
                                }));
        return sleep(() -> !areParticipants(ROLLBACKED));
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
