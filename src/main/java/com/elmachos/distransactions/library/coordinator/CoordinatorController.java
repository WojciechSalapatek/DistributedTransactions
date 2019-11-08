package com.elmachos.distransactions.library.coordinator;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/coordinator")
public class CoordinatorController {

    private CoordinatorService coordinatorService;

    @PostMapping("/createTransaction")
    String createTransaction(@RequestBody TransactionParams params){
        return coordinatorService.createTransaction(params);
    }

    @PostMapping("/transaction/{id}/register")
    public void registerParticipant(@RequestParam String id, @RequestBody Participant participant) {
        coordinatorService.handlers.get(id).registerParticipant(participant);
    }

    @PostMapping("/transaction/{id}/ok")
    public void recieveOkStatusForParticipant(@RequestParam String id, @RequestBody Participant participant) {
        coordinatorService.handlers.get(id).recieveOkStatusForParticipant(participant);
    }

}
