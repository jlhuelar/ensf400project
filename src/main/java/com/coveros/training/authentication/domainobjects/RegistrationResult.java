package com.coveros.training.authentication.domainobjects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Holds detailed information about the outcome of a user registration attempt.
 * <p>
 * This class encapsulates whether the registration was successful, the resulting status,
 * and any additional message associated with the registration process.
 * See {@link com.coveros.training.authentication.RegistrationUtils#processRegistration}
 * for the context in which this result is used.
 * </p>
 */
public final class RegistrationResult {

    /**
     * Indicates whether the registration was successful.
     */
    public final boolean wasSuccessfullyRegistered;

    /**
     * The status of the registration attempt.
     */
    public final RegistrationStatusEnums status;

    /**
     * A detailed message associated with the registration result.
     */
    private final String message;

    /**
     * Constructs a new {@code RegistrationResult} with the specified success flag, status, and message.
     *
     * @param wasSuccessfullyRegistered {@code true} if registration was successful; {@code false} otherwise
     * @param status                    the status of the registration attempt
     * @param message                   additional information regarding the registration outcome
     */
    public RegistrationResult(boolean wasSuccessfullyRegistered, RegistrationStatusEnums status, String message) {
        this.wasSuccessfullyRegistered = wasSuccessfullyRegistered;
        this.status = status;
        this.message = message;
    }

    /**
     * Constructs a new {@code RegistrationResult} with the specified success flag and status.
     * The message is set to an empty string.
     *
     * @param wasSuccessfullyRegistered {@code true} if registration was successful; {@code false} otherwise
     * @param status                    the status of the registration attempt
     */
    public RegistrationResult(boolean wasSuccessfullyRegistered, RegistrationStatusEnums status) {
        this(wasSuccessfullyRegistered, status, "");
    }

    /**
     * Compares this {@code RegistrationResult} with another object for equality.
     *
     * @param obj the object to compare with
     * @return {@code true} if the specified object is equal to this {@code RegistrationResult}; {@code false} otherwise
     */
    @Override
    public final boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        RegistrationResult rhs = (RegistrationResult) obj;
        return new EqualsBuilder()
                .append(wasSuccessfullyRegistered, rhs.wasSuccessfullyRegistered)
                .append(status, rhs.status)
                .append(message, rhs.message)
                .isEquals();
    }

    /**
     * Returns a hash code value for this {@code RegistrationResult}.
     *
     * @return a hash code computed from the registration result fields
     */
    @Override
    public final int hashCode() {
        return new HashCodeBuilder(15, 33)
                .append(wasSuccessfullyRegistered)
                .append(status)
                .append(message)
                .toHashCode();
    }

    /**
     * Returns a string representation of this {@code RegistrationResult}.
     *
     * @return a string representation of the registration result
     */
    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Returns a more human-friendly string representation of this {@code RegistrationResult}.
     *
     * @return a formatted string describing the registration result details
     */
    public final String toPrettyString() {
        return String.format("Successfully registered: %s%n", wasSuccessfullyRegistered) +
                String.format("Status: %s%n", status) +
                String.format("Message:%n%n%s%n", message);
    }

    /**
     * Creates an empty {@code RegistrationResult} instance.
     * <p>
     * An empty registration result represents the default state before any registration has been attempted.
     * </p>
     *
     * @return an empty {@code RegistrationResult} instance with a status of {@link RegistrationStatusEnums#EMPTY}
     */
    public static RegistrationResult createEmpty() {
        return new RegistrationResult(false, RegistrationStatusEnums.EMPTY);
    }

    /**
     * Checks whether this {@code RegistrationResult} is empty.
     *
     * @return {@code true} if this instance is equal to an empty registration result; {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.equals(createEmpty());
    }
}
