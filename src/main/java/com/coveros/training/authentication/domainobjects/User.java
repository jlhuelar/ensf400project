package com.coveros.training.authentication.domainobjects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents the data that we consider full and complete to define a particular user.
 * <p>
 * This data structure corresponds to the details stored in the database.
 * </p>
 */
public final class User {

    /**
     * The username of the user.
     */
    public final String name;

    /**
     * The identifier of the user in the database.
     */
    public final long id;

    /**
     * Constructs a new User with the specified name and id.
     *
     * @param name the username of the user
     * @param id   the unique identifier of the user in the database
     */
    public User(String name, long id) {
        this.name = name;
        this.id = id;
    }

    /**
     * Compares this User with the specified object for equality.
     *
     * @param obj the object to compare with
     * @return {@code true} if this User is equal to the specified object, {@code false} otherwise
     */
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
        User rhs = (User) obj;
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(name, rhs.name)
                .isEquals();
    }

    /**
     * Returns a hash code value for this User.
     *
     * @return a hash code computed from the user's name and id
     */
    public final int hashCode() {
        return new HashCodeBuilder(19, 3)
                .append(name)
                .append(id)
                .toHashCode();
    }

    /**
     * Returns a string representation of this User.
     *
     * @return a string representation of this User
     */
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates an empty User instance.
     * <p>
     * An empty User is defined as a User with an empty name and an id of 0.
     * </p>
     *
     * @return an empty User instance
     */
    public static User createEmpty() {
        return new User("", 0);
    }

    /**
     * Checks whether this User is empty.
     * <p>
     * A User is considered empty if it is equal to the User returned by {@link #createEmpty()}.
     * </p>
     *
     * @return {@code true} if this User is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.equals(User.createEmpty());
    }
}
