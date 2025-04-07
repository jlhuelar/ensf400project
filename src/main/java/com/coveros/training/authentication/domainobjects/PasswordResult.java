package com.coveros.training.authentication.domainobjects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents the result of evaluating the complexity of a given password.
 * <p>
 * This includes details such as the calculated entropy, estimated times to crack the password
 * both offline and online, and an overall status indicating the quality or security of the password.
 * </p>
 * <p>
 * See {@link com.coveros.training.authentication.RegistrationUtils#isPasswordGood} for the calculation that generates this result.
 * </p>
 */
public final class PasswordResult {

    /**
     * The overall status of the password evaluation.
     */
    public final PasswordResultEnums status;

    /**
     * The calculated entropy of the password.
     */
    private final Double entropy;

    /**
     * The estimated time to crack the password in an offline attack.
     */
    public final String timeToCrackOffline;

    /**
     * The estimated time to crack the password in an online attack.
     */
    private final String timeToCrackOnline;

    /**
     * Additional message, for example a response from Nbvcxz, regarding the password complexity.
     */
    private final String message;

    private static final String BASIC_PASSWORD_CHECKS_FAILED = "BASIC_PASSWORD_CHECKS_FAILED";

    /**
     * Constructs a new {@code PasswordResult} with the specified details.
     *
     * @param status             the overall status of the password evaluation
     * @param entropy            the calculated entropy of the password
     * @param timeToCrackOffline the estimated time to crack the password offline
     * @param timeToCrackOnline  the estimated time to crack the password online
     * @param message            additional message or response regarding the password complexity
     */
    public PasswordResult(PasswordResultEnums status,
                          Double entropy,
                          String timeToCrackOffline,
                          String timeToCrackOnline,
                          String message) {
        this.status = status;
        this.entropy = entropy;
        this.timeToCrackOffline = timeToCrackOffline;
        this.timeToCrackOnline = timeToCrackOnline;
        this.message = message;
    }

    /**
     * Creates a default {@code PasswordResult} for cases when basic password validations fail,
     * such as when an empty password is provided.
     *
     * @param resultStatus the result status to assign
     * @return a default {@code PasswordResult} instance with zero entropy and empty time estimates,
     *         along with a basic failure message.
     */
    public static PasswordResult createDefault(PasswordResultEnums resultStatus) {
        return new PasswordResult(resultStatus, 0d, "", "", BASIC_PASSWORD_CHECKS_FAILED);
    }

    /**
     * Compares this {@code PasswordResult} with another object for equality.
     *
     * @param obj the object to compare with
     * @return {@code true} if the specified object is equal to this {@code PasswordResult}, {@code false} otherwise
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
        PasswordResult rhs = (PasswordResult) obj;
        return new EqualsBuilder()
                .append(status, rhs.status)
                .append(entropy, rhs.entropy)
                .append(timeToCrackOffline, rhs.timeToCrackOffline)
                .append(timeToCrackOnline, rhs.timeToCrackOnline)
                .append(message, rhs.message)
                .isEquals();
    }

    /**
     * Returns a hash code value for this {@code PasswordResult}.
     *
     * @return a hash code value computed from the password result fields
     */
    @Override
    public final int hashCode() {
        return new HashCodeBuilder(15, 33)
                .append(status)
                .append(entropy)
                .append(timeToCrackOffline)
                .append(timeToCrackOnline)
                .append(message)
                .toHashCode();
    }

    /**
     * Returns a string representation of this {@code PasswordResult}.
     *
     * @return a string representation of the password result
     */
    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Returns a more human-readable string representation of this {@code PasswordResult}.
     *
     * @return a formatted string describing the password result details
     */
    public final String toPrettyString() {
        return String.format("status: %s%n", status) +
                String.format("entropy: %s%n", entropy) +
                String.format("time to crack offline: %s%n", timeToCrackOffline) +
                String.format("time to crack online: %s%n", timeToCrackOnline) +
                String.format("Nbvcxz response: %s%n", message);
    }

    /**
     * Creates an empty {@code PasswordResult} instance.
     * <p>
     * An empty result is used to represent the absence of a password evaluation result,
     * typically to avoid using null.
     * </p>
     *
     * @return an empty {@code PasswordResult} instance
     */
    public static PasswordResult createEmpty() {
        return new PasswordResult(PasswordResultEnums.NULL, 0d, "", "", "");
    }

    /**
     * Checks whether this {@code PasswordResult} is empty.
     *
     * @return {@code true} if this {@code PasswordResult} is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.equals(createEmpty());
    }
}
