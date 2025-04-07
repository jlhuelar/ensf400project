package com.coveros.training.library;

import com.coveros.training.library.domainobjects.Borrower;
import com.coveros.training.helpers.ServletUtils;
import com.coveros.training.helpers.StringUtils;
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
 * A web API servlet to list all borrowers or search for borrowers by id or name.
 * <p>
 * This servlet processes HTTP GET requests. If no search criteria are provided,
 * it returns all borrowers. If either an id or a name is provided, it performs
 * the respective search. If both are provided, an error message is returned.
 */
@MultipartConfig
@WebServlet(name = "LibraryBorrowerListSearch", urlPatterns = {"/borrower"}, loadOnStartup = 1)
public class LibraryBorrowerListSearchServlet extends HttpServlet {

    private static final long serialVersionUID = -7374339112812653844L;
    private static final Logger logger = LoggerFactory.getLogger(LibraryBorrowerListSearchServlet.class);

    /**
     * The request attribute key for storing the result of the borrower search.
     */
    public static final String RESULT = "result";

    static LibraryUtils libraryUtils = new LibraryUtils();

    /**
     * Processes HTTP GET requests to search for borrowers.
     * <p>
     * The search criteria are determined by the request parameters "id" and "name".
     * <ul>
     *   <li>If both are empty, all borrowers are listed.</li>
     *   <li>If only "id" is provided, a search by id is performed.</li>
     *   <li>If only "name" is provided, a search by name is performed.</li>
     *   <li>If both are provided, an error message is returned.</li>
     * </ul>
     *
     * @param request  the HttpServletRequest containing the search parameters
     * @param response the HttpServletResponse used to send the result
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        final String idString = StringUtils.makeNotNullable(request.getParameter("id"));
        final String name = StringUtils.makeNotNullable(request.getParameter("name"));

        String result;
        if (idString.isEmpty() && name.isEmpty()) {
            result = listAllBorrowers();
        } else if (!idString.isEmpty() && name.isEmpty()) {
            result = searchById(idString);
        } else if (idString.isEmpty()) {
            result = searchByName(name);
        } else {  // both id and name have an input
            logger.info("Received request for borrowers by both id and name - id {} and name {}", idString, name);
            result = "Error: please search by either name or id, not both";
        }
        request.setAttribute(RESULT, result);

        ServletUtils.forwardToRestfulResult(request, response, logger);
    }

    /**
     * Searches for a borrower by name.
     *
     * @param name the name of the borrower to search for
     * @return a JSON-like string representation of the borrower if found, or an error message if not found
     */
    private String searchByName(final String name) {
        logger.info("Received request to search for borrower by name: {}", name);
        final Borrower borrower = libraryUtils.searchForBorrowerByName(name);
        if (borrower.isEmpty()) {
            return "No borrowers found with a name of " + name;
        }
        return "[" + borrower.toOutputString() + "]";
    }

    /**
     * Searches for a borrower by id.
     *
     * @param idString the id of the borrower to search for as a String
     * @return a JSON-like string representation of the borrower if found, or an error message if not found
     */
    private String searchById(final String idString) {
        logger.info("Received request to search for borrower by id: {}", idString);
        int id;
        try {
            id = Integer.parseInt(idString);
        } catch (final NumberFormatException ex) {
            return "Error: could not parse the borrower id as an integer";
        }
        final Borrower borrower = libraryUtils.searchForBorrowerById(id);
        if (borrower.isEmpty()) {
            return "No borrowers found with an id of " + idString;
        }
        return "[" + borrower.toOutputString() + "]";
    }

    /**
     * Lists all borrowers registered in the library.
     *
     * @return a JSON-like string representation of all borrowers, or a message if none exist
     */
    private String listAllBorrowers() {
        logger.info("Received request to list all borrowers");
        final List<Borrower> borrowers = libraryUtils.listAllBorrowers();
        final String allBorrowers = borrowers.stream()
                                             .map(Borrower::toOutputString)
                                             .collect(Collectors.joining(","));
        if (allBorrowers.isEmpty()) {
            return "No borrowers exist in the database";
        }
        return "[" + allBorrowers + "]";
    }
}
