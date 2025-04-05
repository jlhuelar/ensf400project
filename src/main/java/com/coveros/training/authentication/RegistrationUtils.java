package com.coveros.training.authentication;

import com.coveros.training.authentication.domainobjects.PasswordResult;
import com.coveros.training.authentication.domainobjects.RegistrationResult;
import com.coveros.training.helpers.CheckUtils;
import com.coveros.training.persistence.IPersistenceLayer;
import com.coveros.training.persistence.PersistenceLayer;
import me.gosimple.nbvcxz.Nbvcxz;
import me.gosimple.nbvcxz.scoring.Result;
import me.gosimple.nbvcxz.scoring.TimeEstimate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.coveros.training.authentication.domainobjects.PasswordResultEnums.EMPTY_PASSWORD;
import static com.coveros.training.authentication.domainobjects.PasswordResultEnums.*;
import static com.coveros.training.authentication.domainobjects.RegistrationStatusEnums.*;
import static com.coveros.training.helpers.CheckUtils.StringMustNotBeNullOrEmpty;

/**
 * Provides business logic for processing user registration.
 * <p>
 * The registration process includes:
 * <ol>
 *   <li>Verifying that the username and password are not null or empty.</li>
 *   <li>Checking if the user already exists in the database.</li>
 *   <li>Evaluating the password complexity using the Nbvcxz library.</li>
 *   <li>Saving the new user's credentials if all checks pass.</li>
 * </ol>
 */
public class RegistrationUtils {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationUtils.class);
    private final IPersistenceLayer persistenceLayer;

    /**
     * Constructs a RegistrationUtils instance using the specified persistence layer.
     *
     * @param persistenceLayer the persistence layer used for user registration operations
     */
    public RegistrationUtils(IPersistenceLayer persistenceLayer) {
        this.persistenceLayer = persistenceLayer;
    }

    /**
     * Constructs a RegistrationUtils instance using a default persistence layer.
     */
    public RegistrationUtils() {
        this(new PersistenceLayer());
    }

    /**
     * Processes the registration of a new user.
     * <p>
     * The process includes:
     * <ol>
     *   <li>Verifying that the username and password are not null or empty.</li>
     *   <li>Ensuring the user is not already registered.</li>
     *   <li>Evaluating the password complexity.</li>
     *   <li>Saving the new user's credentials if the password is acceptable.</li>
     * </ol>
     *
     * @param username the username to register
     * @param password the password to register
     * @return a {@link RegistrationResult} indicating the outcome of the registration process
     */
    public RegistrationResult processRegistration(String username, String password) {
        logger.info("Starting registration");
        StringMustNotBeNullOrEmpty(username);
        StringMustNotBeNullOrEmpty(password);

        if (isUserInDatabase(username)) {
            logger.info("Cannot register user {} - already registered", username);
            return new RegistrationResult(false, ALREADY_REGISTERED);
        }
        // At this point we know the user is not yet registered in the database.

        // Check if the password is sufficiently complex.
        final PasswordResult passwordResult = isPasswordGood(password);
        if (passwordResult.status != SUCCESS) {
            logger.info("User provided a bad password during registration");
            return new RegistrationResult(false, BAD_PASSWORD, passwordResult.toPrettyString());
        }

        // Save the new user to the database.
        saveToDatabase(username, password);
        logger.info("Saving new user {} to database", username);
        return new RegistrationResult(true, SUCCESSFULLY_REGISTERED);
    }

    /**
     * Creates a RegistrationUtils instance with an empty persistence layer.
     *
     * @return a new RegistrationUtils instance backed by an empty persistence layer
     */
    public static RegistrationUtils createEmpty() {
        return new RegistrationUtils(PersistenceLayer.createEmpty());
    }

    /**
     * Determines whether the underlying persistence layer is empty.
     *
     * @return {@code true} if the persistence layer is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return persistenceLayer.isEmpty();
    }

    /**
     * Evaluates whether the provided password is sufficiently secure.
     * <p>
     * The evaluation includes:
     * <ol>
     *   <li>The password must not be empty.</li>
     *   <li>The password must not be too short (less than 10 characters).</li>
     *   <li>The password must not be too long (more than 100 characters, as analysis slows down beyond this limit).</li>
     *   <li>Entropy analysis is performed using the Nbvcxz library.</li>
     * </ol>
     * <p>
     * See {@link PasswordResult} for details on the evaluation result.
     *
     * @param password the password to evaluate
     * @return a {@link PasswordResult} containing the details of the password evaluation
     */
    public static PasswordResult isPasswordGood(String password) {
        if (password.isEmpty()) {
            logger.info("Password was empty");
            return PasswordResult.createDefault(EMPTY_PASSWORD);
        }
        StringMustNotBeNullOrEmpty(password);

        final boolean isTooSmall = password.length() < 10;
        if (isTooSmall) {
            logger.info("Password was too short");
            return PasswordResult.createDefault(TOO_SHORT);
        }
        CheckUtils.mustBeTrueAtThisPoint(!isTooSmall,
                "At this point, the password cannot be too small");

        final boolean isTooLarge = password.length() > 100;
        if (isTooLarge) {
            logger.info("Password was too long");
            return PasswordResult.createDefault(TOO_LONG);
        }
        CheckUtils.mustBeTrueAtThisPoint(!isTooLarge,
                "At this point, the password cannot be too large");

        // Use Nbvcxz to evaluate the password's entropy.
        final Nbvcxz nbvcxz = new Nbvcxz();
        final Result result = nbvcxz.estimate(password);
        final String suggestions = String.join(";", result.getFeedback().getSuggestion());

        final Double entropy = result.getEntropy();
        CheckUtils.mustBeTrueAtThisPoint(entropy > 0d, "There must be some entropy, more than 0");

        String timeToCrackOff = TimeEstimate.getTimeToCrackFormatted(result, "OFFLINE_BCRYPT_12");
        String timeToCrackOn = TimeEstimate.getTimeToCrackFormatted(result, "ONLINE_THROTTLED");
        if (!result.isMinimumEntropyMet()) {
            logger.info("Minimum entropy for password was not met");
            return new PasswordResult(INSUFFICIENT_ENTROPY, entropy, timeToCrackOff, timeToCrackOn, suggestions);
        } else {
            logger.info("Password met required entropy");
            return new PasswordResult(SUCCESS, entropy, timeToCrackOff, timeToCrackOn, result.getFeedback().getResult());
        }
    }

    /**
     * Determines whether a user with the specified username exists in the database.
     *
     * @param username the username to search for
     * @return {@code true} if the user exists; {@code false} otherwise
     */
    public boolean isUserInDatabase(String username) {
        return persistenceLayer.searchForUserByName(username).isPresent();
    }

    /**
     * Saves a new user to the database.
     *
     * @param username the username to save
     * @param password the password associated with the username
     */
    private void saveToDatabase(String username, String password) {
        final long userId = persistenceLayer.saveNewUser(username);
        persistenceLayer.updateUserWithPassword(userId, password);
    }
}
