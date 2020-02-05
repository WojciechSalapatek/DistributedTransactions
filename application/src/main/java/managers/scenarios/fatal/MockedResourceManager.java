package managers.scenarios.fatal;

import resource.resourceManagers.JDBCResourceManager;

public class MockedResourceManager extends JDBCResourceManager {
    public MockedResourceManager(JDBCResourceManager manager) {
        super(manager.getId(), manager.getConn(), manager.getResourceManagerService(), manager.getApplicationContext());
    }

    @Override
    public void rollback() throws Exception {
        throw new RuntimeException("Error during rollback");
    }
}
