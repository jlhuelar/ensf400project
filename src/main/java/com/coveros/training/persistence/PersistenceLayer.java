package com.coveros.training.persistence;

import com.coveros.training.helpers.CheckUtils;
import com.coveros.training.helpers.StringUtils;
import com.coveros.training.library.domainobjects.Book;
import com.coveros.training.library.domainobjects.Borrower;
import com.coveros.training.library.domainobjects.Loan;
import com.coveros.training.authentication.domainobjects.User;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Provides persistence operations for the application.
 * <p>
 * This class implements the {@link IPersistenceLayer} interface and provides methods to perform
 * database operations such as inserting, updating, deleting, and querying data for library,
 * authentication, and other business domains.
 * </p>
 */
public class PersistenceLayer implements IPersistenceLayer {

    private final DataSource dataSource;

    /**
     * Constructs a new PersistenceLayer instance using a connection pool.
     */
    public PersistenceLayer() {
        this(obtainConnectionPool());
    }

    /**
     * Constructs a new PersistenceLayer instance with the specified DataSource.
     *
     * @param ds the DataSource to be used for database connections
     */
    PersistenceLayer(DataSource ds) {
        dataSource = ds;
    }

    /**
     * Obtains a JdbcConnectionPool for H2 in-memory database configured in PostgreSQL mode.
     *
     * @return a JdbcConnectionPool instance
     */
    private static JdbcConnectionPool obtainConnectionPool() {
        return JdbcConnectionPool.create("jdbc:h2:mem:training;MODE=PostgreSQL", "", "");
    }

    /**
     * Executes an update operation (such as UPDATE or DELETE) using a prepared statement.
     *
     * @param description        a description of the operation (for logging)
     * @param preparedStatement  the SQL statement with placeholders
     * @param params             the parameters to set in the statement
     */
    void executeUpdateTemplate(String description, String preparedStatement, Object... params) {
        final SqlData<Object> sqlData = new SqlData<>(description, preparedStatement, params);
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = prepareStatementWithKeys(sqlData, connection)) {
                executeUpdateOnPreparedStatement(sqlData, st);
            }
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * Executes an insert operation using a prepared statement and returns the generated key.
     *
     * @param description       a description of the operation (for logging)
     * @param preparedStatement the SQL insert statement with placeholders
     * @param params            the parameters to set in the statement
     * @return the generated key as a long value
     */
    public long executeInsertTemplate(String description, String preparedStatement, Object... params) {
        final SqlData<Object> sqlData = new SqlData<>(description, preparedStatement, params);
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = prepareStatementWithKeys(sqlData, connection)) {
                return executeInsertOnPreparedStatement(sqlData, st);
            }
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * Executes an insert prepared statement and extracts the generated key.
     *
     * @param sqlData the SqlData object containing the SQL details and parameters
     * @param st      the prepared statement with generated keys enabled
     * @return the generated key as a long value
     * @param <T>     the type parameter for SqlData
     * @throws SQLException if an SQL error occurs
     */
    <T> long executeInsertOnPreparedStatement(SqlData<T> sqlData, PreparedStatement st) throws SQLException {
        sqlData.applyParametersToPreparedStatement(st);
        st.executeUpdate();
        try (ResultSet generatedKeys = st.getGeneratedKeys()) {
            long newId;
            if (generatedKeys.next()) {
                newId = generatedKeys.getLong(1);
                assert (newId > 0);
            } else {
                throw new SqlRuntimeException("Failed SQL operation. Description: " + sqlData.description
                        + " SQL code: " + sqlData.preparedStatement);
            }
            return newId;
        }
    }

    /**
     * Applies parameters to a prepared statement and executes an update.
     *
     * @param sqlData the SqlData object containing the SQL details and parameters
     * @param st      the prepared statement
     * @param <T>     the type parameter for SqlData
     * @throws SQLException if an SQL error occurs
     */
    private <T> void executeUpdateOnPreparedStatement(SqlData<T> sqlData, PreparedStatement st) throws SQLException {
        sqlData.applyParametersToPreparedStatement(st);
        st.executeUpdate();
    }

    /**
     * Prepares a statement with generated keys enabled.
     *
     * @param sqlData    the SqlData object containing the SQL statement
     * @param connection the database connection
     * @param <T>        the type parameter for SqlData
     * @return a PreparedStatement with generated keys enabled
     * @throws SQLException if an SQL error occurs
     */
    private <T> PreparedStatement prepareStatementWithKeys(SqlData<T> sqlData, Connection connection) throws SQLException {
        return connection.prepareStatement(sqlData.preparedStatement, Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * Executes a query using the provided SqlData and returns the extracted result.
     *
     * @param sqlData the SqlData object containing the SQL query, parameters, and extractor function
     * @param <R>     the result type
     * @return an Optional containing the result if available, otherwise an empty Optional
     */
    <R> Optional<R> runQuery(SqlData<R> sqlData) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement(sqlData.preparedStatement)) {
                sqlData.applyParametersToPreparedStatement(st);
                try (ResultSet resultSet = st.executeQuery()) {
                    return sqlData.extractor.apply(resultSet);
                }
            }
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * A functional interface to wrap functions that may throw exceptions when processing a ResultSet.
     *
     * @param <R> the result type
     * @param <E> the exception type
     */
    @FunctionalInterface
    private interface ThrowingFunction<R, E extends Exception> {
        R apply(ResultSet resultSet) throws E;
    }

    /**
     * Wraps a throwing function so it can be used as a regular function.
     *
     * @param throwingFunction a function that may throw an exception
     * @param <R>              the result type
     * @return a Function that wraps the throwing function, converting exceptions to SqlRuntimeException
     */
    static <R> Function<ResultSet, R> throwingFunctionWrapper(ThrowingFunction<R, Exception> throwingFunction) {
        return resultSet -> {
            try {
                return throwingFunction.apply(resultSet);
            } catch (Exception ex) {
                throw new SqlRuntimeException(ex);
            }
        };
    }

    /**
     * Creates an extractor function to extract data from a ResultSet.
     *
     * @param extractorFunction a throwing function that extracts an Optional value from a ResultSet
     * @param <T>               the type of the extracted data
     * @return a Function that extracts an Optional of type T from a ResultSet
     */
    private <T> Function<ResultSet, Optional<T>> createExtractor(ThrowingFunction<Optional<T>, Exception> extractorFunction) {
        return throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                return extractorFunction.apply(rs);
            } else {
                return Optional.empty();
            }
        });
    }

    // ===========================
    // Library / Book / Borrower Operations
    // ===========================

    /**
     * Saves a new borrower to the database.
     *
     * @param borrowerName the name of the borrower to save
     * @return the generated borrower id
     */
    @Override
    public long saveNewBorrower(String borrowerName) {
        CheckUtils.StringMustNotBeNullOrEmpty(borrowerName);
        return executeInsertTemplate(
                "Adds a new library borrower",
                "INSERT INTO library.borrower (name) VALUES (?);",
                borrowerName);
    }

    /**
     * Creates a new loan for a book borrowed by a borrower.
     *
     * @param book       the book being loaned
     * @param borrower   the borrower taking the book
     * @param borrowDate the date the book is borrowed
     * @return the generated loan id
     */
    @Override
    public long createLoan(Book book, Borrower borrower, Date borrowDate) {
        return executeInsertTemplate(
                "Creates a new loan of a book to a borrower",
                "INSERT INTO library.loan (book, borrower, borrow_date) VALUES (?, ?, ?);",
                book.id, borrower.id, borrowDate);
    }

    /**
     * Saves a new book to the database.
     *
     * @param bookTitle the title of the book to save
     * @return the generated book id
     */
    @Override
    public long saveNewBook(String bookTitle) {
        CheckUtils.StringMustNotBeNullOrEmpty(bookTitle);
        return executeInsertTemplate(
                "Creates a new book in the database",
                "INSERT INTO library.book (title) VALUES (?);",
                bookTitle);
    }

    /**
     * Updates a borrower with a new name.
     *
     * @param id           the id of the borrower to update; must be positive
     * @param borrowerName the new name for the borrower
     */
    @Override
    public void updateBorrower(long id, String borrowerName) {
        CheckUtils.IntParameterMustBePositive(id);
        CheckUtils.StringMustNotBeNullOrEmpty(borrowerName);
        executeUpdateTemplate(
                "Updates the borrower's data",
                "UPDATE library.borrower SET name = ? WHERE id = ?;",
                borrowerName, id);
    }

    /**
     * Deletes a book from the database.
     *
     * @param id the id of the book to delete; must be positive
     */
    @Override
    public void deleteBook(long id) {
        CheckUtils.IntParameterMustBePositive(id);
        executeUpdateTemplate(
                "Deletes a book from the database",
                "DELETE FROM library.book WHERE id = ?;",
                id);
    }

    /**
     * Deletes a borrower from the database.
     *
     * @param id the id of the borrower to delete; must be positive
     */
    @Override
    public void deleteBorrower(long id) {
        CheckUtils.IntParameterMustBePositive(id);
        executeUpdateTemplate(
                "Deletes a borrower from the database",
                "DELETE FROM library.borrower WHERE id = ?;",
                id);
    }

    /**
     * Retrieves the name of a borrower by id.
     *
     * @param id the id of the borrower; must be positive
     * @return an Optional containing the borrower's name if found, otherwise an empty Optional
     */
    @Override
    public Optional<String> getBorrowerName(long id) {
        CheckUtils.IntParameterMustBePositive(id);
        Function<ResultSet, Optional<String>> extractor =
                createExtractor(rs -> Optional.of(StringUtils.makeNotNullable(rs.getString(1))));
        return runQuery(new SqlData<>(
                "Get a borrower's name by their id",
                "SELECT name FROM library.borrower WHERE id = ?;",
                extractor, id));
    }

    /**
     * Searches for a borrower by name.
     *
     * @param borrowerName the name of the borrower to search for
     * @return an Optional containing the Borrower if found, otherwise an empty Optional
     */
    @Override
    public Optional<Borrower> searchBorrowerDataByName(String borrowerName) {
        CheckUtils.StringMustNotBeNullOrEmpty(borrowerName);
        Function<ResultSet, Optional<Borrower>> extractor = createExtractor(rs -> {
            long id = rs.getLong(1);
            String name = StringUtils.makeNotNullable(rs.getString(2));
            return Optional.of(new Borrower(id, name));
        });
        return runQuery(new SqlData<>(
                "Search for details on a borrower by name",
                "SELECT id, name FROM library.borrower WHERE name = ?;",
                extractor, borrowerName));
    }

    /**
     * Searches for a book by its title.
     *
     * @param bookTitle the title of the book to search for
     * @return an Optional containing the Book if found, otherwise an empty Optional
     */
    @Override
    public Optional<Book> searchBooksByTitle(String bookTitle) {
        CheckUtils.StringMustNotBeNullOrEmpty(bookTitle);
        Function<ResultSet, Optional<Book>> extractor = createExtractor(rs -> {
            long id = rs.getLong(1);
            return Optional.of(new Book(id, bookTitle));
        });
        return runQuery(new SqlData<>(
                "Search for a book by title",
                "SELECT id FROM library.book WHERE title = ?;",
                extractor, bookTitle));
    }

    /**
     * Searches for a book by its id.
     *
     * @param id the id of the book to search for; must be positive
     * @return an Optional containing the Book if found, otherwise an empty Optional
     */
    @Override
    public Optional<Book> searchBooksById(long id) {
        CheckUtils.IntParameterMustBePositive(id);
        Function<ResultSet, Optional<Book>> extractor = createExtractor(rs -> {
            long bookId = rs.getLong(1);
            String title = StringUtils.makeNotNullable(rs.getString(2));
            return Optional.of(new Book(bookId, title));
        });
        return runQuery(new SqlData<>(
                "Search for a book by id",
                "SELECT id, title FROM library.book WHERE id = ?;",
                extractor, id));
    }

    /**
     * Searches for a borrower by its id.
     *
     * @param id the id of the borrower to search for; must be positive
     * @return an Optional containing the Borrower if found, otherwise an empty Optional
     */
    @Override
    public Optional<Borrower> searchBorrowersById(long id) {
        CheckUtils.IntParameterMustBePositive(id);
        Function<ResultSet, Optional<Borrower>> extractor = createExtractor(rs -> {
            long borrowerId = rs.getLong(1);
            String name = StringUtils.makeNotNullable(rs.getString(2));
            return Optional.of(new Borrower(borrowerId, name));
        });
        return runQuery(new SqlData<>(
                "Search for a borrower by id",
                "SELECT id, name FROM library.borrower WHERE id = ?;",
                extractor, id));
    }

    /**
     * Lists all books in the database.
     *
     * @return an Optional containing a list of all books if found, otherwise an empty Optional
     */
    @Override
    public Optional<List<Book>> listAllBooks() {
        return listBooks("Get all books", "SELECT id, title FROM library.book;");
    }

    /**
     * Lists all available books (i.e. books that are not currently on loan).
     *
     * @return an Optional containing a list of available books if found, otherwise an empty Optional
     */
    @Override
    public Optional<List<Book>> listAvailableBooks() {
        return listBooks("Get all available books",
                "SELECT b.id, b.title FROM library.book b " +
                "LEFT JOIN library.loan l ON b.id = l.book " +
                "WHERE l.borrow_date IS NULL;");
    }

    /**
     * Helper method to execute a query that returns a list of books.
     *
     * @param description a description of the query
     * @param sqlCode     the SQL query to execute
     * @return an Optional containing a list of books if found, otherwise an empty Optional
     */
    private Optional<List<Book>> listBooks(String description, String sqlCode) {
        Function<ResultSet, Optional<List<Book>>> extractor = createExtractor(rs -> {
            List<Book> bookList = new ArrayList<>();
            do {
                long id = rs.getLong(1);
                String title = StringUtils.makeNotNullable(rs.getString(2));
                bookList.add(new Book(id, title));
            } while (rs.next());
            return Optional.of(bookList);
        });
        return runQuery(new SqlData<>(description, sqlCode, extractor));
    }

    /**
     * Lists all borrowers in the database.
     *
     * @return an Optional containing a list of borrowers if found, otherwise an empty Optional
     */
    @Override
    public Optional<List<Borrower>> listAllBorrowers() {
        Function<ResultSet, Optional<List<Borrower>>> extractor = createExtractor(rs -> {
            List<Borrower> borrowerList = new ArrayList<>();
            do {
                long id = rs.getLong(1);
                String name = StringUtils.makeNotNullable(rs.getString(2));
                borrowerList.add(new Borrower(id, name));
            } while (rs.next());
            return Optional.of(borrowerList);
        });
        return runQuery(new SqlData<>(
                "Get all borrowers",
                "SELECT id, name FROM library.borrower;",
                extractor));
    }

    /**
     * Searches for all loans associated with a given borrower.
     *
     * @param borrower the borrower whose loans to search for
     * @return an Optional containing a list of loans if found, otherwise an empty Optional
     */
    @Override
    public Optional<List<Loan>> searchForLoanByBorrower(Borrower borrower) {
        Function<ResultSet, Optional<List<Loan>>> extractor = createExtractor(rs -> {
            List<Loan> loans = new ArrayList<>();
            do {
                long loanId = rs.getLong(1);
                Date borrowDate = rs.getDate(2);
                long bookId = rs.getLong(3);
                String bookTitle = StringUtils.makeNotNullable(rs.getString(4));
                Date borrowDateNotNullable = (borrowDate == null) ? Date.valueOf("0000-01-01") : borrowDate;
                loans.add(new Loan(new Book(bookId, bookTitle), borrower, loanId, borrowDateNotNullable));
            } while (rs.next());
            return Optional.of(loans);
        });
        return runQuery(new SqlData<>(
                "Search for all loans by borrower",
                "SELECT loan.id, loan.borrow_date, loan.book, book.title FROM library.loan loan " +
                "JOIN library.book book ON book.id = loan.book WHERE loan.borrower = ?;",
                extractor, borrower.id));
    }

    /**
     * Searches for a loan associated with a given book.
     *
     * @param book the book for which to search the loan
     * @return an Optional containing the loan if found, otherwise an empty Optional
     */
    @Override
    public Optional<Loan> searchForLoanByBook(Book book) {
        Function<ResultSet, Optional<Loan>> extractor = createExtractor(rs -> {
            long loanId = rs.getLong(1);
            Date borrowDate = rs.getDate(2);
            long borrowerId = rs.getLong(3);
            String borrowerName = StringUtils.makeNotNullable(rs.getString(4));
            Date borrowDateNotNullable = (borrowDate == null) ? Date.valueOf("0000-01-01") : borrowDate;
            return Optional.of(new Loan(book, new Borrower(borrowerId, borrowerName), loanId, borrowDateNotNullable));
        });
        return runQuery(new SqlData<>(
                "Search for a loan by book",
                "SELECT loan.id, loan.borrow_date, loan.borrower, bor.name FROM library.loan loan " +
                "JOIN library.borrower bor ON bor.id = loan.borrower WHERE loan.book = ?;",
                extractor, book.id));
    }

    // =========================
    // Authentication / "USER" Operations
    // =========================

    /**
     * Saves a new user to the database.
     *
     * @param username the username of the new user
     * @return the generated user id
     */
    @Override
    public long saveNewUser(String username) {
        CheckUtils.StringMustNotBeNullOrEmpty(username);
        return executeInsertTemplate(
                "Creates a new user in the database",
                "INSERT INTO auth.\"USER\" (name) VALUES (?);",
                username);
    }

    /**
     * Searches for a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found, otherwise an empty Optional
     */
    @Override
    public Optional<User> searchForUserByName(String username) {
        CheckUtils.StringMustNotBeNullOrEmpty(username);
        Function<ResultSet, Optional<User>> extractor = createExtractor(rs -> {
            long id = rs.getLong(1);
            return Optional.of(new User(username, id));
        });
        return runQuery(new SqlData<>(
                "Search for a user by name",
                "SELECT id FROM auth.\"USER\" WHERE name = ?;",
                extractor, username));
    }

    /**
     * Checks whether the provided credentials are valid.
     *
     * @param username the username to check
     * @param password the password to verify
     * @return an Optional containing {@code true} if the credentials are valid, otherwise an empty Optional
     */
    @Override
    public Optional<Boolean> areCredentialsValid(String username, String password) {
        Function<ResultSet, Optional<Boolean>> extractor = createExtractor(rs -> {
            long id = rs.getLong(1);
            assert (id > 0);
            return Optional.of(true);
        });
        final String hexHash = createHashedValueFromPassword(password);
        return runQuery(new SqlData<>(
                "Check if the credentials for a user are valid",
                "SELECT id FROM auth.\"USER\" WHERE name = ? AND password_hash = ?;",
                extractor, username, hexHash));
    }

    /**
     * Updates a user's password with a new hashed value.
     *
     * @param id       the id of the user to update; must be positive
     * @param password the new password
     */
    @Override
    public void updateUserWithPassword(long id, String password) {
        CheckUtils.IntParameterMustBePositive(id);
        String hashedPassword = createHashedValueFromPassword(password);
        executeUpdateTemplate(
                "Updates the user's password field with a new hash",
                "UPDATE auth.\"USER\" SET password_hash = ? WHERE id = ?;",
                hashedPassword, id);
    }

    /**
     * Creates a SHA-256 hash of the provided password.
     *
     * @param password the password to hash
     * @return a hexadecimal representation of the hashed password
     */
    private String createHashedValueFromPassword(String password) {
        CheckUtils.StringMustNotBeNullOrEmpty(password);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * Converts a byte array to its hexadecimal string representation.
     *
     * @param bytes the byte array to convert
     * @return a hexadecimal string representing the byte array
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Creates an empty persistence layer instance.
     *
     * @return an IPersistenceLayer instance backed by an empty data source
     */
    public static IPersistenceLayer createEmpty() {
        return new PersistenceLayer(new EmptyDataSource());
    }

    /**
     * Checks whether this persistence layer is empty.
     *
     * @return {@code true} if the underlying data source is an instance of {@link EmptyDataSource}, {@code false} otherwise
     */
    @Override
    public boolean isEmpty() {
        return this.dataSource.getClass().equals(EmptyDataSource.class);
    }

    /**
     * Runs a backup operation by generating a database script.
     *
     * @param backupFileName the file name to which the backup script will be written
     */
    @Override
    public void runBackup(String backupFileName) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement("SCRIPT TO ?")) {
                st.setString(1, backupFileName);
                st.execute();
            }
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * Restores the database from a backup file.
     *
     * @param backupFileName the name of the backup file to restore from
     */
    @Override
    public void runRestore(String backupFileName) {
        String dbScriptsDirectory = "src/integration_test/resources/db_sample_files/";
        String fullPathToBackup = dbScriptsDirectory + backupFileName;
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = connection.prepareStatement(
                    "DROP SCHEMA IF EXISTS ADMINISTRATIVE CASCADE;" +
                    "DROP SCHEMA IF EXISTS AUTH CASCADE;" +
                    "DROP SCHEMA IF EXISTS LIBRARY CASCADE;")) {
                st.execute();
            }
            try (PreparedStatement st = connection.prepareStatement("RUNSCRIPT FROM ?")) {
                st.setString(1, fullPathToBackup);
                st.execute();
            }
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    /**
     * Cleans the database schema and data, then migrates to the current schema version.
     */
    @Override
    public void cleanAndMigrateDatabase() {
        cleanDatabase();
        migrateDatabase();
    }

    /**
     * Cleans the database using Flyway.
     */
    @Override
    public void cleanDatabase() {
        Flyway flyway = configureFlyway();
        flyway.clean();
    }

    /**
     * Migrates the database schema using Flyway.
     */
    @Override
    public void migrateDatabase() {
        Flyway flyway = configureFlyway();
        flyway.migrate();
    }

    /**
     * Configures Flyway for database migrations.
     *
     * @return a configured Flyway instance
     */
    private Flyway configureFlyway() {
        return Flyway.configure()
                .schemas("ADMINISTRATIVE", "LIBRARY", "AUTH")
                .dataSource(this.dataSource)
                .cleanDisabled(false)
                .load();
    }
}
