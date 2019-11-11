package resource.model.datasource;

import coordinator.model.Participant;
import coordinator.model.TransactionParams;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import resource.ExecutableStatement;
import resource.model.AbstractConnection;
import resource.model.IDataSourceHandler;

import java.util.Queue;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
public class DataSource implements IDataSourceHandler {

    private AbstractConnection connection;
    private Queue<ExecutableStatement> statements;
    private String coordinatorEndpointAddress;
    private RestTemplate coordinatorEndpoint;
    private ApplicationContext context;

    private static final String createTransactionSuffix = "/createTransaction";
    private static final Function<String, String> createRegisterSuffix = (id) -> "/transaction/" + id + "/register";

    @Override
    public String initiateTransaction(TransactionParams transactionParams) {
        log.info("Initializing transaction");
        HttpEntity<TransactionParams> req = new HttpEntity<>(transactionParams);
        return coordinatorEndpoint.postForEntity(coordinatorEndpointAddress + createTransactionSuffix, req, String.class).getBody();
    }

    @Override
    public void registerForTransaction(String transactionId) {
        log.info("Registering for transaction {}", transactionId);
        Participant rmModel = new Participant(context.getEnvironment().getProperty("resourceHandler.id"), transactionId);
        HttpEntity<Participant> req = new HttpEntity<>(rmModel);
        coordinatorEndpoint.postForEntity(coordinatorEndpointAddress + createRegisterSuffix.apply(transactionId),
                req, String.class);
    }

    @Override
    public void clearStatements() {
        statements.clear();
    }


    @Override
    public ResponseEntity<String> beginTransaction() {
        try {
            connection.connect();
            connection.checkDataSource();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            closeHandler();
            return ResponseEntity.status(400).build();
        }
    }

    @Override
    public ResponseEntity<String> commit() {
        try {
            connection.commit();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @Override
    public void addExecutableStatement(ExecutableStatement statement) {
        statements.add(statement);
    }

    @Override
    public ResponseEntity<String> rollback() {
        try {
            connection.rollback();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        } finally {
            closeHandler();
        }
    }

    @Override
    public ResponseEntity<String> execute() {
        try {
            statements.forEach(this::executeHandler);
            statements.clear();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
        return ResponseEntity.status(400).build();
    }

    private void executeHandler(ExecutableStatement st) {
        try {
            connection.execute(st);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void closeHandler() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
