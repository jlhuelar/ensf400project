package com.coveros.training.authentication.domainobjects;

/**
 * Enumerates the possible outcomes of evaluating the complexity of a potential password.
 * <p>
 * These statuses represent various conditions such as a password being too short, too long,
 * having insufficient entropy, or meeting the required criteria. The {@code NULL} status is used
 * only for representing an empty result, as in {@link PasswordResult#createEmpty()}.
 * </p>
 */
public enum PasswordResultEnums {
    /** The password is too short. */
    TOO_SHORT,

    /**
     * The password is too long, which may expose the system to DoS attacks due to slow processing.
     */
    TOO_LONG,

    /** The password is empty. */
    EMPTY_PASSWORD,

    /**
     * The password does not meet the minimum entropy requirements.
     * <p>
     * This status indicates that the measured entropy, as determined by the evaluation tool,
     * is insufficient.
     * </p>
     */
    INSUFFICIENT_ENTROPY,

    /** The password meets the required criteria. */
    SUCCESS,

    /**
     * Represents an empty result, typically used when initializing a variable to indicate no value.
     * <p>
     * See {@link PasswordResult#createEmpty()}.
     * </p>
     */
    NULL
}
