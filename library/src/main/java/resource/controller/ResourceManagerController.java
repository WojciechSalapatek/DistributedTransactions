package resource.controller;

import coordinator.model.ParticipantRequestParams;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import resource.service.ResourceManagerService;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceManagerController {

    private ResourceManagerService resourceManagerService;

    @GetMapping("/healthcheck")
    public String healthCheck(){
        return "Running";
    }

    @PostMapping("/${resourceHandler.path}")
    public ResponseEntity<String> handleRequest(@RequestBody ParticipantRequestParams participantRequestParams) {
        return resourceManagerService.handleRequest(participantRequestParams.getId(), participantRequestParams.getCommand());
    }


}




