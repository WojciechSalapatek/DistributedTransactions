package com.elmachos.distransactions.library.resource.model;

import com.elmachos.distransactions.library.resource.ExecutableStatement;
import org.springframework.http.ResponseEntity;

public interface IDataSourceHandler {

    ResponseEntity beginTransaction();

    ResponseEntity commit();

    void addExecutableStatement(ExecutableStatement statement);

    ResponseEntity rollback();

    ResponseEntity execute();
}
