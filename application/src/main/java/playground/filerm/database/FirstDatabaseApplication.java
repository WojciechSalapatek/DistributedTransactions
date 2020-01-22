package playground.filerm.database;

import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.JDBCResourceManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FirstDatabaseApplication {

    private final String url = "jdbc:postgresql://localhost:5432/dp";
    private final String user = "postgres";
    private final String password = "postgres";
    SecondDatabaseApplication secondDatabaseApplication;

    public FirstDatabaseApplication(SecondDatabaseApplication secondDatabaseApplication) {
        this.secondDatabaseApplication = secondDatabaseApplication;
    }

    public void run() {

        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            JDBCResourceManager jdbcResourceManager = DataSourceFactory.jdbcResourceManager(conn);
            jdbcResourceManager.executeQuery("insert into test_table values ('dfds', 1)");
            String transactionId = jdbcResourceManager.initiateTransaction(2);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            secondDatabaseApplication.start(transactionId);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
