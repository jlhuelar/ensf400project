package com.coveros.training.authentication.domainobjects;

/**
 * Enumerates all possible outcomes for an attempted user registration.
 * <p>
 * This enum represents various statuses that can result from the registration process,
 * such as a user being already registered, missing username or password, a successful registration,
 * a bad password, or an empty status used for initialization.
 * </p>
 */
public enum RegistrationStatusEnums {
    /** The user is already registered. */
    ALREADY_REGISTERED,
    /** The username provided is empty. */
    EMPTY_USERNAME,
    /** The password provided is empty. */
    EMPTY_PASSWORD,
    /** The registration was successful. */
    SUCCESSFULLY_REGISTERED,
    /** The password provided does not meet the required security standards. */
    BAD_PASSWORD,
    /**
     * Represents an empty status.
     * <p>
     * This status is used to indicate the absence of a meaningful status,
     * typically when initializing a variable.
     * </p>
     */
    EMPTY
}
