package resource.resourceManagers;

import lombok.Getter;
import resource.model.datasource.ResourceManagerService;

import java.sql.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class JDBCResourceManager implements IResourceManger {

    @Getter
    private final String id;
    private Connection conn;
    private final ResourceManagerService resourceManagerService;
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

    @Override
    public void commit() throws Exception {

    }

    @Override
    public void rollback() throws Exception {

    }

    @Override
    public void execute() throws Exception {

    }

}
