package resource.service;


import coordinator.model.ParticipantCommand;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import resource.model.datasource.ResourceManagerService;
import resource.transactions.ParticipantParams;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CoordinatorHandler {

    private final ResourceManagerService resourceManagerService;

    public ResponseEntity<String> handleRequest(String transactionId, String managerId, ParticipantCommand participantCommand) throws Exception {
        switch (participantCommand) {
            case START: {
                return resourceManagerService.beginTransaction(new ParticipantParams(managerId, transactionId));
            }
            case COMMIT: {
                return resourceManagerService.commit(new ParticipantParams(managerId, transactionId));
            }
            case ROLLBACK: {
                return resourceManagerService.rollback(new ParticipantParams(managerId, transactionId));
            }
        }
        return null;
    }


}