package playground.filerm;

import coordinator.model.Participant;
import coordinator.model.TransactionParams;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import resource.model.datasource.DataSource;
import resource.model.datasource.DataSourceFactory;
import resource.model.statements.FileExecutbleStatement;

import javax.annotation.PostConstruct;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FirstFileResource {

    private Environment environment;
    private ApplicationContext context;

    @PostConstruct()
    void start(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String partId = environment.getProperty("resourceHandler.id");
        String coordId = environment.getProperty("resourceHandler.coordinatorEndpointAddress");
        DataSource dataSource = DataSourceFactory.fileDataSource(coordId, context);
        TransactionParams transactionParams = new TransactionParams(
                new Participant(partId, null),
                1);
        String transactionId = dataSource.initiateTransaction(transactionParams);
        dataSource.addExecutableStatement(new FileExecutbleStatement("dummy"));
        dataSource.registerForTransaction(transactionId);
    }

}
