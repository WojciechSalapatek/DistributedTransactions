package com.elmachos.distransactions.library.coordinator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipantCommand {
    START("START"),
    COMMIT("COMMIT"),
    ROLLBACK("ROLLBACK"),
    ERROR_ROLLBACKED("ERROR_ROLLBACKED"),
    ERROR_INCONSISTENT_STATE("ERROR_INCONSISTENT_STATE");

    private String command;
}