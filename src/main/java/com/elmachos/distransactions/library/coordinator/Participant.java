package com.elmachos.distransactions.library.coordinator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Participant {
    private String address;
    private String transactionId;
}
