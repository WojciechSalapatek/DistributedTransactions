package resource.model.resourcemanager;

import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;
import resource.model.connection.FileConnection;
import resource.service.ResourceManagerService;

import java.util.LinkedList;

public class ResourceManagerFactory {

    private static long id = -1;

    public static ResourceManager fileResourceManager(ApplicationContext context){
        String coordId = context.getEnvironment().getProperty("resourceHandler.coordinatorEndpointAddress");
        if (coordId == null){
            throw new Error("property resourceHandler.coordinatorEndpointAddress must be specified!");
        }
        String address = context.getEnvironment().getProperty("resourceHandler.id");
        if (address == null){
            throw new Error("property resourceHandler.id must be specified!");
        }
        ResourceManager fileDt = new ResourceManager(Long.toString(nextId()), new FileConnection(), new LinkedList<>(), coordId, new RestTemplate(), context, address);
        context.getBean(ResourceManagerService.class).registerManager(fileDt);
        return fileDt;
    }

    private static long nextId(){
        return ++id;
    }

}
