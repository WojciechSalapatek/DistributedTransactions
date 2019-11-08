package com.elmachos.distransactions.library.resource.DataSourceImplementation;

import com.elmachos.distransactions.library.resource.ExecutableStatement;
import com.elmachos.distransactions.library.resource.ResourceManagerStatus;

import java.sql.*;
import java.util.Objects;

public class SQLDataSource extends AbstractDataSource {

    Connection conn;

    public SQLDataSource() throws SQLException {

    }

    private void conectMethod() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://${resourceHandler.url}", "${resourceHandler.user}", "${resourceHandler.password}");
    }

    @Override
    public void beginTransaction() {
        try {
            ResultSet rs = conn.createStatement().executeQuery("select 1");
            if (Objects.nonNull(rs.getObject(0)) && status.equals(ResourceManagerStatus.WAIT)) {
                status = ResourceManagerStatus.BUSY;
            } else {
                throw new RuntimeException("blad");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit() {
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addExecutableStatement(ExecutableStatement statement) {
        statements.add(statement);
    }

    @Override
    public void rollback() {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void execute() {
        try {
            conn.createStatement().executeQuery(statements.remove().getQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
