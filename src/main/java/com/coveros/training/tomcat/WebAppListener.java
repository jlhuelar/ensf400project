package com.coveros.training.tomcat;

import com.coveros.training.persistence.IPersistenceLayer;
import com.coveros.training.persistence.PersistenceLayer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * The purpose of this class is to run certain commands at the start-up of the application.
 * <p>
 * It implements {@link ServletContextListener} to perform initialization tasks such as cleaning
 * and migrating the database.
 * </p>
 */
@WebListener
public class WebAppListener implements ServletContextListener {

    private final IPersistenceLayer pl;

    /**
     * Constructs a WebAppListener with a default persistence layer.
     */
    public WebAppListener() {
        pl = new PersistenceLayer();
    }

    /**
     * Constructs a WebAppListener with the specified persistence layer.
     *
     * @param pl the persistence layer to use for database operations during application startup
     */
    public WebAppListener(IPersistenceLayer pl) {
        this.pl = pl;
    }

    /**
     * Cleans and migrates the database using Flyway.
     * <p>
     * See database migration files like V2__Rest_of_tables_for_auth_and_library.sql.
     * </p>
     *
     * @param sce the ServletContextEvent triggered during application startup
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Clean the database and configure the schema.
        pl.cleanAndMigrateDatabase();
    }

    /**
     * This method is invoked when the servlet context is about to be shut down.
     * It currently does nothing.
     *
     * @param sce the ServletContextEvent triggered during application shutdown
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Do nothing.
    }
}
