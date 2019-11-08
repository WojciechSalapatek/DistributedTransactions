package com.elmachos.distransactions.library.resource.DataSourceImplementation;

import com.elmachos.distransactions.library.resource.ExecutableStatement;

public interface IDataSourceHandler {

    void beginTransaction();

    void commit();

    void addExecutableStatement(ExecutableStatement statement);

    void rollback();

    void execute();
}
