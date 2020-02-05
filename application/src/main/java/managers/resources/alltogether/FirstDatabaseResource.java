package managers.resources.alltogether;

import lombok.AllArgsConstructor;
import managers.callbacks.ErrorRollbackedCallback;
import managers.callbacks.InitCallback;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.JDBCResourceManager;

import java.sql.Connection;
import java.sql.DriverManager;

@AllArgsConstructor
public class FirstDatabaseResource {

    private String query;
    private final String url = "jdbc:postgresql://localhost:5432/dp";
    private final String user = "postgres";
    private final String password = "postgres";

    public String initiateTransaction() throws Exception{
        Connection conn;
        Class.forName("org.postgresql.Driver");
        conn = DriverManager.getConnection(url, user, password);
        JDBCResourceManager jdbcResourceManager = DataSourceFactory.jdbcResourceManager(conn);
        jdbcResourceManager.registerInitializationErrorCallback(new InitCallback());
        jdbcResourceManager.registerRollbackedErrorCallback(new ErrorRollbackedCallback());
        jdbcResourceManager.executeQuery(query);
        return jdbcResourceManager.initiateTransaction(4);
    }
}