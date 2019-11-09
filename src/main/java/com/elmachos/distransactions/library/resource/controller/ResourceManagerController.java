package com.elmachos.distransactions.library.resource.controller;

import com.elmachos.distransactions.library.coordinator.model.ParticipantRequestParams;
import com.elmachos.distransactions.library.resource.service.ResourceManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/manager")
public class ResourceManagerController {

    private ResourceManagerService resourceManagerService;

    @PostMapping("/${resourceHandler.id}" )
    private ResponseEntity handleRequest(ParticipantRequestParams participantRequestParams) {
        return resourceManagerService.handleRequest(participantRequestParams.getId(), participantRequestParams.getCommand());
    }


}




