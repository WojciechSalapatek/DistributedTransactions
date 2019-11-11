package coordinator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ParticipantStatus {
    INIT("init"),
    STARTED("started"),
    WAITING_FOR_COMMIT("waitingForCommit"),
    ERROR("error"),
    COMITTING("commiting"),
    COMMITED("commited"),
    ROLLBACK("rollback"),
    ROLLBACKED("rollbacked");

    private String status;
}
