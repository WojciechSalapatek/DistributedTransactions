package com.elmachos.distransactions.library.resource.model;

import com.elmachos.distransactions.library.resource.ExecutableStatement;
import org.springframework.stereotype.Component;

import java.sql.SQLException;


public interface AbstractConnection {

    void connect() throws Exception;

    void checkDataSource() throws Exception;

    void commit() throws SQLException;

    void close();

    void rollback() throws SQLException;

    void execute(ExecutableStatement executableStatement);
}
