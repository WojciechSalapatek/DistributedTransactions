package managers.scenarios.correct;

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

//Makes transaction on just one database everything should go ok
@Slf4j
public class SingleDatabaseScenario {

    @SneakyThrows
    @PostConstruct
    public void execute(){
        log.info("------STARTING------");
        List<String> queries = Arrays.asList(
                "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")",
                "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")",
                "insert into test_table values ('" + RandomStringUtils.randomAlphabetic(5) + "', " + 99 + ")");
        SingleDatabaseResource resource = new SingleDatabaseResource(queries);
        resource.initiateTransaction();
    }

}
