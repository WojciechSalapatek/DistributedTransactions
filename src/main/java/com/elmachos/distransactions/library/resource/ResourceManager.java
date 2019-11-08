package com.elmachos.distransactions.library.resource;

public interface ResourceManager {

    void beginTransaction();

    void commit();

    void addExecutableStatement(ExecutableStatement statement);

    void rollback();
}
