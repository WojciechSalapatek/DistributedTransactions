package playground.filerm.database;

import lombok.AllArgsConstructor;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.JDBCResourceManager;

import java.sql.Connection;
import java.sql.DriverManager;

@AllArgsConstructor
public class FirstDatabaseApplication {

    private final String url = "jdbc:postgresql://localhost:5432/dp";
    private final String user = "postgres";
    private final String password = "postgres";
    private SecondDatabaseApplication secondDatabaseApplication;
    private String query;

    public void run() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            JDBCResourceManager jdbcResourceManager = DataSourceFactory.jdbcResourceManager(conn);
            jdbcResourceManager.executeQuery(query);
            String transactionId = jdbcResourceManager.initiateTransaction(2);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            secondDatabaseApplication.start(transactionId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}