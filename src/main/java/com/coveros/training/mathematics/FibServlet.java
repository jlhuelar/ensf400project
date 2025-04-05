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
 * Servlet for processing Fibonacci number calculation requests.
 */
@MultipartConfig
@WebServlet(name = "FibServlet", urlPatterns = {"/fibonacci"}, loadOnStartup = 1)
public class FibServlet extends HttpServlet {

    private static final long serialVersionUID = 5290010004362186530L;

    /** Request attribute key for storing the result of the calculation. */
    public static final String RESULT = "result";

    /** Log message template for displaying the Fibonacci calculation result. */
    public static final String FIBONACCI_VALUE_IS = "Fibonacci value is {}";

    static Logger logger = LoggerFactory.getLogger(FibServlet.class);

    /**
     * Helper method to parse an integer parameter from the request and set it as an attribute.
     *
     * @param itemName the name of the request parameter
     * @param request  the HttpServletRequest object
     * @return the parsed integer value
     */
    private int putNumberInRequest(String itemName, HttpServletRequest request) {
        int item = Integer.parseInt(request.getParameter(itemName));
        request.setAttribute(itemName, item);
        return item;
    }

    /**
     * Handles POST requests to calculate the Fibonacci number.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            int fibParamN = putNumberInRequest("fib_param_n", request);
            String algorithm = request.getParameter("fib_algorithm_choice");

            logger.info("received request to calculate the {}th fibonacci number by {}", fibParamN, algorithm);

            if (algorithm.equals("tail_recursive_1")) {
                tailRecursiveAlgo1Calc(request, fibParamN);
            } else if (algorithm.equals("tail_recursive_2")) {
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
     * Calculates the Fibonacci number using the tail recursive algorithm (version 2).
     *
     * @param request   the HttpServletRequest object
     * @param fibParamN the nth Fibonacci number to calculate
     */
    void tailRecursiveAlgo2Calc(HttpServletRequest request, int fibParamN) {
        final BigInteger result = FibonacciIterative.fibAlgo2(fibParamN);
        logger.info(FIBONACCI_VALUE_IS, result);
        request.setAttribute(RESULT, result);
    }

    /**
     * Calculates the Fibonacci number using the tail recursive algorithm (version 1).
     *
     * @param request   the HttpServletRequest object
     * @param fibParamN the nth Fibonacci number to calculate
     */
    void tailRecursiveAlgo1Calc(HttpServletRequest request, int fibParamN) {
        final BigInteger result = FibonacciIterative.fibAlgo1(fibParamN);
        logger.info(FIBONACCI_VALUE_IS, result);
        request.setAttribute(RESULT, result);
    }

    /**
     * Forwards the request to the result page.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param logger   the Logger instance
     */
    void forwardToResult(HttpServletRequest request, HttpServletResponse response, Logger logger) {
        ServletUtils.forwardToRestfulResult(request, response, logger);
    }

    /**
     * Calculates the Fibonacci number using the default recursive*
