package resource.resourceManagers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import resource.model.datasource.ResourceManagerService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;

public class FileResourceManager implements IResourceManger {

    @Getter
    private final String id;
    private ResourceManagerService resourceManagerService;
    private Queue<String> queue = new LinkedList<>();
    private final Path path;
    private Path tmpPath;

    public FileResourceManager(String id, String path, ResourceManagerService resourceManagerService){
        this.id = id;
        this.path = Paths.get(path);
        this.resourceManagerService = resourceManagerService;
        resourceManagerService.addResourceManager(this);
    }

    public void write(String abc) throws IOException {
        queue.add(abc);
    }

    @Override
    public String initiateTransaction(int participants) {
        return resourceManagerService.initiateTransaction(id, participants);
    }

    @Override
    public void registerForTransaction(String transactionId) {
        resourceManagerService.registerForTransaction(id, transactionId);
    }

    @Override
    public void checkDataSource() throws Exception {
        if (!checkFile(path.toFile())) {
            throw new Exception();
        }
    }

    @Override
    public void commit() throws Exception {
        createTmpPath(path.getParent());
        Files.copy(path, tmpPath);
        Files.writeString(tmpPath, String.join("\n", queue));
        queue.clear();
    }

    @Override
    public void rollback() throws Exception {
        Files.delete(tmpPath);
    }

    @Override
    public void execute() throws Exception {
        Files.delete(path);
        Files.move(tmpPath, path);
    }

    private void createTmpPath(Path path) {
        tmpPath = Paths.get(path.toString() + "/" + RandomStringUtils.random(10));
    }

    private boolean checkFile(File file) {
        return file.exists() && !file.isDirectory() && file.canWrite();
    }

}
