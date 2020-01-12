package resource.resourceManagers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import resource.model.datasource.ResourceManagerService;

import java.sql.*;
import java.util.LinkedList;
import java.util.Objects;
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
        if (Objects.nonNull(rs.getObject(0))) {
            throw new Exception();
        }
    }

    //TODO: read ?
    @Override
    public void commit() throws Exception {
        queue.forEach(this::helpExecuteQuery);
    }

    @Override
    public void rollback() throws Exception {
        conn.rollback();
    }

    @Override
    public void execute() throws Exception {
        conn.commit();
    }

    //TODO consider this
    private ResultSet helpExecuteQuery(String query) {
        try {
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException("");
        }
    }

}
