package com.elmachos.distransactions.library.resource.DataSourceImplementation;

import com.elmachos.distransactions.library.resource.ExecutableStatement;
import com.elmachos.distransactions.library.resource.ResourceManagerStatus;

import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractDataSource implements IDataSourceHandler {

    protected ResourceManagerStatus status;

    protected Queue<ExecutableStatement> statements = new LinkedList<>();


}
