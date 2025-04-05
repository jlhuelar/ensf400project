package com.coveros.training.mathematics;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A simple calculator class for performing basic arithmetic operations and
 * demonstrating various programming concepts.
 */
public class Calculator {

    private final Baz baz;

    /**
     * Constructs a Calculator with a default Baz dependency.
     */
    public Calculator() {
        this.baz = new Baz();
    }

    /**
     * Constructs a Calculator with the provided Baz dependency.
     *
     * @param baz the Baz instance to use for third-party operations
     */
    public Calculator(Baz baz) {
        this.baz = baz;
    }

    /**
     * Adds two integers.
     *
     * @param a the first integer operand
     * @param b the second integer operand
     * @return the sum of {@code a} and {@code b}
     */
    public static int add(int a, int b) {
        return a + b;
    }

    /**
     * Adds two doubles.
     *
     * @param a the first double operand
     * @param b the second double operand
     * @return the sum of {@code a} and {@code b}
     */
    public static double add(double a, double b) {
        return a + b;
    }

    /**
     * Converts an integer in the range 0 to 10 into its corresponding ordinal string.
     * For example: {@code 0} returns "zero".
     *
     * @param i the integer value to convert
     * @return the ordinal string representation of the number, or "dunno" if out of range
     */
    public static String toStringZeroToTen(int i) {
        switch (i) {
            case 0: return "zero";
            case 1: return "one";
            case 2: return "two";
            case 3: return "three";
            case 4: return "four";
            case 5: return "five";
            case 6: return "six";
            case 7: return "seven";
            case 8: return "eight";
            case 9: return "nine";
            case 10: return "ten";
            default: return "dunno";
        }
    }

    /**
     * Adds two pairs of integers.
     *
     * @param pair1 the first pair containing two integer values
     * @param pair2 the second pair containing two integer values
     * @return a new {@link Pair} containing the sums of the corresponding elements
     */
    public static Pair<Integer, Integer> add(Pair<Integer, Integer> pair1, Pair<Integer, Integer> pair2) {
        int newLeftValue = pair1.getLeft() + pair2.getLeft();
        int newRightValue = pair1.getRight() + pair2.getRight();
        return Pair.of(newLeftValue, newRightValue);
    }

    /**
     * Calculates a combined result using additional operations defined by {@link iFoo} and {@link iBar}.
     * <p>
     * This method is used for teaching purposes and testing stubs.
     * </p>
     *
     * @param a   the first integer operand
     * @param b   the second integer operand
     * @param foo an implementation of {@link iFoo} for performing a complex operation on {@code a}
     * @param bar an implementation of {@link iBar} for performing another complex operation on the result of {@code foo}
     * @return the sum of {@code a}, {@code b}, and the results from {@code foo} and {@code bar}
     */
    public static int calculateAndMore(int a, int b, iFoo foo, iBar bar) {
        int c = foo.doComplexThing(a);
        int d = bar.doOtherComplexThing(c);
        return a + b + c + d;
    }

    /**
     * Calculates a result by using the third-party operation provided by the {@link Baz} dependency.
     * <p>
     * This method is used for teaching purposes and testing stubs.
     * </p>
     *
     * @param a the integer input value
     * @return the sum of {@code a} and the result of the third-party operation
     */
    public int calculateAndMorePart2(int a) {
        int b = baz.doThirdPartyThing(a);
        return a + b;
    }

    /**
     * Executes a third-party operation provided by the {@link Baz} dependency.
     * <p>
     * This method is used for teaching purposes and testing mocks.
     * </p>
     *
     * @param a the integer input value for the third-party operation
     */
    public void calculateAndMorePart3(int a) {
