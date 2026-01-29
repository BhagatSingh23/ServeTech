package com.ServeTech.Webapp.entity.enums;

public enum AccountStatus {

    // Status of the user's account
    // This is used to determine whether the user is allowed to login
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    PENDING_VERIFICATION,
    DELETED
}