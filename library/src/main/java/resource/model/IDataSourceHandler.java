package resource.model;

import coordinator.model.TransactionParams;
import resource.ExecutableStatement;
import org.springframework.http.ResponseEntity;

public interface IDataSourceHandler {

    String initiateTransaction(TransactionParams transactionParams);

    void registerForTransaction(String transactionId);

    void addExecutableStatement(ExecutableStatement statement);

    void clearStatements();

    ResponseEntity<String> beginTransaction();

    ResponseEntity<String> commit();


    ResponseEntity<String> rollback();

    ResponseEntity<String> execute();
}
