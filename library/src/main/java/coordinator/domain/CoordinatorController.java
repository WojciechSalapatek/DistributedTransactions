package coordinator.domain;

import coordinator.model.Participant;
import coordinator.model.TransactionParams;
import coordinator.service.CoordinatorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CoordinatorController {

    private CoordinatorService coordinatorService;

    @GetMapping("/coordinator/healthcheck")
    public String healthCheck(){
        return "Running";
    }

    @PostMapping("/coordinator/createTransaction")
    public String createTransaction(@RequestBody TransactionParams params) {
        log.info("Creating transaction");
        return coordinatorService.createTransaction(params);
    }

    @PostMapping("/coordinator/transaction/{id}/register")
    public void registerParticipant(@PathVariable String id, @RequestBody Participant participant) {
        log.debug("[transaction {}] Registering participant {} with id {}",
                id, participant.getManagerId(),id);
        coordinatorService.getHandlers().get(id).registerParticipant(participant);
    }

}
