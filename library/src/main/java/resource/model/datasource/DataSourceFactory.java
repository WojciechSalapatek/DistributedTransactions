package resource.model.datasource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import resource.resourceManagers.FileResourceManager;
import resource.resourceManagers.JDBCResourceManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@Service
public class DataSourceFactory implements ApplicationContextAware {

    private static ApplicationContext ac;
    private static long id = -1;

    public static FileResourceManager fileResourceManager(String path) {
        return new FileResourceManager(nextId(), path, ac.getBean(ResourceManagerService.class));
    }

    public static JDBCResourceManager jdbcResourceManager(Connection connection) {
        return new JDBCResourceManager(nextId(), connection, ac.getBean(ResourceManagerService.class));
    }

    private static String nextId(){
        return String.valueOf(++id);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }
}
