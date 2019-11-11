package coordinator.service;

import coordinator.model.Participant;
import coordinator.model.ParticipantCommand;
import coordinator.model.ParticipantStatus;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import static coordinator.model.ParticipantCommand.*;
import static coordinator.model.ParticipantStatus.*;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TransactionHandler extends Thread {

    @NonNull
    private int expected_participants;
    @NonNull
    private ParticipantRestService participantService;
    @NonNull
    private ConcurrentMap<Participant, ParticipantStatus> participants;
    @NonNull
    private Participant master;


    @Value("${coordinator.config.sleeptime}")
    private int sleepTime = 200;
    @Value("${coordinator.config.timeout}")
    private int timeout = 10000;
    private int slept = 0;

    public void run() {
        if (!waitForAllParticipantsToRegister()) return;
        if (!sendStartCommands()) return;
        if(sendCommitCommands())
            sendSuccess();
    }

    public void registerParticipant(Participant participant) {
        participants.put(participant, ParticipantStatus.INIT);
    }

    private boolean waitForAllParticipantsToRegister(){
        if (!sleep(() -> participants.size() < expected_participants)) {
            log.error("Waiting for all participants to register for tansaction {} failed", master.getTransactionId());
            participantService.sendCommand(master, "Waiting for all participants to register failed", ERROR_INITIALIZING,
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
                    participantService.sendCommand(p, "START command", START,
                            s -> receiveOkStatusForParticipant(p),
                            throwable -> {
                                log.error("Start command for {} failed due to {}", p.getAddress(), throwable.getMessage());
                                participants.put(p, ERROR);
                            });
                });

        if (!sleep(() -> !participants.values().stream().allMatch(v -> v == WAITING_FOR_COMMIT))) {
            timeoutExceeded();
            return false;
        }
        return true;
    }

    private boolean sendCommitCommands(){
        participants.keySet().forEach(
                (p) -> {
                    participantService.sendCommand(p, "COMMIT command", ParticipantCommand.COMMIT,
                            s -> participants.put(p, ParticipantStatus.COMMITED),
                            throwable -> {
                                log.error("Sending commit for {} failed due to {}", p.getAddress(), throwable.getMessage());
                                participants.put(p, ERROR);
                            }
                    );
                });

        if (!sleep(() -> !participants.values().stream().allMatch(v -> v == COMMITED))) {
            timeoutExceeded();
            return false;
        }
        return true;
    }

    private boolean rollbackAll() {
        log.info("Rollbacking");
        participants.keySet()
                .forEach((p) ->
                        participantService.sendCommand(p, "ROLLBACK comand", ParticipantCommand.ROLLBACK,
                                s -> participants.put(p, ROLLBACKED),
                                throwable -> {
                                    log.error("Rollbacking for {} failed due to {}", p.getAddress(), throwable.getMessage());
                                    participants.put(p, ERROR);
                                }));
        return sleep(() -> !participants.values().stream().allMatch(v -> v == ROLLBACKED));
    }

    private void sendSuccess(){
        log.info("Transaction {} ended successfully", participants.keySet().stream().findFirst().get().getTransactionId());
        participantService.sendCommand(master, "SUCCESS command", SUCCESS,
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

    private void receiveOkStatusForParticipant(Participant participant) {
        log.info("Received ok status from {} for transaction {}", participant.getAddress(), participant.getTransactionId());
        participants.put(participant, WAITING_FOR_COMMIT);
    }

    private void timeoutExceeded() {
        log.error("Waiting timeout {} exceeded, participants states: ", timeout);
        participants.forEach(
                (k, v) -> log.error("{}: {}", k.getAddress(), v.getStatus())
        );
        rollbackAll();
        if (rollbackAll()) {
            log.info("Intermediate changes was successfully rollbacked");
            participantService.sendCommand(master, "Intermediate changes was successfully rollbacked", ERROR_ROLLBACKED,
                    s -> {},
                    th -> {});
        } else {
            StringBuilder message = new StringBuilder();
            participants.forEach(
                    (k, v) -> message.append(String.format("%s : %s, ", k.getAddress(), v.getStatus()))
            );
            log.error("Error during rollbacking, state: {}", message.toString());
            participantService.sendCommand(master, message.toString(), ERROR_INCONSISTENT_STATE,
                    s -> {},
                    th -> {});
        }
    }


}
