package playground.filerm;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.FileResourceManager;

import javax.annotation.PostConstruct;
import java.io.IOException;

@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FirstFileResource implements Runnable{

    private String path;
    private SecondFileResource secondFileResource;

    public void run() {
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            FileResourceManager resourceManager = DataSourceFactory.fileResourceManager(path);
            resourceManager.write("it's working! :) file 1");
            String transactionId = resourceManager.initiateTransaction(2);
            secondFileResource.start(transactionId);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
