package playground.filerm;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import resource.model.datasource.ResourceManagerService;
import resource.resourceManagers.FileResourceManager;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FirstFileResource {

    private ResourceManagerService resourceManagerService;
    private SecondFileResource secondFileResource;

    @PostConstruct()
    void start() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            FileResourceManager resourceManager = new FileResourceManager("1", resourceManagerService,
                    "C:\\Users\\micha\\IdeaProjects\\DistributedTransactions\\application\\src\\main\\resources\\testfile.txt");
            String transactionId = resourceManager.initiateTransaction(2);
            secondFileResource.start(transactionId);

        } catch (IOException e) {
            e.printStackTrace();
        }
//

//
//        try {
//            FileWriter writer = new FileWriter("dsaad");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
