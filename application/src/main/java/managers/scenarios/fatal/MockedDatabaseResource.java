package managers.scenarios.fatal;

import lombok.AllArgsConstructor;
import lombok.Setter;
import managers.callbacks.ErrorRollbackedCallback;
import managers.callbacks.FatalErrorCallback;
import managers.callbacks.InitCallback;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.JDBCResourceManager;

import java.sql.Connection;
import java.sql.DriverManager;

@Setter
@AllArgsConstructor
public class MockedDatabaseResource implements Runnable {

    private String query;
    private String transactionId;
    private final String url = "jdbc:postgresql://localhost:5432/dp";
    private final String user = "postgres";
    private final String password = "postgres";

    public void run() {
        try {
            Connection conn;
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            JDBCResourceManager jdbcResourceManager = new MockedResourceManager(DataSourceFactory.jdbcResourceManager(conn));
            jdbcResourceManager.registerInitializationErrorCallback(new InitCallback());
            jdbcResourceManager.registerRollbackedErrorCallback(new ErrorRollbackedCallback());
            jdbcResourceManager.registerUnexpectedErrorCallback(new FatalErrorCallback());
            jdbcResourceManager.executeQuery(query);
            jdbcResourceManager.registerForTransaction(transactionId);
        } catch (Exception e) {

        }
    }

}
