package playground.filerm;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import resource.model.datasource.DataSourceFactory;
import resource.model.datasource.ResourceManagerService;
import resource.resourceManagers.FileResourceManager;

import java.io.IOException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SecondFileResource {

    private ResourceManagerService resourceManagerService;

    public void start(String transactionId) {

        try {
            FileResourceManager resourceManager = DataSourceFactory
                    .fileResourceManager("C:\\Users\\DELL\\Desktop\\GitHub\\distransactions\\application\\src\\main\\resources\\testfile1.txt");

            resourceManager.write("it's working!");
            resourceManager.registerForTransaction(transactionId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
