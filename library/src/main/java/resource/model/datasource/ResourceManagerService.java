package resource.model.datasource;

import coordinator.model.Participant;
import coordinator.model.TransactionParams;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import resource.model.ErrorCallback;
import resource.model.IDataSourceManager;
import resource.resourceManagers.IResourceManger;
import resource.transactions.ParticipantParams;
import resource.transactions.TransactionStatus;

import java.util.*;
import java.util.function.Function;

import static java.util.Objects.isNull;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ResourceManagerService implements IDataSourceManager {

    @Getter
    @Value("${resourceHandler.coordinatorEndpointAddress}")
    private String coordinatorEndpointAddress;

    @Value("${resourceHandler.address}")
    private String address;
    private ErrorCallback initErrorCallback = null;
    private ErrorCallback errorRollbackedCallback = null;
    private ErrorCallback errorInconsistentCallback = null;


    private final Map<String, IResourceManger> resourceMangers = new HashMap<>();
    private final Map<String, TransactionStatus> transactionStatus = new LinkedHashMap<>();
    private final RestTemplate coordinatorEndpoint;

    private static final String createTransactionSuffix = "/createTransaction";
    private static final Function<String, String> createRegisterSuffix = (id) -> "/transaction/" + id + "/register";

    public void addResourceManager(IResourceManger resourceManger){
        resourceMangers.put(resourceManger.getId(), resourceManger);
    }

    @Override
    public String initiateTransaction(String resourceManagerId, int participants) {
        log.info("Initializing transaction");
        HttpEntity<TransactionParams> req = new HttpEntity<>(
                new TransactionParams(new Participant(resourceManagerId, address, null), participants));
        return coordinatorEndpoint.postForEntity(coordinatorEndpointAddress + createTransactionSuffix, req, String.class).getBody();
    }

    @Override
    public void registerForTransaction(String resourceManagerId, String transactionId) {
        log.info("Registering for transaction {}", transactionId);
        transactionStatus.put(transactionId, TransactionStatus.REGISTERED);
        Participant rmModel = new Participant(resourceManagerId,address, transactionId);
        HttpEntity<Participant> req = new HttpEntity<>(rmModel);
        coordinatorEndpoint.postForEntity(coordinatorEndpointAddress + createRegisterSuffix.apply(transactionId),
                req, String.class);
    }

    public IResourceManger getResourceManager(String resourceManagerId) {
        return resourceMangers.get(resourceManagerId);
    }

    @Override
    public ResponseEntity<String> beginTransaction(ParticipantParams participantParams) throws Exception {
        log.debug("Beginning for {}", participantParams.getParticipantId());
        IResourceManger resourceManager = resourceMangers.get(participantParams.getParticipantId());
        resourceManager.checkDataSource();
        resourceManager.execute();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(participantParams.getParticipantId());
    }

    @Override
    public ResponseEntity<String> commit(ParticipantParams participantParams) throws Exception {
        log.info("Commiting transaction {} for {}", participantParams.getTransactionId(), participantParams.getParticipantId());
        transactionStatus.put(participantParams.getTransactionId(), TransactionStatus.COMMITED);
        IResourceManger resourceManager = resourceMangers.get(participantParams.getParticipantId());
        resourceManager.commit();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(participantParams.getParticipantId());
    }

    @Override
    public ResponseEntity<String> rollback(ParticipantParams participantParams) throws Exception {
        log.info("Rollbacking transaction {} for {}", participantParams.getTransactionId(), participantParams.getParticipantId());
        IResourceManger resourceManager = resourceMangers.get(participantParams.getParticipantId());
        resourceManager.rollback();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(participantParams.getParticipantId());
    }

    @Override
    public void unexpectedTransactionError(String transactionId) {
        transactionStatus.put(transactionId, TransactionStatus.ROLLBACK_ERROR); //TODO size
        if(!isNull(errorInconsistentCallback)) {
            errorInconsistentCallback.handle(transactionId);
        }
        throw new RuntimeException("Unexpected Rollback Error, transactionId =" + transactionId);
    }

    @Override
    public void transactionRollbacked(String transactionId) {
        log.info("Transaction {} successfully rollbacked", transactionId);
        if(!isNull(errorRollbackedCallback)) {
            errorRollbackedCallback.handle(transactionId);
        }
        transactionStatus.put(transactionId, TransactionStatus.ROLLBACKED);
    }

    @Override
    public void unableToFindParticipants(String transactionId) {
        log.info("Unable to find participants for transactionId = {}", transactionId);
        if(!isNull(initErrorCallback)) {
            initErrorCallback.handle(transactionId);
        }
        transactionStatus.put(transactionId, TransactionStatus.PARTICIPANTS_NOT_FOUND);
    }

    public TransactionStatus checkTransactionId(String transactionId) {
        return transactionStatus.getOrDefault(transactionId, TransactionStatus.NO_INFO);
    }

}
