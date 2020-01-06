package resource.model.datasource;

import coordinator.model.Participant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import resource.model.IDataSourceManager;
import resource.resourceManagers.IResourceManger;
import resource.transactions.ParticipantParams;
import resource.transactions.TransactionParam;

import java.util.HashMap;
import java.util.function.Function;

@Slf4j
@Component
@NoArgsConstructor
public class ResourceManagerService implements IDataSourceManager {


    @Getter
    @Value("${resourceHandler.coordinatorEndpointAddress}")
    private String coordinatorEndpointAddress;

    private final HashMap<String, IResourceManger> resourceMangers = new HashMap<>();
    private final RestTemplate coordinatorEndpoint = new RestTemplate();

    private static final String createTransactionSuffix = "/createTransaction";
    private static final Function<String, String> createRegisterSuffix = (id) -> "/transaction/" + id + "/register";

    public void addResourceManager(IResourceManger resourceManger){
        resourceMangers.put(resourceManger.getId(), resourceManger);
    }

    @Override
    public String initiateTransaction(String resourceManagerId, int participants) {
        log.info("Initializing transaction");
        HttpEntity<TransactionParam> req = new HttpEntity<>(new TransactionParam(resourceManagerId, participants));
        return coordinatorEndpoint.postForEntity(coordinatorEndpointAddress + createTransactionSuffix, req, String.class).getBody();
    }

    @Override
    public void registerForTransaction(String resourceManagerId, String transactionId) {
        log.info("Registering for transaction {}", transactionId);
        Participant rmModel = new Participant(resourceManagerId, transactionId);
        HttpEntity<Participant> req = new HttpEntity<>(rmModel);
        coordinatorEndpoint.postForEntity(coordinatorEndpointAddress + createRegisterSuffix.apply(transactionId),
                req, String.class);
    }


    public IResourceManger getResourceManager(String resourceManagerId){
        return resourceMangers.get(resourceManagerId);
    }

    //TODO: handle error
    @Override
    public ResponseEntity<String> beginTransaction(ParticipantParams participantParams) throws Exception {
        IResourceManger resourceManager = resourceMangers.get(participantParams.getParticipantId());
        resourceManager.checkDataSource();
        resourceManager.commit();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<String> commit(ParticipantParams participantParams) throws Exception {
        IResourceManger resourceManager = resourceMangers.get(participantParams.getParticipantId());
        resourceManager.execute();
        return new ResponseEntity<String>(HttpStatus.ACCEPTED);
    }



}
