package playground.filerm.files;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.FileResourceManager;
import resource.transactions.TransactionStatus;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FirstFileResource implements Runnable {

    private String path;
    private SecondFileResource secondFileResource;

    public void run() {
        try {
            FileResourceManager resourceManager = DataSourceFactory.fileResourceManager(path);
            resourceManager.registerInitializationErrorCallback(new InitCallback());
            resourceManager.registerRollbackedErrorCallback(new ErrorRollbackedCallback());
            resourceManager.write("it's working! :) file 1");
            String transactionId = resourceManager.initiateTransaction(2);
            secondFileResource.start(transactionId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
