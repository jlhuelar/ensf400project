package com.coveros.training.persistence;

import com.coveros.training.CheckUtils;
import com.coveros.training.StringUtils;
import com.coveros.training.domainobjects.Book;
import com.coveros.training.domainobjects.Borrower;
import com.coveros.training.domainobjects.Loan;
import com.coveros.training.domainobjects.User;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcConnectionPool;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PersistenceLayer {

    private final DataSource dataSource;

    public PersistenceLayer() {
        this(obtainConnectionPool());
    }

    PersistenceLayer(DataSource ds) {
        dataSource = ds;
    }

    private static JdbcConnectionPool obtainConnectionPool() {
        return JdbcConnectionPool.create(
                "jdbc:h2:mem:training;MODE=PostgreSQL", "", "");
    }

    /**
     * This command provides a template to execute updates (including inserts) on the database
     *
     * @param sqlData An object that contains the necessary components to run a SQL statement.
     *                Usually contains some SQL text and some values that will be injected
     *                into the statement at run-time.
     */
    void executeUpdateTemplate(SqlData sqlData) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = prepareStatementWithKeys(sqlData, connection)) {
                executeUpdateOnPreparedStatement(sqlData, st);
            }
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    private long executeInsertTemplate(SqlData sqlData) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st = prepareStatementWithKeys(sqlData, connection)) {
                return executeInsertOnPreparedStatement(sqlData, st);
            }
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }
    }

    long executeInsertOnPreparedStatement(SqlData sqlData, PreparedStatement st) throws SQLException {
        sqlData.applyParametersToPreparedStatement(st);
        st.executeUpdate();
        try (ResultSet generatedKeys = st.getGeneratedKeys()) {
            long newId;
            if (generatedKeys.next()) {
                newId = generatedKeys.getLong(1);
                assert (newId > 0);
            } else {
                throw new SqlRuntimeException("failed Sql.  Description: " + sqlData.description + " SQL code: " + sqlData.preparedStatement);
            }
            return newId;
        }
    }

    private void executeUpdateOnPreparedStatement(SqlData sqlData, PreparedStatement st) throws SQLException {
        sqlData.applyParametersToPreparedStatement(st);
        st.executeUpdate();
    }

    /**
     * A helper method.  Simply creates a prepared statement that
     * always returns the generated keys from the database, like
     * when you insert a new row of data in a table with auto-generating primary key.
     *
     * @param sqlData    see {@link SqlData}
     * @param connection a typical {@link Connection}
     */
    private PreparedStatement prepareStatementWithKeys(SqlData sqlData, Connection connection) throws SQLException {
        return connection.prepareStatement(
                sqlData.preparedStatement,
                Statement.RETURN_GENERATED_KEYS);
    }

    /**
     * Creates a new borrower in the database
     *
     * @param borrowerName the name of the borrower
     * @return a long id representing its id in the database.  Generated by the database.
     */
    long saveNewBorrower(String borrowerName) {
        final SqlData sqlData = new SqlData(
                "adds a new library borrower",
                "INSERT INTO library.borrower (name) VALUES (?);");
        sqlData.addParameter(borrowerName, String.class);

        return executeInsertTemplate(sqlData);
    }

    /**
     * Creates a new loan of a book in the database.
     *
     * @param book       a book we have to loan out
     * @param borrower   a person who is borrowing the book
     * @param borrowDate the date this book was borrowed
     * @return a long id representing its id in the database.  Generated by the database.
     */
    long createLoan(Book book, Borrower borrower, Date borrowDate) {
        final SqlData sqlData = new SqlData(
                "Creates a new loan of a book to a borrower",
                "INSERT INTO library.loan (book, borrower, borrow_date) VALUES (?, ?, ?);");
        sqlData.addParameter(book.id, Long.class);
        sqlData.addParameter(borrower.id, Long.class);
        sqlData.addParameter(borrowDate, Date.class);

        return executeInsertTemplate(sqlData);
    }

    /**
     * Creates a new book in the database
     *
     * @param bookTitle the String title of a book
     * @return a long id representing its id in the database.  Generated by the database.
     */
    long saveNewBook(String bookTitle) {
        final SqlData sqlData = new SqlData(
                "Creates a new book in the database",
                "INSERT INTO library.book (title) VALUES (?);");
        sqlData.addParameter(bookTitle, String.class);

        return executeInsertTemplate(sqlData);
    }

    /**
     * Save a new user to the database
     *
     * @param username a String name of a user
     * @return a long id representing its id in the database.  Generated by the database.
     */
    long saveNewUser(String username) {
        final SqlData sqlData = new SqlData(
                "Creates a new user in the database",
                "INSERT INTO auth.user (name) VALUES (?);");
        sqlData.addParameter(username, String.class);

        return executeInsertTemplate(sqlData);
    }

    /**
     * If we already have a borrower, this command allows us to change
     * their values (except for their id)
     *
     * @param id           the id of a borrower (a constant)
     * @param borrowerName the name of a borrower, which we can change.
     */
    void updateBorrower(long id, String borrowerName) {
        CheckUtils.checkIntParamPositive(id);
        final SqlData sqlData = new SqlData(
                "Updates the borrower's data",
                "UPDATE library.borrower SET name = ? WHERE id = ?;");
        sqlData.addParameter(borrowerName, String.class);
        sqlData.addParameter(id, Long.class);
        executeUpdateTemplate(sqlData);
    }


    /**
     * Delete a book from the database
     * @param id the identifier for a book
     */
    public void deleteBook(long id) {
        CheckUtils.checkIntParamPositive(id);
        final SqlData sqlData = new SqlData(
                "Deletes a book from the database",
                "DELETE FROM library.book WHERE id = ?;");
        sqlData.addParameter(id, Long.class);
        executeUpdateTemplate(sqlData);
    }


    /**
     * Delete a borrower from the database
     * @param id the identifier for a borrower
     */
    public void deleteBorrower(long id) {
        CheckUtils.checkIntParamPositive(id);
        final SqlData sqlData = new SqlData(
                "Deletes a borrower from the database",
                "DELETE FROM library.borrower WHERE id = ?;");
        sqlData.addParameter(id, Long.class);
        executeUpdateTemplate(sqlData);
    }

    /**
     * Given the id for a borrower, this command returns their name.
     *
     * @param id a borrower's id.
     * @return the borrower's name, or an empty string if not found
     */
    String getBorrowerName(long id) {
        CheckUtils.checkIntParamPositive(id);
        Function<ResultSet, String> extractor = throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                return StringUtils.makeNotNullable(rs.getString(1));
            } else {
                return "";
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "get a borrower's name by their id",
                        "SELECT name FROM library.borrower WHERE id = ?;",
                        extractor);
        sqlData.addParameter(id, Long.class);
        return runQuery(sqlData);
    }

    /**
     * Searches for a borrower by name.  Returns full details
     * if found.  return empty borrower data if not found.
     *
     * @param borrowerName the name of a borrower
     * @return a valid borrower, or an empty borrower if not found
     */
    Borrower searchBorrowerDataByName(String borrowerName) {
        Function<ResultSet, Borrower> extractor = throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                long id = rs.getLong(1);
                String name = StringUtils.makeNotNullable(rs.getString(2));
                return new Borrower(id, name);
            } else {
                return Borrower.createEmpty();
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "search for details on a borrower by name",
                        "SELECT id, name FROM library.borrower WHERE name = ?;",
                        extractor);
        sqlData.addParameter(borrowerName, String.class);
        return runQuery(sqlData);
    }

    Book searchBooksByTitle(String bookTitle) {
        Function<ResultSet, Book> extractor = throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                long id = rs.getLong(1);
                return new Book(id, bookTitle);
            } else {
                return Book.createEmpty();
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "search for a book by title",
                        "SELECT id FROM library.book WHERE title = ?;",
                        extractor);
        sqlData.addParameter(bookTitle, String.class);
        return runQuery(sqlData);
    }


    public Book searchBooksById(long id) {
        Function<ResultSet, Book> extractor = throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                long bookId = rs.getLong(1);
                String title = StringUtils.makeNotNullable(rs.getString(2));
                return new Book(bookId, title);
            } else {
                return Book.createEmpty();
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "search for a book by title",
                        "SELECT id, title FROM library.book WHERE id = ?;",
                        extractor);
        sqlData.addParameter(id, Long.class);
        return runQuery(sqlData);
    }


    public Borrower searchBorrowersById(long id) {
        Function<ResultSet, Borrower> extractor = throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                long borrowerId = rs.getLong(1);
                String name = StringUtils.makeNotNullable(rs.getString(2));
                return new Borrower(borrowerId, name);
            } else {
                return Borrower.createEmpty();
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "search for a borrower by name",
                        "SELECT id, name FROM library.borrower WHERE id = ?;",
                        extractor);
        sqlData.addParameter(id, Long.class);
        return runQuery(sqlData);
    }

    public List<Book> listAllBooks() {
        Function<ResultSet, List<Book>> extractor = throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                List<Book> bookList = new ArrayList<>();
                do {
                    long id = rs.getLong(1);
                    String title = StringUtils.makeNotNullable(rs.getString(2));
                    bookList.add(new Book(id, title));
                } while (rs.next());
                return bookList;
            } else {
                return new ArrayList<>();
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "get all books",
                        "SELECT id, title FROM library.book;",
                        extractor);
        return runQuery(sqlData);
    }

    public List<Borrower> listAllBorrowers() {
        Function<ResultSet, List<Borrower>> extractor = throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                List<Borrower> borrowerList = new ArrayList<>();
                do {
                    long id = rs.getLong(1);
                    String name = StringUtils.makeNotNullable(rs.getString(2));
                    borrowerList.add(new Borrower(id, name));
                } while (rs.next());
                return borrowerList;
            } else {
                return new ArrayList<>();
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "get all borrowers",
                        "SELECT id, name FROM library.borrower;",
                        extractor);
        return runQuery(sqlData);
    }


    <R> R runQuery(SqlData sqlData) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement st =
                         connection.prepareStatement(sqlData.preparedStatement)) {
                sqlData.applyParametersToPreparedStatement(st);
                try (ResultSet resultSet = st.executeQuery()) {
                    @SuppressWarnings("unchecked")
                    final R result = (R) sqlData.extractor.apply(resultSet);
                    return result;
                }
            }
        } catch (SQLException ex) {
            throw new SqlRuntimeException(ex);
        }

    }


    /**
     * This is an interface to a wrapper around {@link Function} so we can catch exceptions
     * in the generic function.
     *
     * @param <T> The type of the thing acted upon
     * @param <R> The return type
     * @param <E> The type of the exception
     */
    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    /**
     * This is the implementation of {@link ThrowingFunction}
     */
    static <T, R> Function<T, R> throwingFunctionWrapper(
            ThrowingFunction<T, R, Exception> throwingFunction) {

        return t -> {
            try {
                return throwingFunction.apply(t);
            } catch (Exception ex) {
                throw new SqlRuntimeException(ex);
            }
        };
    }

    User searchForUserByName(String username) {
        Function<ResultSet, User> extractor = throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                final long id = rs.getLong(1);
                return new User(username, id);
            } else {
                return User.createEmpty();
            }
        });
        final SqlData sqlData =
                new SqlData<>(
                        "search for a user by id, return that user if found, otherwise return an empty user",
                        "SELECT id  FROM auth.user WHERE name = ?;",
                        extractor);
        sqlData.addParameter(username, String.class);
        return runQuery(sqlData);
    }

    Boolean areCredentialsValid(String username, String password) {
        Function<ResultSet, Boolean> extractor = throwingFunctionWrapper(rs -> {
            if (rs.next()) {
                final long id = rs.getLong(1);
                assert (id > 0);
                return true;
            } else {
                return false;
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "check to see if the credentials for a user are valid",
                        "SELECT id FROM auth.user WHERE name = ? AND password_hash = ?;",
                        extractor);
        final String hexHash = createHashedValueFromPassword(password);
        sqlData.addParameter(username, String.class);
        sqlData.addParameter(hexHash, String.class);
        return runQuery(sqlData);
    }

    void updateUserWithPassword(long id, String password) {
        CheckUtils.checkIntParamPositive(id);
        final SqlData sqlData = new SqlData(
                "Updates the user's password field with a new hash",
                "UPDATE auth.user SET password_hash = ? WHERE id = ?;");
        String hashedPassword = createHashedValueFromPassword(password);
        sqlData.addParameter(hashedPassword, String.class);
        sqlData.addParameter(id, Long.class);
        executeUpdateTemplate(sqlData);
    }

    private String createHashedValueFromPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * A borrower may have more than one loan.  But a book
     * can only be loaned to one borrower.  That is why when we
     * search for loans by borrower, we may get multiple loans
     * back, but when we search by book, we only get one back.
     * A book cannot be loaned to two people at the same time!
     */
    public List<Loan> searchForLoanByBorrower(Borrower borrower) {
        Function<ResultSet, List<Loan>> extractor = throwingFunctionWrapper((rs) -> {
            List<Loan> loans = new ArrayList<>();
            if (rs.next()) {
                do {
                    final long loanId = rs.getLong(1);
                    final Date borrowDate = rs.getDate(2);
                    final long bookId = rs.getLong(3);
                    final String bookTitle = StringUtils.makeNotNullable(rs.getString(4));
                    final Date borrowDateNotNullable = borrowDate == null ? Date.valueOf("0000-01-01") : borrowDate;
                    loans.add(new Loan(new Book(bookId, bookTitle), borrower, loanId, borrowDateNotNullable));
                } while (rs.next());
                return loans;
            } else {
                return new ArrayList<>();
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "search for all loans by borrower",
                        "SELECT loan.id, loan.borrow_date, loan.book, book.title " +
                        "FROM library.loan loan " +
                        "JOIN library.book book ON book.id = loan.book " +
                        "WHERE loan.borrower = ?;",
                        extractor);
        sqlData.addParameter(borrower.id, Long.class);
        return runQuery(sqlData);
    }

    Loan searchForLoanByBook(Book book) {
        Function<ResultSet, Loan> extractor = throwingFunctionWrapper((rs) -> {
            if (rs.next()) {
                final long loanId = rs.getLong(1);
                final Date borrowDate = rs.getDate(2);
                final long borrowerId = rs.getLong(3);
                final String borrowerName = StringUtils.makeNotNullable(rs.getString(4));
                final Date borrowDateNotNullable = borrowDate == null ? Date.valueOf("0000-01-01") : borrowDate;
                return new Loan(book, new Borrower(borrowerId, borrowerName), loanId, borrowDateNotNullable);
            } else {
                return Loan.createEmpty();
            }
        });

        final SqlData sqlData =
                new SqlData<>(
                        "search for a loan by book",
                        "SELECT loan.id, loan.borrow_date, loan.borrower, bor.name " +
                        "FROM library.loan loan " +
                        "JOIN library.borrower bor ON bor.id = loan.borrower " +
                        "WHERE loan.book = ?;",
                        extractor);
        sqlData.addParameter(book.id, Long.class);
        return runQuery(sqlData);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte hash1 : hash) {
            String hex = Integer.toHexString(0xff & hash1);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static PersistenceLayer createEmpty() {
        return new PersistenceLayer(new EmptyDataSource());
    }

    public boolean isEmpty() {
        return this.dataSource.getClass().equals(EmptyDataSource.class);
    }

    /**
     * Records the current state of the database as a SQL script,
     * used later for restoring by something like {@link #runRestore(String)}
     *
     * @param backupFileName the path to the sql script, based in the project home directory.
     */
    void runBackup(String backupFileName) {
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
     * Runs a database restore script.
     *
     * @param backupFileName the path to the sql script, based in the project home directory.
     */
    void runRestore(String backupFileName) {
        String dbScriptsDirectory="src/main/resources/db/db_sample_files/";
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
     * Cleans the database and runs the scripts to update it
     * to the most current version.
     */
    public void cleanAndMigrateDatabase() {
        cleanDatabase();
        migrateDatabase();
    }

    /**
     * Cleans the database and runs the scripts to update it
     * to the most current version.
     */
    public void cleanDatabase() {
        Flyway flyway = configureFlyway();
        flyway.clean();
    }

    /**
     * Cleans the database and runs the scripts to update it
     * to the most current version.
     */
    public void migrateDatabase() {
        Flyway flyway = configureFlyway();
        flyway.migrate();
    }

    private Flyway configureFlyway() {
        return Flyway.configure()
                .schemas("ADMINISTRATIVE", "LIBRARY", "AUTH")
                .dataSource(this.dataSource)
                .load();
    }

}
