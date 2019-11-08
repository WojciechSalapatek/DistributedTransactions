package com.elmachos.distransactions.library.coordinator.model;

import lombok.Data;

@Data
public class TransactionParams {
    private Participant master;
    private int participants;
}
