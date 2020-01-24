package playground.filerm.files;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import playground.filerm.database.SecondDatabaseApplication;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.FileResourceManager;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FileAndDatabaseResource {

    private String path;
    private SecondDatabaseApplication databaseApplication;

    public void run() {
        try {
            FileResourceManager resourceManager = DataSourceFactory.fileResourceManager(path);
            resourceManager.write("it's working! :) file 1");
            String transactionId = resourceManager.initiateTransaction(2);
            databaseApplication.start(transactionId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
