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
public class SecondFileResource {

    private ApplicationContext context;

    public void start(String transactionId) {
        ResourceManager resourceManager = ResourceManagerFactory.fileResourceManager(context);
        resourceManager.addExecutableStatement(new FileExecutbleStatement("dummy"));
        resourceManager.registerForTransaction(transactionId);
    }

}
