package com.coveros.training.persistence;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A POJO container for parameters used by the {@link SqlData} object.
 * <p>
 * This class holds the data to be injected into SQL statements along with its corresponding type.
 *
 * @param <T> the type of the parameter
 */
public final class ParameterObject<T> {

    /**
     * The data being injected into the SQL statement.
     */
    final Object data;

    /**
     * The type of the data (e.g. Integer, String, etc.).
     */
    final Class<T> type;

    ParameterObject(Object data, Class<T> type) {
        this.data = data;
        this.type = type;
    }

    /**
     * Checks if this ParameterObject is equal to another object.
     *
     * @param obj the object to compare with
     * @return {@code true} if the objects are equal; {@code false} otherwise
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
        ParameterObject<?> rhs = (ParameterObject<?>) obj;
        return new EqualsBuilder()
                .append(data, rhs.data)
                .append(type, rhs.type)
                .isEquals();
    }

    /**
     * Computes a hash code for this ParameterObject.
     *
     * @return a hash code value for this object
     */
    public final int hashCode() {
        return new HashCodeBuilder(63, 7)
                .append(data)
                .append(type)
                .toHashCode();
    }

    /**
     * Creates an empty ParameterObject.
     * <p>
     * This is used to represent an absence of a parameter, avoiding the use of {@code null}.
     *
     * @return an empty ParameterObject instance
     */
    public static ParameterObject<Void> createEmpty() {
        return new ParameterObject<>("", Void.class);
    }

    /**
     * Determines whether this ParameterObject is empty.
     *
     * @return {@code true} if this instance is equal to an empty ParameterObject; {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.equals(ParameterObject.createEmpty());
    }

    /**
     * Returns a string representation of this ParameterObject.
     *
     * @return a string representation of this object
     */
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
