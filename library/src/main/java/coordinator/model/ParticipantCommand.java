package coordinator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ParticipantCommand {
    START("START"),
    COMMIT("COMMIT"),
    ROLLBACK("ROLLBACK"),
    ERROR_INITIALIZING("ERROR_INITIALIZING"),
    ERROR_ROLLBACKED("ERROR_ROLLBACKED"),
    ERROR_INCONSISTENT_STATE("ERROR_INCONSISTENT_STATE"),
    SUCCESS("SUCCESS");

    private String command;
}