package com.coveros.training.persistence;

import com.coveros.training.helpers.ServletUtils;
import com.coveros.training.helpers.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet for managing database operations related to schema migration and cleaning.
 * <p>
 * This servlet processes HTTP GET requests and supports three actions:
 * <ul>
 *   <li>"clean": removes all data and schema from the database</li>
 *   <li>"migrate": applies the database schema without data</li>
 *   <li>default: cleans and then migrates the database to reset it</li>
 * </ul>
 * The result of the operation is forwarded to a result page.
 */
@WebServlet(name = "DbServlet", urlPatterns = {"/flyway"}, loadOnStartup = 1)
public class DbServlet extends HttpServlet {

    private static final long serialVersionUID = 1960160729302133928L;
    
    /**
     * The persistence layer used for database operations.
     */
    private final IPersistenceLayer pl;
    
    private static final String RESULT = "result";
    private static final Logger logger = LoggerFactory.getLogger(DbServlet.class);

    /**
     * Default constructor that creates a new PersistenceLayer.
     */
    public DbServlet() {
        pl = new PersistenceLayer();
    }

    /**
     * Constructs a DbServlet with a provided persistence layer.
     *
     * @param pl the persistence layer to use for database operations
     */
    public DbServlet(IPersistenceLayer pl) {
        this.pl = pl;
    }

    /**
     * Processes HTTP GET requests to perform database operations.
     * <p>
     * The servlet determines the action based on the "action" request parameter:
     * <ul>
     *   <li>"clean": cleans the database</li>
     *   <li>"migrate": migrates the database schema</li>
     *   <li>default: cleans and then migrates the database</li>
     * </ul>
     * After the operation, a result message is set as a request attribute and the request is forwarded.
     *
     * @param request  the HttpServletRequest object containing client request data
     * @param response the HttpServletResponse object used to send a response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        final String action = StringUtils.makeNotNullable(request.getParameter("action"));
        switch (action) {
            case "clean":
                logger.info("Received request to clean the database - remove all data and schema.");
                pl.cleanDatabase();
                request.setAttribute(RESULT, "cleaned");
                break;
            case "migrate":
                logger.info("Received request to migrate the database - add schema, but no data.");
                pl.migrateDatabase();
                request.setAttribute(RESULT, "migrated");
                break;
            default:
                logger.info("Received request to clean then migrate the database to a fresh state with no data.");
                pl.cleanAndMigrateDatabase();
                request.setAttribute(RESULT, "cleaned and migrated");
        }
        request.setAttribute("return_page", "library.html");
        ServletUtils.forwardToResult(request, response, logger);
    }
}
