package com.coveros.training.mathematics;

import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An interface for an iterative implementation of the Ackermann function using a tail-recursive approach.
 * <p>
 * This implementation is adapted from the version available at 
 * <a href="https://rosettacode.org/wiki/Ackermann_function#Java">Rosetta Code</a>.
 * </p>
 */
public interface AckermannIterative {

    /**
     * Calculates the Ackermann function for the given integer parameters.
     *
     * @param m the first parameter as an integer
     * @param n the second parameter as an integer
     * @return the computed Ackermann function value as a {@link BigInteger}
     */
    static BigInteger calculate(int m, int n) {
        BigInteger bigM = BigInteger.valueOf(m);
        BigInteger bigN = BigInteger.valueOf(n);
        return AckTailRecursion.main(bigM, bigN);
    }

    /**
     * Retrieves the first numeric value used in the tail-recursive state.
     *
     * @return the first {@link BigInteger} value
     */
    BigInteger number1();

    /**
     * Retrieves the second numeric value used in the tail-recursive state.
     *
     * @return the second {@link BigInteger} value
     */
    BigInteger number2();

    /**
     * Returns the stack used to maintain state during the tail-recursive computation.
     *
     * @return a {@link Deque} containing {@link BigInteger} values representing the stack
     */
    Deque<BigInteger> stack();

    /**
     * Indicates whether the tail-recursive process is in a specific state.
     *
     * @return {@code true} if a flag condition is set; {@code false} otherwise
     */
    boolean flag();

    /**
     * The enum that encapsulates the tail-recursive implementation of the Ackermann function.
     */
    enum AckTailRecursion {
        END;

        // Predefined constants for arithmetic operations.
        private static final BigInteger ZERO = BigInteger.ZERO;
        private static final BigInteger ONE = BigInteger.ONE;
        private static final BigInteger TWO = BigInteger.valueOf(2);
        private static final BigInteger THREE = BigInteger.valueOf(3);

        /**
         * Creates a tail-recursive state from the provided parameters.
         *
         * @param number1 the first number
         * @param number2 the second number
         * @param stack   the current state stack
         * @param flag    the flag indicating a specific state condition
         * @return an instance of {@link AckermannIterative} representing the current state
         */
        private static AckermannIterative tail(BigInteger number1, BigInteger number2, Deque<BigInteger> stack, boolean flag) {
            return (FunctionalAckermann) field -> {
                switch (field) {
                    case NUMBER_1:
                        return number1;
                    case NUMBER_2:
                        return number2;
                    case STACK:
                        return stack;
                    case FLAG:
                        return flag;
                    default:
                        throw new UnsupportedOperationException(
                                field instanceof Field
                                        ? "Field checker has not been updated properly."
                                        : "Field is not of the correct type."
                        );
                }
            };
        }

        /**
         * A BinaryOperator that encapsulates the tail-recursive Ackermann calculation.
         * <p>
         * The operator uses a tail-recursive strategy (via the {@code TailRecursive.tailie} helper method)
         * to iteratively compute the Ackermann function.
         * </p>
         */
        private static final BinaryOperator<BigInteger> ACKERMANN =
                TailRecursive.tailie(
                        // Initial state creation lambda:
                        (BigInteger number1, BigInteger number2) ->
                                tail(
                                        number1,
                                        number2,
                                        Stream.of(number1)
                                                .collect(Collectors.toCollection(ArrayDeque::new)),
                                        false
                                ),
                        // Recursive step lambda:
                        ackermann -> {
                            BigInteger number1 = ackermann.number1();
                            BigInteger number2 = ackermann.number2();
                            Deque<BigInteger> stack = ackermann.stack();
                            // If there is a saved state and the flag is false, restore number1 from the stack.
                            if (!stack.isEmpty() && !ackermann.flag()) {
                                number1 = stack.pop();
                            }
                            // Handle different cases based on the value of number1.
                            switch (number1.intValue()) {
                                case 0:
                                    return tail(
                                            number1,
                                            number2.add(ONE),
                                            stack,
                                            false
                                    );
                                case 1:
                                    return tail(
                                            number1,
                                            number2.add(TWO),
                                            stack,
                                            false
                                    );
                                case 2:
                                    return tail(
                                            number1,
                                            number2.multiply(TWO).add(THREE),
                                            stack,
                                            false
                                    );
                                default:
                                    if (ZERO.equals(number2)) {
                                        return tail(
                                                number1.subtract(ONE),
                                                ONE,
                                                stack,
                                                true
                                        );
                                    } else {
                                        // Save state and decrement number2
                                        stack.push(number1.subtract(ONE));
                                        return tail(
                                                number1,
                                                number2.subtract(ONE),
                                                stack,
                                                true
                                        );
                                    }
                            }
                        },
                        // Termination condition lambda: finish when the stack is empty.
                        ackermann -> ackermann.stack().isEmpty(),
                        // Final transformation lambda: extract the result from the tail-recursive state.
                        AckermannIterative::number2
                )::apply;

        /**
         * The main entry point for the tail-recursive Ackermann function calculation.
         *
         * @param m the first parameter as a {@link BigInteger}
         * @param n the second parameter as a {@link BigInteger}
         * @return the computed Ackermann function value as a {@link BigInteger}
         */
        private static BigInteger main(BigInteger m, BigInteger n) {
            return ACKERMANN.apply(m, n);
        }

        /**
         * The available fields in the tail-recursive state.
         */
        private enum Field {
            NUMBER_1,
            NUMBER_2,
            STACK,
            FLAG
        }

        /**
         * A functional interface combining {@link FunctionalField} and {@link AckermannIterative} to support
         * the extraction of state fields.
         */
        @FunctionalInterface
        private interface FunctionalAckermann extends FunctionalField<Field>, AckermannIterative {
            @Override
            default BigInteger number1() {
                return field(Field.NUMBER_1);
            }

            @Override
            default BigInteger number2() {
                return field(Field.NUMBER_2);
            }

            @Override
            default Deque<BigInteger> stack() {
                return field(Field.STACK);
            }

            @Override
            default boolean flag() {
                return field(Field.FLAG);
            }
        }
    }
}
