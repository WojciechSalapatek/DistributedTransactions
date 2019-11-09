package com.elmachos.distransactions.library.resource.service;


import com.elmachos.distransactions.library.coordinator.model.ParticipantCommand;
import com.elmachos.distransactions.library.resource.model.IDataSourceHandler;
import org.springframework.http.ResponseEntity;

public class ResourceManagerService {

    private IDataSourceHandler dataSourceHandler;

    public ResponseEntity handleRequest(String id, ParticipantCommand participantCommand) {
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
            default: {
                return ResponseEntity.status(400).build();
            }
        }
    }
}
