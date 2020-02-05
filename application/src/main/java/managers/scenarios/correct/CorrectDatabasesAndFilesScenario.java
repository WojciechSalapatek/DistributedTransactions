package managers.scenarios.correct;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import managers.resources.alltogether.FirstDatabaseResource;
import managers.resources.alltogether.FirstFileResource;
import managers.resources.alltogether.SecondDatabaseResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/*
Makes transaction on two databases and two files, everything should go ok
 */
@Slf4j
public class CorrectDatabasesAndFilesScenario {

    private final static String QUERY1 = "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")";
    private final static String QUERY2 = "insert into test_table1 values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")";
    private final static String PATH1 = "C:\\Users\\DELL\\Desktop\\GitHub\\distransactions\\application\\src\\main\\resources\\testfiles\\file1";
    private final static String PATH2 = "C:\\Users\\DELL\\Desktop\\GitHub\\distransactions\\application\\src\\main\\resources\\testfiles\\file2";

    @SneakyThrows
    @PostConstruct
    public void execute(){
        log.info("------STARTING------");
        FirstDatabaseResource firstDatabaseResource = new FirstDatabaseResource(QUERY1);
        SecondDatabaseResource secondDatabaseResource = new SecondDatabaseResource(QUERY2, null);
        FirstFileResource firstFileResource = new FirstFileResource(PATH1, null);
        FirstFileResource secondFileResource = new FirstFileResource(PATH2, null);
        String transactionId = firstDatabaseResource.initiateTransaction();
        secondDatabaseResource.setTransactionId(transactionId);
        firstFileResource.setTransactionId(transactionId);
        secondFileResource.setTransactionId(transactionId);
        secondDatabaseResource.run();
        firstFileResource.run();
        secondFileResource.run();
    }

}
