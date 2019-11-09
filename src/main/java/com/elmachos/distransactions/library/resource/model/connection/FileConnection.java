package com.elmachos.distransactions.library.resource.model.connection;

import com.elmachos.distransactions.library.resource.ExecutableStatement;
import com.elmachos.distransactions.library.resource.model.AbstractConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class FileConnection implements AbstractConnection {

    private Path file;
    private String tmpPath;

    @Override
    public void connect() {
       file = Paths.get("${resourceHandler.url}");
    }

    @Override
    public void checkDataSource() throws Exception {
        connect();
        if (checkFile(file.toFile())) {
            throw new Exception();
        }
    }

    @Override
    public void commit() {

    }

    @Override
    public void close()  {

    }

    @Override
    public void rollback()  {

    }

    @Override
    public void execute(ExecutableStatement executableStatement) {

    }

    private boolean checkFile(File file) {
        return file.exists() && !file.isDirectory() && file.canWrite();
    }


}
