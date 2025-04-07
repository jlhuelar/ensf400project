package com.coveros.training.mathematics;

/**
 * A functional interface for accessing a field value using an enum key.
 * <p>
 * Implementations of this interface provide a method to retrieve field values in an untyped manner,
 * while the default method {@code field} allows for a type-safe cast.
 * </p>
 *
 * @param <F> the enum type used as keys to access fields
 */
@FunctionalInterface
public interface FunctionalField<F extends Enum<?>> {

    /**
     * Retrieves the value associated with the given field.
     * <p>
     * This method returns an untyped object which should be cast appropriately.
     * </p>
     *
     * @param field the enum constant representing the field to retrieve
     * @return the value associated with the specified field as an {@code Object}
     */
    Object untypedField(F field);

    /**
     * Retrieves the value associated with the given field, cast to the desired type.
     * <p>
     * This default method calls {@link #untypedField(Enum)} and casts the result to the target type.
     * Use with caution as an incorrect type cast may result in a {@link ClassCastException}.
     * </p>
     *
     * @param field the enum constant representing the field to retrieve
     * @param <V>   the expected type of the field value
     * @return the value associated with the specified field cast to type {@code V}
     * @throws ClassCastException if the value cannot be cast to type {@code V}
     */
    @SuppressWarnings("unchecked")
    default <V> V field(F field) {
        return (V) untypedField(field);
    }
}
