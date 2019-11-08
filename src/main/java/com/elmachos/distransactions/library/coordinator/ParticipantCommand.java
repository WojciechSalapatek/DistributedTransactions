package com.elmachos.distransactions.library.coordinator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipantCommand {
    START("START"),
    COMMIT("COMMIT"),
    ROLLBACK("ROLLBACK");

    private String command;
}