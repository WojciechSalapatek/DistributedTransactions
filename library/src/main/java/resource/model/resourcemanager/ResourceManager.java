package resource.model.resourcemanager;

import lombok.Data;

@Data
public class ResourceManager {

    private static String id;

    public ResourceManager() {
        id = System.getProperty("resourceHandler.id");
    }

}
