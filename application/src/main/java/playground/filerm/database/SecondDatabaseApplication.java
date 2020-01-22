package playground.filerm.database;

import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.JDBCResourceManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SecondDatabaseApplication {

    private final String url = "jdbc:postgresql://localhost:5432/dp";
    private final String user = "postgres";
    private final String password = "postgres";


    public void start (String transactionId) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            JDBCResourceManager jdbcResourceManager = DataSourceFactory.jdbcResourceManager(conn);
            jdbcResourceManager.executeQuery("insert into test_table1 values (112, 2)");
            jdbcResourceManager.registerForTransaction(transactionId);


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
