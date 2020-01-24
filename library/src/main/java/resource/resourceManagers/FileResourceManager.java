package resource.resourceManagers;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.ApplicationContext;
import resource.model.datasource.ResourceManagerService;
import resource.transactions.TransactionStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Getter
public class FileResourceManager implements IResourceManger {

    @Getter
    private final String id;
    private ResourceManagerService resourceManagerService;
    private LinkedList<String> queue;
    private final Path path;
    private Path tmpPath;
    private ApplicationContext applicationContext;

    public FileResourceManager(String id, String path, ResourceManagerService resourceManagerService,
                               ApplicationContext context){
        this.id = id;
        this.path = Paths.get(path);
        this.resourceManagerService = resourceManagerService;
        this.applicationContext = context;
        queue = new LinkedList<>();
        createTmpPath(this.path.getParent());
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
            throw new Exception("File " + path + " is not accessible");
        }
    }

    @Override
    public void execute() throws Exception {
        Files.copy(path, tmpPath);
        Files.writeString(tmpPath, String.join("\n", queue));
        queue.clear();
    }

    @Override
    public TransactionStatus checkTransactionStatus(String transactionId) {
        return resourceManagerService.checkTransactionId(transactionId);
    }

    @Override
    public void rollback() throws Exception {
        if(Files.exists(tmpPath)) Files.delete(tmpPath);
    }

    @Override
    public void commit() throws Exception {
        Files.delete(path);
        Files.move(tmpPath, path);
    }

    private void createTmpPath(Path path) {
        tmpPath = Paths.get(path.toString() + "/CPY_" + RandomStringUtils.randomAlphanumeric(42));
    }

    private boolean checkFile(File file) {
        return file.exists() && !file.isDirectory() && file.canWrite();
    }

}
