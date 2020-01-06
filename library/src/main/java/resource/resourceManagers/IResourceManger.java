package resource.resourceManagers;

import org.springframework.http.ResponseEntity;
import resource.transactions.ParticipantParams;

public interface IResourceManger {

    String initiateTransaction(int participants);

    void registerForTransaction(String transactionId);

    void checkDataSource() throws Exception;

    void commit() throws Exception;

    void close() throws Exception;

    void rollback() throws Exception;

    void execute() throws Exception;

    String getId();

}
