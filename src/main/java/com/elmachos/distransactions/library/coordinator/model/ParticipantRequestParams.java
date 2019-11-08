package com.elmachos.distransactions.library.coordinator.model;

import com.elmachos.distransactions.library.coordinator.model.ParticipantCommand;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantRequestParams {

    private String id;
    private ParticipantCommand command;
    private String message;

}
