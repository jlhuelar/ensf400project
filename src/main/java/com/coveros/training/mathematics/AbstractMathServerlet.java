package com.coveros.training.mathematics;

import com.coveros.training.helpers.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * An abstract base servlet for mathematical calculations.
 * Provides common functionality for parsing integer parameters, handling errors,
 * setting results, forwarding requests, and logger management.
 * Subclasses must implement the performCalculation method to define
 * the specific mathematical operation.
 */
public abstract class AbstractMathServlet extends HttpServlet {

    /**
     * The request attribute key for storing the calculation result or error message.
     */
    public static final String RESULT = "result";
    /**
     * The error message set when non-integer input is provided.
     */
    public static final String INTEGER_ERROR_MSG = "Error: only accepts integers";

    // Logger remains mutable for test injection.
    private static Logger logger = LoggerFactory.getLogger(AbstractMathServlet.class); // Default logger

    /**
     * Public accessor for the logger. Subclasses should use this.
     *
     * @return the logger instance
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Package-private setter for the logger, intended for testing.
     *
     * @param newLogger the new logger instance
     */
    static void setLogger(Logger newLogger) {
        logger = newLogger;
    }

    /**
     * Sets the logger for a specific subclass instance if needed,
     * though the static logger is generally used.
     * @param classLogger The logger specific to the subclass.
     */
    protected static void setSubclassLogger(Logger classLogger) {
        logger = classLogger;
    }

    /**
     * Retrieves an integer parameter from the request, sets it as a request attribute,
     * and returns its value.
     *
     * @param itemName the name of the request parameter to parse as an integer
     * @param request  the HttpServletRequest containing the parameter
     * @return the integer value of the parameter
     * @throws NumberFormatException if the parameter cannot be parsed as an integer
     */
    protected int putNumberInRequest(String itemName, HttpServletRequest request) throws NumberFormatException {
        int item = Integer.parseInt(request.getParameter(itemName));
        request.setAttribute(itemName, item); // Keep the input value in the request for the result page
        return item;
    }

    /**
     * Forwards the request and response to the standard result page.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     */
    protected void forwardToResult(HttpServletRequest request, HttpServletResponse response) {
        // Use the static logger from this base class for forwarding details
        ServletUtils.forwardToRestfulResult(request, response, getLogger());
    }

    /**
     * Handles POST requests, providing common structure for error handling and forwarding.
     * Delegates the specific calculation logic to the abstract performCalculation method.
     *
     * @param request  the HttpServletRequest
     * @param response the HttpServletResponse
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Delegate specific parameter parsing and calculation to subclass
            performCalculation(request);
        } catch (NumberFormatException ex) {
            getLogger().error("Invalid integer input received.", ex);
            request.setAttribute(RESULT, INTEGER_ERROR_MSG);
        } catch (Exception e) {
            // Catch unexpected errors during calculation
            getLogger().error("An unexpected error occurred during calculation.", e);
            request.setAttribute(RESULT, "Error: An internal error occurred.");
        }
        // Always forward to the result page
        forwardToResult(request, response);
    }

    /**
     * Abstract method to be implemented by subclasses.
     * This method should:
     * 1. Retrieve necessary parameters using getParameter or putNumberInRequest.
     * 2. Perform the specific mathematical calculation.
     * 3. Set the result (or a specific error message) in the request using request.setAttribute(RESULT, ...).
     * Note: NumberFormatExceptions from putNumberInRequest will be caught by doPost.
     *
     * @param request the HttpServletRequest
     * @throws NumberFormatException if parameter parsing fails (will be caught by doPost)
     * @throws Exception for any other calculation errors (will be caught by doPost)
     */
    protected abstract void performCalculation(HttpServletRequest request) throws NumberFormatException, Exception;

}