package com.coveros.training.mathematics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

/**
 * A servlet for computing Fibonacci numbers using various algorithms.
 * Extends AbstractMathServlet to reuse common servlet logic.
 */
@MultipartConfig
@WebServlet(name = "FibServlet", urlPatterns = {"/fibonacci"}, loadOnStartup = 1)
public class FibServlet extends AbstractMathServlet {

    private static final long serialVersionUID = 5290010004362186530L;
    private static final Logger log = LoggerFactory.getLogger(FibServlet.class);

    // Static initializer block to set the logger in the base class for this specific servlet
    static {
        setSubclassLogger(log);
    }

    /** The log message pattern for displaying the Fibonacci value. */
    public static final String FIBONACCI_VALUE_IS = "Fibonacci value is {}";

    /**
     * Performs the Fibonacci calculation based on request parameters.
     * Implements the abstract method from AbstractMathServlet.
     *
     * @param request the HttpServletRequest
     * @throws NumberFormatException if fib_param_n is not a valid integer
     */
    @Override
    protected void performCalculation(HttpServletRequest request) throws NumberFormatException {
        // Parameter parsing now throws NumberFormatException, caught by base class doPost
        int fibParamN = putNumberInRequest("fib_param_n", request);
        String algorithm = request.getParameter("fib_algorithm_choice");

        log.info("Received request to calculate the {}th Fibonacci number using algorithm: {}", fibParamN, algorithm);

        // Select algorithm and calculate
        if ("tail_recursive_1".equals(algorithm)) {
            tailRecursiveAlgo1Calc(request, fibParamN);
        } else if ("tail_recursive_2".equals(algorithm)) {
            tailRecursiveAlgo2Calc(request, fibParamN);
        } else {
            defaultRecursiveCalculation(request, fibParamN);
        }
    }

    /**
     * Computes the Fibonacci number using the iterative tail-recursive algorithm (version 2).
     */
    private void tailRecursiveAlgo2Calc(HttpServletRequest request, int fibParamN) {
        final BigInteger result = FibonacciIterative.fibAlgo2(fibParamN);
        log.info(FIBONACCI_VALUE_IS, result);
        request.setAttribute(RESULT, result); // Set result using the constant from base class
    }

    /**
     * Computes the Fibonacci number using the iterative tail-recursive algorithm (version 1).
     */
    private void tailRecursiveAlgo1Calc(HttpServletRequest request, int fibParamN) {
        final BigInteger result = FibonacciIterative.fibAlgo1(fibParamN);
        log.info(FIBONACCI_VALUE_IS, result);
        request.setAttribute(RESULT, result); // Set result using the constant from base class
    }

    /**
     * Computes the Fibonacci number using the default recursive algorithm.
     */
    private void defaultRecursiveCalculation(HttpServletRequest request, int n) {
        // Consider potential performance issues or stack overflows for large 'n'
        final long result = Fibonacci.calculate(n);
        log.info(FIBONACCI_VALUE_IS, result);
        request.setAttribute(RESULT, result); // Set result using the constant from base class
    }
}