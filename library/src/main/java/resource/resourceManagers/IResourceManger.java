package resource.resourceManagers;

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

}
