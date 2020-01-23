package coordinator.command;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import resource.model.datasource.ResourceManagerService;
import resource.transactions.ParticipantParams;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ErrorRollbackedCommand extends Command{

    private String message;

    @Builder
    public ErrorRollbackedCommand(String transactionId, String id, String managerId, String message) {
        super(transactionId, id, managerId);
        this.message = message;
    }

    @Override
    protected ResponseEntity<String> execute(ResourceManagerService resourceManagerService){
        resourceManagerService.transactionRollbacked(transactionId);
        return ResponseEntity.ok().build();
    }

}
