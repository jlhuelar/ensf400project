package com.coveros.training.mathematics;

import com.coveros.training.helpers.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;

/**
 * A servlet for computing Fibonacci numbers using various algorithms.
 * <p>
 * This servlet supports multiple algorithms for calculating the Fibonacci number:
 * <ul>
 *   <li><strong>tail_recursive_1</strong>: uses {@link FibonacciIterative#fibAlgo1(long)}</li>
 *   <li><strong>tail_recursive_2</strong>: uses {@link FibonacciIterative#fibAlgo2(int)}</li>
 *   <li><strong>default</strong>: uses the classic recursive implementation via {@link Fibonacci#calculate(long)}</li>
 * </ul>
 * The computed Fibonacci value is then set as a request attribute and forwarded to a result page.
 */
@MultipartConfig
@WebServlet(name = "FibServlet", urlPatterns = {"/fibonacci"}, loadOnStartup = 1)
public class FibServlet extends HttpServlet {

    private static final long serialVersionUID = 5290010004362186530L;
    /** The request attribute key for storing the Fibonacci calculation result. */
    public static final String RESULT = "result";

    /** The log message pattern for displaying the Fibonacci value. */
    public static final String FIBONACCI_VALUE_IS = "Fibonacci value is {}";
    public static  final Logger logger = LoggerFactory.getLogger(FibServlet.class);

    /**
     * Retrieves an integer parameter from the request, sets it as an attribute, and returns its value.
     *
     * @param itemName the name of the request parameter to parse as an integer
     * @param request  the HttpServletRequest containing the parameter
     * @return the integer value of the parameter
     * @throws NumberFormatException if the parameter cannot be parsed as an integer
     */
    private int putNumberInRequest(String itemName, HttpServletRequest request) {
        int item = Integer.parseInt(request.getParameter(itemName));
        request.setAttribute(itemName, item);
        return item;
    }

    /**
     * Processes POST requests to calculate a Fibonacci number.
     * <p>
     * Based on the "fib_algorithm_choice" parameter, the servlet delegates the computation to one of the following methods:
     * <ul>
     *   <li>{@link #tailRecursiveAlgo1Calc(HttpServletRequest, int)}</li>
     *   <li>{@link #tailRecursiveAlgo2Calc(HttpServletRequest, int)}</li>
     *   <li>{@link #defaultRecursiveCalculation(HttpServletRequest, int)}</li>
     * </ul>
     * If a non-integer value is provided, an error message is set.
     *
     * @param request  the HttpServletRequest containing the parameters
     * @param response the HttpServletResponse used to forward the result
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            int fibParamN = putNumberInRequest("fib_param_n", request);
            String algorithm = request.getParameter("fib_algorithm_choice");

            logger.info("Received request to calculate the {}th Fibonacci number using algorithm: {}", fibParamN, algorithm);

            if ("tail_recursive_1".equals(algorithm)) {
                tailRecursiveAlgo1Calc(request, fibParamN);
            } else if ("tail_recursive_2".equals(algorithm)) {
                tailRecursiveAlgo2Calc(request, fibParamN);
            } else {
                defaultRecursiveCalculation(request, fibParamN);
            }
        } catch (NumberFormatException ex) {
            request.setAttribute(RESULT, "Error: only accepts integers");
        }
        forwardToResult(request, response, logger);
    }

    /**
     * Computes the Fibonacci number using the iterative tail-recursive algorithm (version 2) and sets the result.
     *
     * @param request   the HttpServletRequest to set the result attribute
     * @param fibParamN the Fibonacci number index (n) to compute
     */
    void tailRecursiveAlgo2Calc(HttpServletRequest request, int fibParamN) {
        final BigInteger result = FibonacciIterative.fibAlgo2(fibParamN);
        logger.info(FIBONACCI_VALUE_IS, result);
        request.setAttribute(RESULT, result);
    }

    /**
     * Computes the Fibonacci number using the iterative tail-recursive algorithm (version 1) and sets the result.
     *
     * @param request   the HttpServletRequest to set the result attribute
     * @param fibParamN the Fibonacci number index (n) to compute
     */
    void tailRecursiveAlgo1Calc(HttpServletRequest request, int fibParamN) {
        final BigInteger result = FibonacciIterative.fibAlgo1(fibParamN);
        logger.info(FIBONACCI_VALUE_IS, result);
        request.setAttribute(RESULT, result);
    }

    /**
     * Forwards the request and response to the result page.
     * <p>
     * This method is wrapped to facilitate easier testing.
     *
     * @param request  the HttpServletRequest containing the result attribute
     * @param response the HttpServletResponse used for forwarding
     * @param logger   the logger for logging details during forwarding
     */
    void forwardToResult(HttpServletRequest request, HttpServletResponse response, Logger logger) {
        ServletUtils.forwardToRestfulResult(request, response, logger);
    }

    /**
     * Computes the Fibonacci number using the default recursive algorithm and sets the result.
     * <p>
     * The recursive algorithm is the classic implementation with exponential time complexity.
     *
     * @param request the HttpServletRequest to set the result attribute
     * @param n       the Fibonacci number index (n) to compute
     */
    void defaultRecursiveCalculation(HttpServletRequest request, int n) {
        final long result = Fibonacci.calculate(n);
        logger.info(FIBONACCI_VALUE_IS, result);
        request.setAttribute(RESULT, result);
    }
}
