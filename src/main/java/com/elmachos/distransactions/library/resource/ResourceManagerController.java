package com.elmachos.distransactions.library.resource;

import com.elmachos.distransactions.library.coordinator.ParticipantRequestParams;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/manager")
public class ResourceManagerController {

    private ResourceManagerService resourceManagerService;

    @PostMapping("/${resourceHandler.id}" )
    private String handleRequest(ParticipantRequestParams participantRequestParams) {
        return resourceManagerService.handleRequest(participantRequestParams.getId(), participantRequestParams.getCommand());
    }


}




