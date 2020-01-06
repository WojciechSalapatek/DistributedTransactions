//package resource.model.datasource;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.web.client.RestTemplate;
//import resource.model.connection.FileConnection;
//import resource.service.CoordinatorHandler;
//
//import java.util.LinkedList;
//
//public class DataSourceFactory {
//
//    private static long id = -1;
//
//    public static ResourceManagerService fileResourceManager(ApplicationContext context){
//        String coordId = context.getEnvironment().getProperty("resourceHandler.coordinatorEndpointAddress");
//        if (coordId == null){
//            throw new Error("property resourceHandler.coordinatorEndpointAddress must be specified!");
//        }
//        String address = context.getEnvironment().getProperty("resourceHandler.id");
//        if (address == null){
//            throw new Error("property resourceHandler.id must be specified!");
//        }
//        ResourceManagerService fileDt = new ResourceManagerService(Long.toString(nextId()), new FileConnection(), new LinkedList<>(), coordId, new RestTemplate(), context, address);
//        context.getBean(CoordinatorHandler.class).registerManager(fileDt);
//        return fileDt;
//    }
//
//    private static long nextId(){
//        return ++id;
//    }
//
//}
