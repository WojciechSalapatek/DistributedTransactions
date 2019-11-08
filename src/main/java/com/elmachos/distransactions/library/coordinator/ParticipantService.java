package com.elmachos.distransactions.library.coordinator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ParticipantService {

    private RestTemplate restTemplate;

    public void sendPost(Participant participant, ParticipantCommand command){
        log.info("Sending command {} to {}", command.getCommand(), participant.getAddress());
        restTemplate.postForEntity(
                participant.getAddress(),
                new ParticipantRequestParams(participant.getTransactionId(), command),
                String.class);
    }
}
