package com.coveros.training.helpers;

/**
 * This exception is thrown whenever an invariant is expected to be true but is not.
 * See <a href="https://en.wikipedia.org/wiki/Invariant_(mathematics)#Invariants_in_computer_science">Invariants in Computer Science</a>.
 */
public class AssertionException extends RuntimeException {

    /**
     * Constructs a new AssertionException with the specified detail message.
     *
     * @param message the detail message explaining the invariant failure
     */
    public AssertionException(String message) {
        super(message);
    }
}
