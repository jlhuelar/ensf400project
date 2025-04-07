package com.coveros.training.mathematics;

/**
 * A utility class for computing Fibonacci numbers using a simple recursive approach.
 * <p>
 * This implementation uses the naive recursive method which has exponential time complexity.
 * </p>
 */
public class Fibonacci {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Fibonacci() {
        // Static utility class. Do not instantiate.
    }

    /**
     * Recursively computes the nth Fibonacci number.
     * <p>
     * Note: This naive recursive implementation has exponential time complexity and is not suitable for large {@code n}.
     * </p>
     *
     * @param n the index (starting from 0) of the Fibonacci sequence to compute
     * @return the nth Fibonacci number
     */
    public static long calculate(long n) {
        long result;
        if (n <= 1) {
            result = n;
        } else {
            result = calculate(n - 1) + calculate(n - 2);
        }
        return result;
    }
}
