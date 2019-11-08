package com.elmachos.distransactions.library.coordinator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantRequestParams {

    private String id;
    private ParticipantCommand command;
    private String message;

}
