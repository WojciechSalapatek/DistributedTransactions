package coordinator.command;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import resource.model.datasource.ResourceManagerService;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ErrorInconsistentStateCommand extends Command{

    private String message;

    @Builder
    public ErrorInconsistentStateCommand(String transactionId, String id, String managerId, String message) {
        super(transactionId, id, managerId);
        this.message = message;
    }

    @Override
    protected ResponseEntity<String> execute(ResourceManagerService resourceManagerService) {
        return ResponseEntity.ok().build();
    }

}
