package resource.controller;

import coordinator.command.Command;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import resource.service.CommandHandler;

@Slf4j
@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceManagerController {

    private CommandHandler commandHandler;

    @GetMapping("/healthcheck")
    public String healthCheck(){
        return "Running";
    }

    @PostMapping("${resourceHandler.path}")
    public ResponseEntity<String> handleRequest(@RequestBody Command command) {
        return commandHandler.handleRequest(command);
    }

}
