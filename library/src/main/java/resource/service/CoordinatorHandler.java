package resource.service;


import coordinator.model.ParticipantCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import resource.model.IDataSourceManager;
import resource.model.datasource.ResourceManagerService;
import resource.resourceManagers.IResourceManger;
import resource.transactions.ParticipantParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import static coordinator.model.ParticipantCommand.START;

@Service
public class CoordinatorHandler {

    @Autowired
    public CoordinatorHandler(ResourceManagerService resourceManagerService) {
        this.resourceManagerService = resourceManagerService;
    }

    private final ResourceManagerService resourceManagerService;



    public ResponseEntity<String> handleRequest(String transactionId, String managerId, ParticipantCommand participantCommand) throws Exception {
        switch (participantCommand) {
            case START: {
                return resourceManagerService.beginTransaction(new ParticipantParams(managerId, transactionId));
            }
            case COMMIT: {
                resourceManagerService.commit(new ParticipantParams(managerId, transactionId));
            }
        } return null;
    }


}