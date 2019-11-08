package com.elmachos.distransactions.library.resource;

import com.elmachos.distransactions.library.coordinator.ParticipantCommand;
import com.elmachos.distransactions.library.coordinator.ParticipantRequestParams;

import static com.elmachos.distransactions.library.coordinator.ParticipantCommand.*;

public class ResourceManagerService {



    public String handleRequest(String id, ParticipantCommand participantCommand) {
        switch (participantCommand) {
            case START: {
                // sprawdzic czy baza ok
            }
            case COMMIT: {
                // connectionAndCommit
                // wylaczyc autocommit
            }
            case ROLLBACK: {
                //rollback
            }
            default: {
            }
        }
        return "";
    }
}
