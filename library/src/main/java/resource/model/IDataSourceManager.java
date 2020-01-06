package resource.model;

import org.springframework.http.ResponseEntity;
import resource.resourceManagers.IResourceManger;
import resource.transactions.ParticipantParams;

public interface IDataSourceManager {

    String initiateTransaction(String resourceManagerId, int participants);

    void registerForTransaction(String resourceManagerId, String transactionId);

    IResourceManger getResourceManager(String resourceManagerId);

    ResponseEntity<String> beginTransaction(ParticipantParams participantParams) throws Exception;

    ResponseEntity<String> commit(ParticipantParams participantParams) throws Exception;
}
