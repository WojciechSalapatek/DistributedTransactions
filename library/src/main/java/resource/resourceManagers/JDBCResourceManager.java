package resource.resourceManagers;

import lombok.Getter;
import resource.model.datasource.ResourceManagerService;
import resource.transactions.TransactionStatus;

import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;


public class JDBCResourceManager implements IResourceManger {

    @Getter
    private final String id;
    private Connection conn;
    private ResourceManagerService resourceManagerService;
    private Queue<String> queue = new LinkedList<>();

    public JDBCResourceManager(String id, Connection conn, ResourceManagerService resourceManagerService) {
        this.id = id;
        this.conn = conn;
        this.resourceManagerService = resourceManagerService;
        unableAutoCommit(conn);
        resourceManagerService.addResourceManager(this);
    }

    private void unableAutoCommit(Connection conn) {
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void executeQuery(String query) {
        queue.add(query);
    }

    @Override
    public String initiateTransaction(int participants) {
        return resourceManagerService.initiateTransaction(id, participants);
    }

    @Override
    public void registerForTransaction(String transactionId) {
        resourceManagerService.registerForTransaction(id, transactionId);
    }

    @Override
    public void checkDataSource() throws Exception {
        ResultSet rs = conn.createStatement().executeQuery("select 1");
        if (!rs.next()) {
            throw new Exception();
        }
    }

    @Override
    public void execute() throws Exception {
        queue.forEach(this::helpExecuteQuery);
    }

    @Override
    public TransactionStatus checkTransactionStatus(String transactionId) {
        return resourceManagerService.checkTransactionId(transactionId);
    }

    @Override
    public void rollback() throws Exception {
        conn.rollback();
    }

    @Override
    public void commit() throws Exception {
        conn.commit();
    }

    private void helpExecuteQuery(String query) {
        try {
            conn.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
