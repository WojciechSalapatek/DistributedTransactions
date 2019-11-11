package resource.model;

import resource.ExecutableStatement;


public interface AbstractConnection {

    void connect() throws Exception;

    void checkDataSource() throws Exception;

    void commit() throws Exception;

    void close() throws Exception;

    void rollback() throws Exception;

    void execute(ExecutableStatement executableStatement) throws Exception;
}
