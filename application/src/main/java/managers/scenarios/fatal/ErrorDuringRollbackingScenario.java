package managers.scenarios.fatal;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import managers.resources.alltogether.FirstDatabaseResource;
import managers.resources.alltogether.FirstFileResource;
import managers.resources.alltogether.SecondDatabaseResource;
import managers.resources.single.SingleDatabaseResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Slf4j
//Makes transaction on just one database with one invalid query, transaction is expected to be rollbacked, but
//rollback fails, fatal error handler is expected to be invoked
public class ErrorDuringRollbackingScenario {

    private final static String QUERY1 = "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")";
    private final static String QUERY2 = "Invalid query that causes error";
    private final static String PATH1 = "C:\\Users\\DELL\\Desktop\\GitHub\\distransactions\\application\\src\\main\\resources\\testfiles\\file1";
    private final static String PATH2 = "C:\\Users\\DELL\\Desktop\\GitHub\\distransactions\\application\\src\\main\\resources\\testfiles\\file2";


    @SneakyThrows
    @PostConstruct
    public void execute() {
        log.info("------STARTING------");
        FirstDatabaseResource firstDatabaseResource = new FirstDatabaseResource(QUERY1);
        MockedDatabaseResource secondDatabaseResource = new MockedDatabaseResource(QUERY2, null);
        FirstFileResource firstFileResource = new FirstFileResource(PATH1, null);
        FirstFileResource secondFileResource = new FirstFileResource(PATH2, null);
        String transactionId = firstDatabaseResource.initiateTransaction();
        secondDatabaseResource.setTransactionId(transactionId);
        firstFileResource.setTransactionId(transactionId);
        secondFileResource.setTransactionId(transactionId);
        firstFileResource.run();
        secondFileResource.run();
        secondDatabaseResource.run();
    }

}
