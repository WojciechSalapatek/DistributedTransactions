package resource.model.connection;

import resource.ExecutableStatement;
import resource.model.AbstractConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Objects;

public class SQLConnection implements AbstractConnection {

    private Connection conn;

    @Override
    public void connect() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://${resourceHandler.url}", "${resourceHandler.user}", "${resourceHandler.password}");
    }

    @Override
    public void checkDataSource() throws Exception {
        ResultSet rs = conn.createStatement().executeQuery("select 1");
        if (Objects.nonNull(rs.getObject(0))) {
            throw new Exception();
        }
    }

    @Override
    public void commit() throws Exception {
        conn.commit();
    }

    @Override
    public void close() throws Exception {
        conn.close();
    }

    @Override
    public void rollback() throws Exception {
        conn.rollback();
    }

    @Override
    public void execute(ExecutableStatement executableStatement) throws Exception {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(executableStatement.getQuery());
    }

}
