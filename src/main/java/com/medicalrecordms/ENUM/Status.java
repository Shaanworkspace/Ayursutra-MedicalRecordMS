package com.medicalrecordms.ENUM;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    ACTIVE,
    COMPLETED,
    PENDING,
    HAVING_TREATMENT,
    HAVING_THERAPY;

    @JsonCreator
    public static Status fromValue(String value) {
        for (Status status : Status.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}
