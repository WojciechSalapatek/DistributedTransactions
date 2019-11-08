package com.elmachos.distransactions.library.coordinator;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CoordinatorService {

    private static long id = 0;
    private ParticipantService participantService;
    @Getter
    Map<String, TransactionHandler> handlers = new HashMap<>();

    public synchronized static String getNextId(){
        return Long.toString(id++);
    }

    public String createTransaction(TransactionParams params){
        String transactionId = getNextId();
        TransactionHandler handler = new TransactionHandler(params.getParticipants(), participantService, new HashMap<>(), params.getMaster());
        log.debug("Created handler [participants: {}] for {}", params.getParticipants(), transactionId);
        handlers.put(transactionId, handler);
        return transactionId;
    }

}
