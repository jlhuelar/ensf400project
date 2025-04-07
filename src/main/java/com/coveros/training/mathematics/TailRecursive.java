package com.coveros.training.mathematics;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Provides a utility for implementing tail-recursive algorithms in a functional style.
 * <p>
 * The {@code tailie} method takes in functions to create an intermediary state, update it,
 * test for termination, and transform the state into a result.
 * </p>
 */
public interface TailRecursive {

    /**
     * Implements a tail-recursive computation.
     * <p>
     * This method returns a function that, given two inputs, iteratively applies the provided
     * functions to generate an intermediary state, until a termination condition is met. Then, it transforms
     * the final state into a result.
     * </p>
     *
     * @param toIntermediary a function that creates an intermediary state from two inputs
     * @param unaryOperator  an operator that updates the intermediary state
     * @param predicate      a predicate to test whether the termination condition is met
     * @param toOutput       a function that converts the final intermediary state to the desired result
     * @param <M>            the type of the first input
     * @param <N>            the type of the second input
     * @param <I>            the type of the intermediary state
     * @param <O>            the type of the output result
     * @return a function that computes the tail-recursive result given the two inputs
     */
    static <M, N, I, O> BiFunction<M, N, O> tailie(
            BiFunction<M, N, I> toIntermediary,
            UnaryOperator<I> unaryOperator,
            Predicate<I> predicate,
            Function<I, O> toOutput) {
        return (input1, input2) ->
                $.epsilon(
                        Stream.iterate(
                                toIntermediary.apply(input1, input2),
                                unaryOperator
                        ),
                        predicate,
                        toOutput
                );
    }

    /**
     * An internal enum used as a namespace for the epsilon method.
     */
    enum $ {
        /**
         * A marker constant.
         */
        END;

        /**
         * Iterates through a stream until the predicate condition is met,
         * then applies a function to produce a result.
         *
         * @param stream    the stream of intermediary states
         * @param predicate a predicate that returns {@code true} when the desired state is reached
         * @param function  a function that converts the desired state to the final output
         * @param <I>       the type of the intermediary state
         * @param <O>       the type of the output result
         * @return the computed result of type {@code O}
         * @throws RuntimeException if no element in the stream satisfies the predicate
         */
        private static <I, O> O epsilon(Stream<I> stream, Predicate<I> predicate, Function<I, O> function) {
            return stream
                    .filter(predicate)
                    .map(function)
                    .findAny()
                    .orElseThrow(RuntimeException::new);
        }
    }
}
