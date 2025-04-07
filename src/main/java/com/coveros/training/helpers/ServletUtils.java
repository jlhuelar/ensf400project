package com.coveros.training.helpers;

import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Helper methods to remove duplication in servlets.
 * <p>
 * These methods provide forwarding functionality for both standard and RESTful web API calls.
 * See the individual method documentation for more details.
 */
public class ServletUtils {

    /**
     * The JSP page for RESTful responses.
     */
    public static final String RESTFUL_RESULT_JSP = "restfulresult.jsp";

    /**
     * The JSP page for standard responses.
     */
    public static final String RESULT_JSP = "result.jsp";

    // Private constructor to prevent instantiation.
    private ServletUtils() {
        // using a private constructor to hide the implicit public one.
    }

    /**
     * Forwards the request and response to the standard result page.
     * <p>
     * This method is used when a user posts information in the regular web application.
     * See {@code result.jsp} for an example.
     *
     * @param request  the HttpServletRequest containing the request data
     * @param response the HttpServletResponse used to forward the request
     * @param logger   the Logger used to log any errors during forwarding
     */
    public static void forwardToResult(HttpServletRequest request, HttpServletResponse response, Logger logger) {
        try {
            request.getRequestDispatcher(RESULT_JSP).forward(request, response);
        } catch (Exception ex) {
            logger.error(String.format("Failed during forward: %s", ex));
        }
    }

    /**
     * Forwards the request and response to the RESTful result page.
     * <p>
     * This method is used when responding to RESTful web API calls.
     * See {@code restfulresult.jsp} for an example.
     *
     * @param request  the HttpServletRequest containing the request data
     * @param response the HttpServletResponse used to forward the request
     * @param logger   the Logger used to log any errors during forwarding
     */
    public static void forwardToRestfulResult(HttpServletRequest request, HttpServletResponse response, Logger logger) {
        try {
            request.getRequestDispatcher(RESTFUL_RESULT_JSP).forward(request, response);
        } catch (Exception ex) {
            logger.error(String.format("Failed during forward: %s", ex));
        }
    }
}
