package resource.service;


import coordinator.model.ParticipantCommand;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import resource.model.IDataSourceHandler;

@Slf4j
@Setter
@Service
public class ResourceManagerService {

    private IDataSourceHandler dataSourceHandler;

    public ResponseEntity<String> handleRequest(String id, ParticipantCommand participantCommand) {
        switch (participantCommand) {
            case START: {
                return dataSourceHandler.beginTransaction();
            }
            case COMMIT: {
                return dataSourceHandler.commit();
            }
            case ROLLBACK: {
                return dataSourceHandler.execute();
            }
            case SUCCESS:
                log.info("Success");
                return ResponseEntity.status(200).build();
            default: {
                return ResponseEntity.status(400).build();
            }
        }
    }
}
