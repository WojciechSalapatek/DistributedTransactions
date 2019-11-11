package resource.model.datasource;

import org.springframework.context.ApplicationContext;
import org.springframework.web.client.RestTemplate;
import resource.model.connection.FileConnection;
import resource.service.ResourceManagerService;

import java.util.LinkedList;

public class DataSourceFactory {

    public static DataSource fileDataSource(String id, ApplicationContext context){
        DataSource fileDt = new DataSource(new FileConnection(), new LinkedList<>(), id, new RestTemplate(), context);
        context.getBean(ResourceManagerService.class).setDataSourceHandler(fileDt);
        return fileDt;
    }

}
