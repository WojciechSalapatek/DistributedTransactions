package resource.transactions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TransactionStatus {

    NO_INFO("no info"),
    PARTICIPANTS_NOT_FOUND("participants not found"),
    COMMITED("commited"),
    ROLLBACKED("rollbacked"),
    ROLLBACK_ERROR("rollback error");

    private String status;
}
