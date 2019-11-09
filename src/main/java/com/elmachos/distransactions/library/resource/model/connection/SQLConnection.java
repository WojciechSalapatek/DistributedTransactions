package com.elmachos.distransactions.library.resource.model.connection;

import com.elmachos.distransactions.library.resource.ExecutableStatement;
import com.elmachos.distransactions.library.resource.model.AbstractConnection;
import org.springframework.stereotype.Component;

import java.sql.*;
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
    public void commit() throws SQLException {
        conn.commit();
    }

    @Override
    public void close(){
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void rollback() throws SQLException {
        conn.rollback();
    }

    @Override
    public void execute(ExecutableStatement executableStatement) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(executableStatement.getQuery());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}
