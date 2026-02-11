package com.ServeTech.Webapp.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gender enum
 */
public enum GenderType {
    MALE("MALE"),
    FEMALE("FEMALE"),
    OTHER("OTHER");

    private final String value;

    GenderType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}