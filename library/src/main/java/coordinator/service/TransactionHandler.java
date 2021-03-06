package coordinator.service;

import coordinator.command.Command;
import coordinator.command.CommandBuilder;
import coordinator.model.Participant;
import coordinator.model.ParticipantStatus;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static coordinator.model.ParticipantStatus.*;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TransactionHandler extends Thread {

    final private String id;
    final private int expectedParticipants;
    final private ParticipantRestService participantService;
    final private ConcurrentMap<Participant, ParticipantStatus> participants;
    final private String initializerId;
    final private String initializerAddress;

    @Value("${coordinator.config.sleeptime}")
    private int sleepTime = 2000;
    @Value("${coordinator.config.timeout}")
    private int timeout = 5000;
    private int slept = 0;

    public static final ResponseEntity<String> ERROR_STATUS = ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();

    public void run() {
        if (!waitForAllParticipantsToRegister()) return;
        try {
            if (!sendStartCommands()) return;
            if (sendCommitCommands())
                sendSuccess();
        } catch (Exception e) {
            handleError();
        }
    }

    public void registerParticipant(Participant participant) {
        participants.put(participant, ParticipantStatus.INIT);
    }

    private boolean participantsOk() {
        return participants.values().stream().noneMatch((v) -> v == ERROR);
    }

    private boolean waitForAllParticipantsToRegister() {
        if (!sleep(() -> participants.size() < expectedParticipants)) {
            log.error("[transaction {}] Waiting for all participants to register for tansaction failed", id);
            Command errorCommand = CommandBuilder.getErrorInitializingCommand(id, initializerId);
            participantService.sendCommand(initializerAddress, errorCommand,
                    s -> {},
                    th -> {}
                    );
            return false;
        }
        log.info("[transaction {}] All participants registered", id);
        return true;
    }

    private boolean sendStartCommands() {
        log.info("[transaction {}] Preparing", id);
        List<ResponseEntity<String>> startedParticipants = sendHelper(CommandBuilder.getStartCommand(id),
                "[transaction {}] Start command for {} failed due to {}", STARTED);

        return checkParticipants(startedParticipants, WAITING_FOR_COMMIT);
    }

    private boolean sendCommitCommands() {
        log.info("[transaction {}] Committing", id);
        List<ResponseEntity<String>> commitedParticipants = sendHelper(CommandBuilder.getCommitCommand(id, initializerId),
                "[transaction {}] Commit for {} failed due to {}", COMMITED);

        return checkParticipants(commitedParticipants, COMMITED);
    }

    private boolean checkParticipants(List<ResponseEntity<String>> startedParticipants, ParticipantStatus waitingForCommit) {
        if (startedParticipants.contains(ERROR_STATUS) || !participantsOk()) {
            timeoutExceeded();
            return false;
        } else {
            participants.keySet().forEach(p -> participants.put(p, waitingForCommit));
            return true;
        }
    }

    private boolean rollbackAll() {
        log.warn("[transaction {}] Rollbacking", id);
        List<ResponseEntity<String>> rollbackedParticipants = sendHelper(CommandBuilder.getRollbackCommand(id),
                "[transaction {}] Rollback for {} failed due to {}", ROLLBACKED);
        return !rollbackedParticipants.contains(ERROR_STATUS);
    }

    private List<ResponseEntity<String>> sendHelper(Command command, String errorMessage,
                                                    ParticipantStatus status) {
        return participants
                .keySet()
                .stream()
                .map(p -> participantService.sendCommand(p.getAddress(), command.changeManagerId(p.getManagerId()),
                        s -> receiveOkStatusForParticipant(p, status),
                        throwable -> {
                            log.error(errorMessage, id, p.getAddress(), throwable.getMessage());
                            participants.put(p, ERROR);
                        }))
                .map(ListenableFuture::completable)
                .map(p -> p.completeOnTimeout(ERROR_STATUS, timeout, TimeUnit.MILLISECONDS))
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    private void sendSuccess() {
        log.info("[transaction {}] Transaction ended successfully", id);
        participantService.sendCommand(initializerAddress, CommandBuilder.getSuccessCommand(id, initializerId),
                s -> {},
                th -> {}
                );
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
        participants.forEach((k, v) -> log.error("{}: {}", k.getAddress(), v.getStatus()));

        handleError();
    }

    private void handleError() {
        try {
            if (rollbackAll()) {
                log.info("[transaction {}] Intermediate changes was successfully rollbacked", id);
                Command errorCommand = CommandBuilder.getErrorRollbackedCommand(id, initializerId);
                participantService.sendCommand(initializerAddress, errorCommand,
                        s -> {},
                        th -> {}
                        );
            } else throw new RuntimeException();
        } catch (Exception e){
            StringBuilder message = new StringBuilder();
            participants.forEach((k, v) -> message.append(String.format("%s : %s, ", k.getAddress(), v.getStatus())));
            log.error("[transaction {}] Error during rollbacking, state: {}", id, message.toString());
            Command errorCommand = CommandBuilder.getErrorInconsistentStateCommand(id, initializerId, message.toString());
            participantService.sendCommand(initializerAddress, errorCommand,
                    s -> {},
                    th -> {}
                    );
        }
    }
}