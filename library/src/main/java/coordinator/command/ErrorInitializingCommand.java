package coordinator.command;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import resource.model.datasource.ResourceManagerService;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ErrorInitializingCommand extends Command{

    private String message;

    @Builder
    public ErrorInitializingCommand(String transactionId, String id, String managerId, String message) {
        super(transactionId, id, managerId);
        this.message = message;
    }

    @Override
    protected ResponseEntity<String> execute(ResourceManagerService resourceManagerService) {
        resourceManagerService.unableToFindParticipants(transactionId);
        return ResponseEntity.ok().build();
    }

}
