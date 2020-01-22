package coordinator.command;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import resource.model.datasource.ResourceManagerService;
import resource.transactions.ParticipantParams;

@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommitCommand extends Command{

    private String message;

    @Builder
    public CommitCommand(String transactionId, String id, String managerId, String message) {
        super(transactionId, id, managerId);
        this.message = message;
    }

    @Override
    protected ResponseEntity<String> execute(ResourceManagerService resourceManagerService) throws Exception{
        return resourceManagerService.commit(new ParticipantParams(managerId, transactionId));
    }

}
