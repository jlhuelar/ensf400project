package com.coveros.training.mathematics;

import com.coveros.training.helpers.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet for performing simple mathematical operations.
 * <p>
 * This servlet processes HTTP POST requests by retrieving two integer parameters,
 * adding them using {@link Calculator#add(int, int)}, and forwarding the result.
 */
@MultipartConfig
@WebServlet(name = "MathServlet", urlPatterns = {"/math"}, loadOnStartup = 1)
public class MathServlet extends HttpServlet {

    private static final long serialVersionUID = 1766696864489619658L;
    static Logger logger = LoggerFactory.getLogger(MathServlet.class);

    private int putNumberInRequest(String itemName, HttpServletRequest request) {
        int item = Integer.parseInt(request.getParameter(itemName));
        request.setAttribute(itemName, item);
        return item;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            int itemA = putNumberInRequest("item_a", request);
            int itemB = putNumberInRequest("item_b", request);

            logger.info("Received request to add two numbers, {} and {}", itemA, itemB);

            setResultToSum(request, itemA, itemB);
        } catch (NumberFormatException ex) {
            request.setAttribute("result", "Error: only accepts integers");
        }
        forwardToResult(request, response, logger);
    }

    /**
     * Forwards the request and response to the result page.
     * <p>
     * This method wraps a static method call for testing purposes.
     * </p>
     *
     * @param request  the HttpServletRequest containing the result attribute
     * @param response the HttpServletResponse used for forwarding
     * @param logger   the Logger used for logging during forwarding
     */
    void forwardToResult(HttpServletRequest request, HttpServletResponse response, Logger logger) {
        ServletUtils.forwardToRestfulResult(request, response, logger);
    }

    /**
     * Computes the sum of two integers and sets the result as a request attribute.
     * <p>
     * The result is computed using {@link Calculator#add(int, int)}.
     * </p>
     *
     * @param request the HttpServletRequest used to set the result attribute
     * @param itemA   the first integer value
     * @param itemB   the second integer value
     */
    void setResultToSum(HttpServletRequest request, int itemA, int itemB) {
        final int result = Calculator.add(itemA, itemB);
        request.setAttribute("result", result);
    }
}
