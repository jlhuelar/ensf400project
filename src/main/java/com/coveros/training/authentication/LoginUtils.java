package com.coveros.training.authentication;

import com.coveros.training.helpers.CheckUtils;
import com.coveros.training.persistence.IPersistenceLayer;
import com.coveros.training.persistence.PersistenceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides business-layer functionality to verify whether the entered credentials are valid for authentication.
 * <p>
 * This class checks the user credentials against the persistence layer and logs the outcome.
 * </p>
 */
public class LoginUtils {

    private static final Logger logger = LoggerFactory.getLogger(LoginUtils.class);
    private final IPersistenceLayer persistenceLayer;

    /**
     * Constructs a LoginUtils instance with the provided persistence layer.
     *
     * @param persistenceLayer the persistence layer used to validate credentials
     */
    public LoginUtils(IPersistenceLayer persistenceLayer) {
        this.persistenceLayer = persistenceLayer;
    }

    /**
     * Constructs a LoginUtils instance with a default persistence layer.
     */
    public LoginUtils() {
        this(new PersistenceLayer());
    }

    /**
     * Determines whether the given username and password correspond to a registered user.
     * <p>
     * The method validates that neither parameter is null or empty, then delegates the credential
     * validation to the persistence layer.
     * </p>
     *
     * @param username the username to validate
     * @param password the password to validate
     * @return {@code true} if the credentials are valid, {@code false} otherwise
     * @throws IllegalArgumentException if either the username or password is null or empty
     */
    public boolean isUserRegistered(String username, String password) {
        CheckUtils.StringMustNotBeNullOrEmpty(username, password);
        logger.info("Checking if credentials for {} are valid for login", username);
        boolean isValid = persistenceLayer.areCredentialsValid(username, password).orElse(false);
        if (isValid) {
            logger.info("Credentials for {} are valid - granting access", username);
        } else {
            logger.info("Credentials for {} were invalid - denying access", username);
        }
        return isValid;
    }

    /**
     * Creates a LoginUtils instance with an empty persistence layer.
     *
     * @return a new LoginUtils instance backed by an empty persistence layer
     */
    public static LoginUtils createEmpty() {
        return new LoginUtils(PersistenceLayer.createEmpty());
    }

    /**
     * Checks whether the underlying persistence layer is empty.
     *
     * @return {@code true} if the persistence layer is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return persistenceLayer.isEmpty();
    }
}
