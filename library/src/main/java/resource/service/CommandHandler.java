package resource.service;


import coordinator.command.Command;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import resource.model.datasource.ResourceManagerService;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class CommandHandler {

    private final ResourceManagerService resourceManagerService;

    public ResponseEntity<String> handleRequest(Command command){
        return command.safeExecute(resourceManagerService);
    }

}