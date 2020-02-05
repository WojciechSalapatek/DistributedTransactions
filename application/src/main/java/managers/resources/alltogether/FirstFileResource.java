package managers.resources.alltogether;

import lombok.AllArgsConstructor;
import lombok.Setter;
import managers.callbacks.ErrorRollbackedCallback;
import org.springframework.beans.factory.annotation.Autowired;
import managers.callbacks.InitCallback;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.FileResourceManager;

import java.io.IOException;

@Setter
@AllArgsConstructor
public class FirstFileResource implements Runnable {

    private String path;
    private String transactionId;

    public void run() {
        try {
            FileResourceManager resourceManager = DataSourceFactory.fileResourceManager(path);
            resourceManager.registerInitializationErrorCallback(new InitCallback());
            resourceManager.registerRollbackedErrorCallback(new ErrorRollbackedCallback());
            resourceManager.write("It's working!");
            resourceManager.registerForTransaction(transactionId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
