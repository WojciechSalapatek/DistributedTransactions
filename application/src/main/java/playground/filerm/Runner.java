package playground.filerm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import playground.filerm.database.FirstDatabaseApplication;
import playground.filerm.database.SecondDatabaseApplication;
import playground.filerm.files.FirstFileResource;
import playground.filerm.files.SecondFileResource;
import resource.model.datasource.DataSourceFactory;
import resource.resourceManagers.FileResourceManager;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class Runner {

    public static final String ROOT = "C:\\Users\\DELL\\Desktop\\GitHub\\distransactions\\application\\src\\main\\resources\\testfiles\\";

    @PostConstruct
    public void run() throws Exception{
//        testFiles();
        testDatabase();
    }

    private void testFiles() throws IOException {
        String files1[] = new String[1000];
        String files2[] = new String[1000];
        for (int i = 0; i < 250; i++) {
            String f1 = ROOT + RandomStringUtils.randomAlphanumeric(42);
            String f2 = ROOT + RandomStringUtils.randomAlphanumeric(42);
            File file1 = new File(f1);
            File file2 = new File(f2);
            file1.createNewFile();
            file2.createNewFile();
            files1[i] = f1;
            files2[i] = f2;

        }
        log.info("--------------------------------STARTING!-------------------------------------");
        for (int i = 0; i < 250; i++) {
            SecondFileResource secondFileResource= new SecondFileResource(files1[i]);
            FirstFileResource firstFileResource = new FirstFileResource(files2[i], secondFileResource);
            firstFileResource.run();
        }
    }

    private void testDatabase() {
        String query1[] = new String[25];
        String query2[] = new String[25];
        for (int i = 0; i < 25; i++) {
            query1[i] = "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + i + ")";
            query2[i] = "insert into test_table1 values (" + RandomStringUtils.randomNumeric(5) + ", " + i + ")";
        }

        log.info("--------------------------------STARTING!-------------------------------------");
        for (int i = 0; i < 3; i++) {
            SecondDatabaseApplication secondDatabaseApplication = new SecondDatabaseApplication(query1[i]);
            FirstDatabaseApplication firstDatabaseApplication = new FirstDatabaseApplication(secondDatabaseApplication, query2[i]);
            firstDatabaseApplication.run();
        }
    }
}
