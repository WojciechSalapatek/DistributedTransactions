package com.elmachos.distransactions.library.coordinator.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Participant {
    private String address;
    private String transactionId;
}
