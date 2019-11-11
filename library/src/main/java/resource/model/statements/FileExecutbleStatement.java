package resource.model.statements;

import lombok.AllArgsConstructor;
import resource.ExecutableStatement;

@AllArgsConstructor
public class FileExecutbleStatement implements ExecutableStatement {

    private String query;

    @Override
    public String getQuery() {
        return query;
    }
}
