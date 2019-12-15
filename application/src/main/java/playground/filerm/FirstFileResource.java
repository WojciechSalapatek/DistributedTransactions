package playground.filerm;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import resource.model.resourcemanager.ResourceManager;
import resource.model.resourcemanager.ResourceManagerFactory;
import resource.model.statements.FileExecutbleStatement;

import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FirstFileResource {

    private ApplicationContext context;
    private SecondFileResource secondFileResource;

    @PostConstruct()
    void start() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResourceManager resourceManager = ResourceManagerFactory.fileResourceManager(context);
        String transactionId = resourceManager.initiateTransaction(2);
        resourceManager.addExecutableStatement(new FileExecutbleStatement("dummy"));
        resourceManager.registerForTransaction(transactionId);
        secondFileResource.start(transactionId);
    }

}
