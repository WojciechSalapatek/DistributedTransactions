package resource.resourceManagers;

import lombok.Getter;

import java.sql.*;
import java.util.Objects;

public class JDBCResourceManager implements IResourceManger {

    @Getter
    private final String id;
    private Connection conn;

    public JDBCResourceManager(String id, Connection conn) {
        this.id = id;
        this.conn = conn;
    }

    @Override
    public String initiateTransaction(int participants) {
        return null;
    }

    @Override
    public void registerForTransaction(String transactionId) {

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

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void rollback() throws Exception {

    }

    @Override
    public void execute() throws Exception {

    }

}
