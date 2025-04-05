package com.coveros.training.library;

import com.coveros.training.library.domainobjects.Book;
import com.coveros.training.helpers.ServletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A web API servlet that provides a list of books available for borrowing.
 * <p>
 * This servlet retrieves the available books from the library and forwards the result to a
 * restful result page.
 * </p>
 */
@MultipartConfig
@WebServlet(name = "LibraryBookListAvailableSearch", urlPatterns = {"/listavailable"}, loadOnStartup = 1)
public class LibraryBookListAvailableServlet extends HttpServlet {

    private static final long serialVersionUID = 3219972716578253134L;
    private static final Logger logger = LoggerFactory.getLogger(LibraryBookListAvailableServlet.class);
    public static final String RESULT = "result";
    static LibraryUtils libraryUtils = new LibraryUtils();

    /**
     * Processes HTTP GET requests to list all available books for borrowing.
     * <p>
     * The servlet retrieves the list of available books from the library, formats them as a JSON-like string,
     * and sets the result as a request attribute before forwarding to the restful result page.
     * </p>
     *
     * @param request  the HttpServletRequest containing the client request
     * @param response the HttpServletResponse used to send the response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        final List<Book> books = libraryUtils.listAvailableBooks();
        logger.info("Received request for all available books");
        String result;
        if (books.isEmpty()) {
            result = "No books exist in the database";
        } else {
            final String allBooks = books.stream()
                                         .map(Book::toOutputString)
                                         .collect(Collectors.joining(","));
            result = "[" + allBooks + "]";
        }
        request.setAttribute(RESULT, result);
        ServletUtils.forwardToRestfulResult(request, response, logger);
    }
}
