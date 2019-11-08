package com.elmachos.distransactions.library.coordinator;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ParticipantService {

    private RestTemplate restTemplate;

    public void sendPost(Participant participant, ParticipantCommand command){
        restTemplate.postForEntity(
                participant.getAddress(),
                new ParticipantRequestParams(participant.getTransactionId(), command),
                String.class);
    }
}
