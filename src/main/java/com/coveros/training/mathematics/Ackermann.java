/**
 * Computes the Ackermann function.
 * <p>
 * This version is adapted from the implementation found at 
 * <a href="https://rosettacode.org/wiki/Ackermann_function#Java">Rosetta Code</a>.
 * </p>
 *
 * @param m the first parameter of the function
 * @param n the second parameter of the function
 * @return the computed Ackermann function value as a {@link BigInteger}
 */
public static BigInteger ack(BigInteger m, BigInteger n) {
    if (m.equals(BigInteger.ZERO)) {
        return n.add(BigInteger.ONE);
    }
    if (n.equals(BigInteger.ZERO)) {
        return ack(m.subtract(BigInteger.ONE), BigInteger.ONE);
    }
    return ack(m.subtract(BigInteger.ONE), ack(m, n.subtract(BigInteger.ONE)));
}

/**
 * A convenience method for computing the Ackermann function with integer arguments.
 * Internally, it converts the provided integers to {@link BigInteger} and calls {@link #ack(BigInteger, BigInteger)}.
 *
 * @param m the first parameter as an integer
 * @param n the second parameter as an integer
 * @return the computed Ackermann function value as a {@link BigInteger}
 */
public static BigInteger calculate(int m, int n) {
    BigInteger bigM = BigInteger.valueOf(m);
    BigInteger bigN = BigInteger.valueOf(n);
    return ack(bigM, bigN);
}
