package com.elmachos.distransactions.library.coordinator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class CoordinatorController {

    private CoordinatorService coordinatorService;

    @PostMapping("/createTransaction")
    public String createTransaction(@RequestBody TransactionParams params){
        log.info("Creating transaction");
        return coordinatorService.createTransaction(params);
    }

    @PostMapping("/transaction/{id}/register")
    public void registerParticipant(@RequestParam String id, @RequestBody Participant participant) {
        log.info("Registering participant {} for transaction {}", participant.getAddress(), id);
        coordinatorService.handlers.get(id).registerParticipant(participant);
    }

    @PostMapping("/transaction/{id}/ok")
    public void recieveOkStatusForParticipant(@RequestParam String id, @RequestBody Participant participant) {
        log.info("Recived ok beat from {} for transaction {}", participant.getAddress(), id);
        coordinatorService.handlers.get(id).recieveOkStatusForParticipant(participant);
    }

}
