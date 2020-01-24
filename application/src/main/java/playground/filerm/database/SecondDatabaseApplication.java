package playground.filerm.database;

import lombok.AllArgsConstructor;
import playground.filerm.files.ErrorRollbackedCallback;
import playground.filerm.files.InitCallback;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.JDBCResourceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@AllArgsConstructor
public class SecondDatabaseApplication {

    private final String url = "jdbc:postgresql://localhost:5432/dp";
    private final String user = "postgres";
    private final String password = "postgres";
    private String query;

    public void start (String transactionId) {
        Connection conn = null;
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