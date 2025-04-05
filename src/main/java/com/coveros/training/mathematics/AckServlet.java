package com.coveros.training.mathematics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;

/**
 * A servlet for computing the Ackermann function using different algorithms.
 * Extends AbstractMathServlet to reuse common servlet logic.
 */
@MultipartConfig
@WebServlet(name = "AckServlet", urlPatterns = {"/ackermann"}, loadOnStartup = 1)
public class AckServlet extends AbstractMathServlet {

    private static final long serialVersionUID = 5669410483481180165L;
    private static final Logger log = LoggerFactory.getLogger(AckServlet.class);

    // Static initializer block to set the logger in the base class for this specific servlet
    static {
        setSubclassLogger(log);
    }

    /**
     * Performs the Ackermann function calculation based on request parameters.
     * Implements the abstract method from AbstractMathServlet.
     *
     * @param request the HttpServletRequest
     * @throws NumberFormatException if ack_param_m or ack_param_n are not valid integers
     */
    @Override
    protected void performCalculation(HttpServletRequest request) throws NumberFormatException {
        // Parameter parsing now throws NumberFormatException, caught by base class doPost
        int ackParamM = putNumberInRequest("ack_param_m", request);
        int ackParamN = putNumberInRequest("ack_param_n", request);
        String algorithm = request.getParameter("ack_algorithm_choice");

        log.info("Received request to calculate Ackermann's function with parameters {} and {} using the {} algorithm",
                ackParamM, ackParamN, (algorithm == null ? "default" : algorithm) ); // Added null check for logging

        // Select algorithm and calculate
        if ("tail_recursive".equals(algorithm)) {
            tailRecursive(request, ackParamM, ackParamN);
        } else {
            regularRecursive(request, ackParamM, ackParamN);
        }
    }

    /**
     * Computes the Ackermann function using the regular recursive implementation.
     */
    private void regularRecursive(HttpServletRequest request, int itemA, int itemB) {
        // Beware: Ackermann grows extremely fast, prone to StackOverflowError or long computation times
        final BigInteger result = Ackermann.calculate(itemA, itemB);
        log.info("Ackermann's function result (regular recursive) is {}", result);
        request.setAttribute(RESULT, result); // Set result using the constant from base class
    }

    /**
     * Computes the Ackermann function using the iterative tail-recursive implementation.
     */
    private void tailRecursive(HttpServletRequest request, int itemA, int itemB) {
         // Beware: Ackermann grows extremely fast, iterative version avoids StackOverflow but can still consume much time/memory
        final BigInteger result = AckermannIterative.calculate(itemA, itemB);
        log.info("Ackermann's function result (tail-recursive) is {}", result);
        request.setAttribute(RESULT, result); // Set result using the constant from base class
    }
}