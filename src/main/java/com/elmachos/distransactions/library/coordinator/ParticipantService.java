package com.elmachos.distransactions.library.coordinator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;

@Slf4j
@Service
public class ParticipantService {

    private RestTemplate restTemplate;

    public HttpStatus sendPost(Participant participant, ParticipantCommand command){
        return sendPost(participant, command, "");
    }

    public HttpStatus sendPost(Participant participant, ParticipantCommand command, String message){
        log.info("Sending command {} to {}", command.getCommand(), participant.getAddress());
        return restTemplate.postForEntity(
                participant.getAddress(),
                new ParticipantRequestParams(participant.getTransactionId(), command, message),
                String.class).getStatusCode();
    }
}
