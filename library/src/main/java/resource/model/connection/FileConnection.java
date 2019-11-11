package resource.model.connection;

import lombok.extern.slf4j.Slf4j;
import resource.ExecutableStatement;
import resource.model.AbstractConnection;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
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
        log.info("commiting");
    }

    @Override
    public void close() {
        log.info("closing");
    }

    @Override
    public void rollback() {
        log.info("rollbacking");
    }

    @Override
    public void execute(ExecutableStatement executableStatement) {
        log.info("executing {}", executableStatement.getQuery());
    }

    private boolean checkFile(File file) {
        return file.exists() && !file.isDirectory() && file.canWrite();
    }


}
