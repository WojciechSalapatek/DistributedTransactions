package coordinator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantRequestParams {

    private String id;
    private String managerId;
    private ParticipantCommand command;
    private String message;

}
