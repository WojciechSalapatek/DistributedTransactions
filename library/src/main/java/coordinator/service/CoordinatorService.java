package coordinator.service;

import coordinator.model.TransactionParams;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CoordinatorService {

    private static long id = 0;
    @NonNull
    private ParticipantRestService participantService;
    @Getter
    Map<String, TransactionHandler> handlers = new HashMap<>();

    public synchronized static String getNextId() {
        return Long.toString(id++);
    }

    public String createTransaction(TransactionParams params) {
        String transactionId = getNextId();
        TransactionHandler handler = new TransactionHandler(
                transactionId, params.getParticipants(), participantService,
                new ConcurrentHashMap<>(), params.getMaster().getManagerId(),
                params.getMaster().getAddress()
        );
        handler.registerParticipant(params.getMaster());
        handler.start();
        log.info("Created handler [participants: {}] for transactionId : {}, Master {} with id {}",
                params.getParticipants(), transactionId, params.getMaster().getAddress(), params.getMaster().getManagerId());
        handlers.put(transactionId, handler);
        return transactionId;
    }

}
