package resource.model;

import coordinator.model.TransactionParams;
import resource.ExecutableStatement;
import org.springframework.http.ResponseEntity;

public interface IDataSourceManager {

    String getId();

    String initiateTransaction(int participants);

    void registerForTransaction(String transactionId);

    void addExecutableStatement(ExecutableStatement statement);

    void clearStatements();

    ResponseEntity<String> beginTransaction();

    ResponseEntity<String> commit();


    ResponseEntity<String> rollback();

    ResponseEntity<String> execute();
}
