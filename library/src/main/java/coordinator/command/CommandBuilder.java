package coordinator.command;

public class CommandBuilder {

    public static Command getRollbackCommand(String transactionId) {
        return RollbackCommand
                .builder()
                .message("Rollback Command")
                .transactionId(transactionId)
                .build();
    }

    public static Command getSuccessCommand(String transactionId, String managerId) {
        return SuccessCommand
                .builder()
                .transactionId(transactionId)
                .message("Success Command")
                .managerId(managerId)
                .build();
    }

    public static Command getErrorInitializingCommand(String transactionId, String managerId) {
        return ErrorInitializingCommand
                .builder()
                .transactionId(transactionId)
                .managerId(managerId)
                .message("Waiting for all participants to register failed")
                .build();
    }

    public static Command getCommitCommand(String transactionId, String managerId) {
        return CommitCommand
                .builder()
                .message("Commit command")
                .transactionId(managerId)
                .build();
    }

    public static Command getErrorRollbackedCommand(String transactionId, String managerId) {
        return ErrorRollbackedCommand
                .builder()
                .transactionId(transactionId)
                .managerId(managerId)
                .message("Intermediate changes was successfully rollbacked")
                .build();
    }

    public static Command getErrorRollbackedCommand(String transactionId, String managerId, String message) {
        return ErrorInconsistentStateCommand
                .builder()
                .transactionId(transactionId)
                .managerId(managerId)
                .message(message)
                .build();
    }

}
