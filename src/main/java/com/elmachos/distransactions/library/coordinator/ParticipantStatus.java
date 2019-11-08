package com.elmachos.distransactions.library.coordinator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipantStatus {
    INIT("init"),
    STARTED("started"),
    WAITING_FOR_COMMIT("waitingForCommit"),
    ERROR("error"),
    COMITTING("commiting"),
    COMMITED("commited"),
    ROLLBACK("rollback");

    private String status;
}
