package managers.resources.alltogether;

import lombok.AllArgsConstructor;
import lombok.Setter;
import managers.callbacks.ErrorRollbackedCallback;
import managers.callbacks.InitCallback;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.JDBCResourceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Setter
@AllArgsConstructor
public class SecondDatabaseResource implements Runnable{

    private String query;
    private String transactionId;
    private final String url = "jdbc:postgresql://localhost:5432/dp";
    private final String user = "postgres";
    private final String password = "postgres";

    public void run() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(url, user, password);

            JDBCResourceManager jdbcResourceManager = DataSourceFactory.jdbcResourceManager(conn);
            jdbcResourceManager.registerInitializationErrorCallback(new InitCallback());
            jdbcResourceManager.registerRollbackedErrorCallback(new ErrorRollbackedCallback());
            jdbcResourceManager.executeQuery(query);
            jdbcResourceManager.registerForTransaction(transactionId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}