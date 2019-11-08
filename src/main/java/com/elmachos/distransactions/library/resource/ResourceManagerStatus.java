package com.elmachos.distransactions.library.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourceManagerStatus {
    WAIT("wait"),
    ERROR("error"),
    BUSY("busy");

    private String status;
}
