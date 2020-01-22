package playground.filerm;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

@Slf4j
@Component
public class Runner {

    public static final String ROOT = "C:\\Users\\DELL\\Desktop\\GitHub\\distransactions\\application\\src\\main\\resources\\testfiles\\";

    @PostConstruct
    public void run() throws Exception{
        String files1[] = new String[1000];
        String files2[] = new String[1000];
        for (int i = 0; i < 25; i++) {
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
        for (int i = 0; i < 25; i++) {
            SecondFileResource secondFileResource= new SecondFileResource(files1[i]);
            FirstFileResource firstFileResource = new FirstFileResource(files2[i], secondFileResource);
            firstFileResource.run();
        }

    }

}
