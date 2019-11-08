package com.elmachos.distransactions.library.coordinator;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TransactionHandler extends Thread {

    @NonNull
    private int expected_participants;
    @NonNull
    private ParticipantService participantService;
    @NonNull
    private Map<Participant, ParticipantStatus> participants;
    @NonNull
    private Participant master;


    @Value("${coordinator.config.sleeptime}")
    private int sleepTime;
    @Value("${coordinator.config.timeout}")
    private int timeout;
    private int slept = 0;

    public void run() {
        sleep(() -> expected_participants < participants.size());

        participants.keySet().forEach(
                (p) -> {
                    participantService.sendPost(p, ParticipantCommand.START);
                    participants.put(p, ParticipantStatus.STARTED);
                });

        sleep(() -> participants.values().stream().allMatch(v -> v == ParticipantStatus.WAITING_FOR_COMMIT));

        participants.keySet().forEach(
                (p) -> {
                    participantService.sendPost(p, ParticipantCommand.COMMIT);
                    participants.put(p, ParticipantStatus.STARTED);
                });
    }

    public void registerParticipant(Participant participant) {
        participants.put(participant, ParticipantStatus.INIT);
    }

    public void recieveOkStatusForParticipant(Participant participant) {
        participants.put(participant, ParticipantStatus.WAITING_FOR_COMMIT);
    }

    public boolean rollbackAll() {
        return participants.keySet()
                .stream()
                .map((p) -> {
                    if (participantService.sendPost(p, ParticipantCommand.ROLLBACK) == HttpStatus.OK)
                        participants.put(p, ParticipantStatus.ROLLBACK);
                    else return false;
                    return true;

                })
                .allMatch(e -> e);
    }

    private void sleep(Supplier<Boolean> condition) {
        while (condition.get()) {
            if (slept > timeout) {
                timeoutExceeded();
            }
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            slept += sleepTime;
        }
    }

    private void timeoutExceeded() {
        log.error("Waiting timeout {} exceeded, participants states: ", timeout);
        participants.forEach(
                (k, v) -> log.error("{}: {}", k.getAddress(), v.getStatus())
        );

        if (rollbackAll()) {
            log.info("Intermediate changes was successfully rollbacked");
            participantService.sendPost(master, ParticipantCommand.ERROR_ROLLBACKED);
        }
        else {
            StringBuilder message = new StringBuilder();
            participants.forEach(
                    (k,v) -> message.append(String.format("%s : %s, ", k.getAddress(), v.getStatus()))
            );
            log.error("Error during rollbacking, state: {}", message.toString());
            participantService.sendPost(master, ParticipantCommand.ERROR_INCONSISTENT_STATE, message.toString());
        }
    }


}
