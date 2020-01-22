package playground.filerm.files;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import resource.model.datasource.DataSourceFactory;
import resource.model.datasource.ResourceManagerService;
import resource.resourceManagers.FileResourceManager;

import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SecondFileResource {

    private String path;

    public void start(String transactionId) {

        try {
            FileResourceManager resourceManager = DataSourceFactory
                    .fileResourceManager(path);

            resourceManager.write("it's working!");
            resourceManager.registerForTransaction(transactionId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
