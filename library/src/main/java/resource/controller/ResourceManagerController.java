package resource.controller;

import coordinator.model.ParticipantRequestParams;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import resource.service.CoordinatorHandler;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceManagerController {

    private CoordinatorHandler coordinatorHandler;

    @GetMapping("/healthcheck")
    public String healthCheck(){
        return "Running";
    }

    @PostMapping("/manager/rm1")
    public ResponseEntity<String> handleRequest(@RequestBody ParticipantRequestParams participantRequestParams) throws Exception {
        return coordinatorHandler.handleRequest(participantRequestParams.getId(), participantRequestParams.getManagerId(), participantRequestParams.getCommand());
    }

}
