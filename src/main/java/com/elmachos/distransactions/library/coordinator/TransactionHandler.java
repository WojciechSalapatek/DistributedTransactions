package com.elmachos.distransactions.library.coordinator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TransactionHandler extends Thread {

    private int expected_participants;
    private ParticipantService participantService;
    private Map<Participant, ParticipantStatus> participants;


    private static final int SLEEP_TIME = 200;

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

    private void sleep(Supplier<Boolean> condition) {
        while (condition.get()) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerParticipant(Participant participant) {
        participants.put(participant, ParticipantStatus.INIT);
    }

    public void recieveOkStatusForParticipant(Participant participant) {
        participants.put(participant, ParticipantStatus.WAITING_FOR_COMMIT);
    }
}
