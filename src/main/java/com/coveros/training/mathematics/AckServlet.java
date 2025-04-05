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
 * A servlet for computing the Ackermann function.
 * <p>
 * This servlet supports two algorithms: the regular recursive implementation and an iterative tail-recursive approach.
 * Depending on the "ack_algorithm_choice" parameter provided in the POST request, it invokes either the regular
 * or tail-recursive computation.
 * </p>
 * <p>
 * It expects the following parameters:
 * <ul>
 *   <li><code>ack_param_m</code>: an integer value for the first parameter</li>
 *   <li><code>ack_param_n</code>: an integer value for the second parameter</li>
 *   <li><code>ack_algorithm_choice</code>: either "tail_recursive" or another value to select the algorithm</li>
 * </ul>
 * </p>
 */
@MultipartConfig
@WebServlet(name = "AckServlet", urlPatterns = {"/ackermann"}, loadOnStartup = 1)
public class AckServlet extends HttpServlet {

    private static final long serialVersionUID = 5669410483481180165L;
    public static final String RESULT = "result";
    private static final Logger logger = LoggerFactory.getLogger(AckServlet.class);

    /**
     * Retrieves an integer parameter from the request, sets it as an attribute, and returns its value.
     *
     * @param itemName the name of the request parameter to parse as an integer
     * @param request  the HttpServletRequest object containing the parameter
     * @return the integer value of the parameter
     * @throws NumberFormatException if the parameter cannot be parsed as an integer
     */
    private int putNumberInRequest(String itemName, HttpServletRequest request) {
        int item = Integer.parseInt(request.getParameter(itemName));
        request.setAttribute(itemName, item);
        return item;
    }

    /**
     * Processes POST requests to compute the Ackermann function.
     * <p>
     * Depending on the "ack_algorithm_choice" parameter, this method either invokes the tail-recursive or regular
     * recursive implementation.
     * </p>
     *
     * @param request  the HttpServletRequest containing the parameters
     * @param response the HttpServletResponse used to forward the result
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            int ackParamM = putNumberInRequest("ack_param_m", request);
            int ackParamN = putNumberInRequest("ack_param_n", request);
            String algorithm = request.getParameter("ack_algorithm_choice");

            logger.info("Received request to calculate Ackermann's function with parameters {} and {} using the {} algorithm", ackParamM, ackParamN, algorithm);

            if ("tail_recursive".equals(algorithm)) {
                tailRecursive(request, ackParamM, ackParamN);
            } else {
                regularRecursive(request, ackParamM, ackParamN);
            }

        } catch (NumberFormatException ex) {
            request.setAttribute(RESULT, "Error: only accepts integers");
        }
        forwardToResult(request, response, logger);
    }

    /**
     * Forwards the request and response to a result page.
     * <p>
     * This method wraps a static method call for testing purposes.
     * </p>
     *
     * @param request  the HttpServletRequest containing the result attribute
     * @param response the HttpServletResponse used for forwarding
     * @param logger   the logger used for logging during forwarding
     */
    void forwardToResult(HttpServletRequest request, HttpServletResponse response, Logger logger) {
        ServletUtils.forwardToRestfulResult(request, response, logger);
    }

    /**
     * Computes the Ackermann function using the regular recursive implementation.
     * <p>
     * The result is logged and set as a request attribute.
     * </p>
     *
     * @param request the HttpServletRequest used to set the result attribute
     * @param itemA   the first parameter for the Ackermann function
     * @param itemB   the second parameter for the Ackermann function
     */
    void regularRecursive(HttpServletRequest request, int itemA, int itemB) {
        final BigInteger result = Ackermann.calculate(itemA, itemB);
        logger.info("Ackermann's function result (regular recursive) is {}", result);
        request.setAttribute(RESULT, result);
    }

    /**
     * Computes the Ackermann function using the iterative tail-recursive implementation.
     * <p>
     * The result is logged and set as a request attribute.
     * </p>
     *
     * @param request the HttpServletRequest used to set the result attribute
     * @param itemA   the first parameter for the Ackermann function
     * @param itemB   the second parameter for the Ackermann function
     */
    void tailRecursive(HttpServletRequest request, int itemA, int itemB) {
        final BigInteger result = AckermannIterative.calculate(itemA, itemB);
        logger.info("Ackermann's function result (tail-recursive) is {}", result);
        request.setAttribute(RESULT, result);
    }
}
