package com.elmachos.distransactions.library.resource.model.datasource;

import com.elmachos.distransactions.library.resource.ExecutableStatement;
import com.elmachos.distransactions.library.resource.model.AbstractConnection;
import com.elmachos.distransactions.library.resource.model.IDataSourceHandler;
import org.springframework.http.ResponseEntity;

import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;

public class DataSource implements IDataSourceHandler {

    private AbstractConnection sqlConnection;

    private Queue<ExecutableStatement> statements = new LinkedList<>();

    @Override
    public ResponseEntity beginTransaction() {
        try {
            sqlConnection.connect();
            sqlConnection.checkDataSource();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            sqlConnection.close();
            return ResponseEntity.status(400).build();
        }
    }

    @Override
    public ResponseEntity commit() {
        try {
            sqlConnection.commit();
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.status(400).build();
        } finally {
            sqlConnection.close();
        }
    }

    @Override
    public void addExecutableStatement(ExecutableStatement statement) {
        statements.add(statement);
    }

    @Override
    public ResponseEntity rollback() {
        try {
            sqlConnection.rollback();
            return ResponseEntity.ok().build();
        } catch (SQLException e) {
            return ResponseEntity.status(400).build();
        } finally {
            sqlConnection.close();
        }
    }

    @Override
    public ResponseEntity execute() {
        statements.forEach(x -> sqlConnection.execute(x));
        statements.clear();
        return ResponseEntity.status(400).build();
    }

}
