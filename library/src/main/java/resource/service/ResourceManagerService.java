package resource.service;


import coordinator.model.ParticipantCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import resource.model.IDataSourceManager;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ResourceManagerService {

    private List<IDataSourceManager> dataSourceManagers = new ArrayList<>();

    public ResponseEntity<String> handleRequest(String transactionId, String managerId, ParticipantCommand participantCommand) {
        IDataSourceManager dataSourceManager = dataSourceManagers
                .stream()
                .filter(m -> m.getId().equals(managerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No matching resource manager for id: " + managerId));
        switch (participantCommand) {
            case START: {
                return dataSourceManager.beginTransaction();
            }
            case ERROR_INITIALIZING: {
                // TODO
                //handle error when one(or more) of resourcemanagers did not register for transaction
                return ResponseEntity.status(200).build();
            }
            case COMMIT: {
                return dataSourceManager.commit();
            }
            case ROLLBACK: {
                return dataSourceManager.execute();
            }
            case ERROR_ROLLBACKED: {
                // TODO
                //handle situation when error occured on first phase and changes was successfully rollbacked
                return ResponseEntity.status(200).build();
            }
            case ERROR_INCONSISTENT_STATE: {
                // TODO
                //handle situation when error occured on second phase or durig rollbacking, data might be inconsistend
                return ResponseEntity.status(200).build();
            }
            case SUCCESS:
                log.info("Success");
                return ResponseEntity.status(200).build();
            default: {
                return ResponseEntity.status(400).build();
            }
        }
    }

    public void registerManager(IDataSourceManager manager) {
        this.dataSourceManagers.add(manager);
    }
}
