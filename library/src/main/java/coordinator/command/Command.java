package coordinator.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import resource.model.datasource.ResourceManagerService;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StartCommand.class, name = "start"),
        @JsonSubTypes.Type(value = CommitCommand.class, name = "commit"),
        @JsonSubTypes.Type(value = SuccessCommand.class, name = "success"),
        @JsonSubTypes.Type(value = RollbackCommand.class, name = "rollback"),
        @JsonSubTypes.Type(value = ErrorRollbackedCommand.class, name = "errorRlb"),
        @JsonSubTypes.Type(value = ErrorInitializingCommand.class, name = "errorInit"),
        @JsonSubTypes.Type(value = ErrorInconsistentStateCommand.class, name = "errorInc"),
})
public abstract class Command implements Serializable {

    protected String transactionId;
    protected String id;
    protected String managerId;

    public ResponseEntity<String> safeExecute(ResourceManagerService resourceManagerService){
        try {
            return execute(resourceManagerService);
        } catch (Exception e) {
            log.error("", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Command changeManagerId(String managerId) {
        setManagerId(managerId);
        return this;
    }

    protected abstract ResponseEntity<String> execute(ResourceManagerService resourceManagerService) throws Exception;
}
