package managers.scenarios.rollbacks;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import managers.resources.single.SingleDatabaseResource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Slf4j
//Makes transaction on just one database with one invalid query, transaction is expected to be rollbacked
public class SingleDatabaseInvalidQuery {

    @SneakyThrows
    @PostConstruct
    public void execute() {
        log.info("------STARTING------");
        List<String> queries = Arrays.asList(
                "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")",
                "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")",
                "Invalid query");
        SingleDatabaseResource resource = new SingleDatabaseResource(queries);
        resource.initiateTransaction();
    }

    public String getComment(){
        return "Makes transaction on just one database with one invalid query, transaction is expected to be rollbacked";
    }

}
