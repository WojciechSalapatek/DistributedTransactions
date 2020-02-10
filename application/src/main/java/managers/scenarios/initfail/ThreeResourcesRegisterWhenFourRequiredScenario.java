package managers.scenarios.initfail;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import managers.resources.alltogether.FirstDatabaseResource;
import managers.resources.alltogether.FirstFileResource;
import managers.resources.alltogether.SecondDatabaseResource;
import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.PostConstruct;
//Initializes transaction for four resources, but only three registers, transaction is expected to fail on init
@Slf4j
public class ThreeResourcesRegisterWhenFourRequiredScenario {

    private final static String QUERY1 = "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")";
    private final static String QUERY2 = "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")";
    private final static String PATH1 = "C:\\Users\\DELL\\Desktop\\GitHub\\distransactions\\application\\src\\main\\resources\\testfiles\\file1";

    @SneakyThrows
    @PostConstruct
    public void execute() {
        log.info("------STARTING------");
        FirstDatabaseResource firstDatabaseResource = new FirstDatabaseResource(QUERY1);
        SecondDatabaseResource secondDatabaseResource = new SecondDatabaseResource(QUERY2, null);
        FirstFileResource firstFileResource = new FirstFileResource(PATH1, null);
        String transactionId = firstDatabaseResource.initiateTransaction();
        secondDatabaseResource.setTransactionId(transactionId);
        firstFileResource.setTransactionId(transactionId);
        secondDatabaseResource.run();
        firstFileResource.run();
    }

    public String getComment(){
        return "Initializes transaction for four resources, but only three registers, transaction is expected to fail on init";
    }
}
