package resource.resourceManagers;

import org.springframework.context.ApplicationContext;
import resource.model.ErrorCallback;
import resource.model.datasource.ResourceManagerService;
import resource.transactions.TransactionStatus;

public interface IResourceManger {

    String initiateTransaction(int participants);

    void registerForTransaction(String transactionId);

    void checkDataSource() throws Exception;

    void commit() throws Exception;

    void rollback() throws Exception;

    void execute() throws Exception;

    TransactionStatus checkTransactionStatus(String transactionId);

    String getId();

    ApplicationContext getApplicationContext();

    default void registerInitializationErrorCallback(ErrorCallback callback){
        getApplicationContext().getBean(ResourceManagerService.class).setInitErrorCallback(callback);
    }

    default void registerRollbackedErrorCallback(ErrorCallback callback){
        getApplicationContext().getBean(ResourceManagerService.class).setErrorRollbackedCallback(callback);
    }

    default void registerUnexpectedErrorCallback(ErrorCallback callback){
        getApplicationContext().getBean(ResourceManagerService.class).setErrorInconsistentCallback(callback);
    }

}
