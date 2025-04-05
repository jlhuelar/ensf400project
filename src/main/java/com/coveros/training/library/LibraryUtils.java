package com.coveros.training.library;

import com.coveros.training.library.domainobjects.Book;
import com.coveros.training.library.domainobjects.Borrower;
import com.coveros.training.library.domainobjects.LibraryActionResults;
import com.coveros.training.library.domainobjects.Loan;
import com.coveros.training.persistence.IPersistenceLayer;
import com.coveros.training.persistence.PersistenceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides business logic for handling library operations such as lending books, 
 * registering books or borrowers, searching for entities, and deleting records.
 * <p>
 * All operations delegate persistence-related tasks to the provided persistence layer.
 * </p>
 */
public class LibraryUtils {

    private final IPersistenceLayer persistence;
    private static final Logger logger = LoggerFactory.getLogger(LibraryUtils.class);

    /**
     * Constructs a LibraryUtils instance with the given persistence layer.
     *
     * @param persistence the persistence layer for database operations
     */
    public LibraryUtils(IPersistenceLayer persistence) {
        this.persistence = persistence;
    }

    /**
     * Constructs a LibraryUtils instance with a default persistence layer.
     */
    public LibraryUtils() {
        this(new PersistenceLayer());
    }

    /**
     * Lends a book to a borrower given the book title, borrower name, and the borrow date.
     *
     * @param bookTitle    the title of a registered book (see {@link #registerBook(String)})
     * @param borrowerName the name of a registered borrower (see {@link #registerBorrower(String)})
     * @param borrowDate   the date the book is being lent out
     * @return a {@link LibraryActionResults} enum indicating the status of the lending operation
     */
    public LibraryActionResults lendBook(String bookTitle, String borrowerName, Date borrowDate) {
        logger.info("Starting process to lend a book: {} to borrower: {}", bookTitle, borrowerName);
        final Book book = searchForBookByTitle(bookTitle);
        final Book foundBook = new Book(book.id, bookTitle);
        final Borrower borrower = searchForBorrowerByName(borrowerName);
        final Borrower foundBorrower = new Borrower(borrower.id, borrowerName);
        return lendBook(foundBook, foundBorrower, borrowDate);
    }

    /**
     * Lends a book to a borrower using the provided Book and Borrower objects.
     *
     * @param book       the book to lend
     * @param borrower   the borrower to whom the book is lent
     * @param borrowDate the date the book is being lent out
     * @return a {@link LibraryActionResults} enum indicating the status of the lending operation
     */
    public LibraryActionResults lendBook(Book book, Borrower borrower, Date borrowDate) {
        if (book.id == 0) {
            logger.info("Book: {} was not registered. Lending failed.", book.title);
            return LibraryActionResults.BOOK_NOT_REGISTERED;
        }
        if (borrower.id == 0) {
            logger.info("Borrower: {} was not registered. Lending failed.", borrower.name);
            return LibraryActionResults.BORROWER_NOT_REGISTERED;
        }
        final Loan loan = searchForLoanByBook(book);
        if (!loan.isEmpty()) {
            logger.info("Book: {} was already checked out on {}. Lending failed.", book.title, loan.checkoutDate);
            return LibraryActionResults.BOOK_CHECKED_OUT;
        }
        logger.info("Book: {} is available for borrowing by valid borrower: {}", book.title, borrower.name);
        createLoan(book, borrower, borrowDate);
        return LibraryActionResults.SUCCESS;
    }

    /**
     * Creates a new loan for a book and borrower.
     * <p>
     * This method delegates the loan creation to the persistence layer.
     * </p>
     *
     * @param book       the book to be loaned
     * @param borrower   the borrower who will receive the book
     * @param borrowDate the date the book is loaned out
     */
    void createLoan(Book book, Borrower borrower, Date borrowDate) {
        logger.info("Creating loan for book: {} by borrower: {}", book.title, borrower.name);
        persistence.createLoan(book, borrower, borrowDate);
    }

    /**
     * Registers a new borrower in the library.
     *
     * @param borrower the name of the borrower to register
     * @return a {@link LibraryActionResults} enum indicating the status of the registration operation
     */
    public LibraryActionResults registerBorrower(String borrower) {
        logger.info("Attempting to register a borrower with name: {}", borrower);
        final Borrower borrowerDetails = searchForBorrowerByName(borrower);
        final boolean borrowerWasFound = !borrowerDetails.equals(Borrower.createEmpty());
        if (borrowerWasFound) {
            logger.info("Borrower: {} was already registered.", borrower);
            return LibraryActionResults.ALREADY_REGISTERED_BORROWER;
        }
        logger.info("Borrower: {} was not found. Registering new borrower...", borrower);
        saveNewBorrower(borrower);
        return LibraryActionResults.SUCCESS;
    }

    /**
     * Saves a new borrower to the persistence layer.
     *
     * @param borrower the name of the new borrower
     */
    void saveNewBorrower(String borrower) {
        logger.info("Saving new borrower: {}", borrower);
        persistence.saveNewBorrower(borrower);
    }

    /**
     * Registers a new book in the library.
     *
     * @param bookTitle the title of the book to register
     * @return a {@link LibraryActionResults} enum indicating the status of the registration operation
     * @throws IllegalArgumentException if the book title is empty
     */
    public LibraryActionResults registerBook(String bookTitle) {
        if (bookTitle.isEmpty()) {
            throw new IllegalArgumentException("bookTitle was an empty string - disallowed when registering books");
        }
        logger.info("Attempting to register a book with title: {}", bookTitle);
        final Book book = searchForBookByTitle(bookTitle);
        if (!book.isEmpty()) {
            logger.info("Book: {} was already registered.", bookTitle);
            return LibraryActionResults.ALREADY_REGISTERED_BOOK;
        }
        logger.info("Book: {} was not found. Registering new book...", bookTitle);
        saveNewBook(bookTitle);
        return LibraryActionResults.SUCCESS;
    }

    /**
     * Saves a new book to the persistence layer.
     *
     * @param bookTitle the title of the new book
     */
    void saveNewBook(String bookTitle) {
        logger.info("Saving new book: {}", bookTitle);
        persistence.saveNewBook(bookTitle);
    }

    /**
     * Searches for a loan associated with the specified book.
     *
     * @param book the book for which to search the loan
     * @return the corresponding {@link Loan} if found; otherwise, an empty loan instance
     */
    public Loan searchForLoanByBook(Book book) {
        logger.info("Searching for loan by book with title: {}", book.title);
        return persistence.searchForLoanByBook(book).orElse(Loan.createEmpty());
    }

    /**
     * Searches for loans associated with the specified borrower.
     *
     * @param borrower the borrower for which to search loans
     * @return a list of {@link Loan} objects, or an empty list if none are found
     */
    public List<Loan> searchForLoanByBorrower(Borrower borrower) {
        logger.info("Searching for loans by borrower with name: {}", borrower.name);
        return persistence.searchForLoanByBorrower(borrower).orElse(new ArrayList<>());
    }

    /**
     * Searches for a borrower by name.
     *
     * @param borrowerName the name of the borrower to search for
     * @return the corresponding {@link Borrower} if found; otherwise, an empty borrower instance
     */
    public Borrower searchForBorrowerByName(String borrowerName) {
        logger.info("Searching for borrower by name: {}", borrowerName);
        return persistence.searchBorrowerDataByName(borrowerName).orElse(Borrower.createEmpty());
    }

    /**
     * Searches for a book by title.
     *
     * @param title the title of the book to search for; must be non-empty
     * @return the corresponding {@link Book} if found; otherwise, an empty book instance
     * @throws IllegalArgumentException if the title is empty
     */
    public Book searchForBookByTitle(String title) {
        if (title.isEmpty()) {
            throw new IllegalArgumentException("when searching for a book, must include a non-empty string for title");
        }
        logger.info("Searching for book with title: {}", title);
        final Book book = persistence.searchBooksByTitle(title).orElse(Book.createEmpty());
        if (book.isEmpty()) {
            logger.info("No book found with title: {}", title);
        } else {
            logger.info("Book found with title: {}", title);
        }
        return book;
    }

    /**
     * Searches for a book by its identifier.
     *
     * @param id the positive identifier of the book
     * @return the corresponding {@link Book} if found; otherwise, an empty book instance
     * @throws IllegalArgumentException if the id is less than 1
     */
    public Book searchForBookById(long id) {
        if (id < 1) {
            throw new IllegalArgumentException("when searching for a book, must include an id of one or greater");
        }
        logger.info("Searching for book with id: {}", id);
        final Book book = persistence.searchBooksById(id).orElse(Book.createEmpty());
        if (book.isEmpty()) {
            logger.info("No book found with id: {}", id);
        } else {
            logger.info("Book found with id: {}", id);
        }
        return book;
    }

    /**
     * Searches for a borrower by its identifier.
     *
     * @param id the positive identifier of the borrower
     * @return the corresponding {@link Borrower} if found; otherwise, an empty borrower instance
     * @throws IllegalArgumentException if the id is less than 1
     */
    public Borrower searchForBorrowerById(long id) {
        if (id < 1) {
            throw new IllegalArgumentException("When searching for a borrower, the id must be 1 or greater.");
        }
        logger.info("Searching for borrower with id: {}", id);
        final Borrower borrower = persistence.searchBorrowersById(id).orElse(Borrower.createEmpty());
        if (borrower.isEmpty()) {
            logger.info("No borrower found with id: {}", id);
        } else {
            logger.info("Borrower found with id: {}", id);
        }
        return borrower;
    }

    /**
     * Creates an empty LibraryUtils instance using an empty persistence layer.
     *
     * @return a LibraryUtils instance with an empty persistence layer
     */
    public static LibraryUtils createEmpty() {
        return new LibraryUtils(PersistenceLayer.createEmpty());
    }

    /**
     * Checks whether the underlying persistence layer is empty.
     *
     * @return {@code true} if the persistence layer is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return persistence.isEmpty();
    }

    /**
     * Deletes a book from the library.
     *
     * @param book the book to delete
     * @return a {@link LibraryActionResults} enum indicating the status of the delete operation
     */
    public LibraryActionResults deleteBook(Book book) {
        logger.info("Deleting book with id: {}, title: {}", book.id, book.title);
        final Book bookInDatabase = searchForBookByTitle(book.title);
        if (bookInDatabase.isEmpty()) {
            logger.info("Book not found in the database; cannot be deleted.");
            return LibraryActionResults.NON_REGISTERED_BOOK_CANNOT_BE_DELETED;
        }
        persistence.deleteBook(book.id);
        logger.info("Book with title: {} and id: {} was deleted", bookInDatabase.title, bookInDatabase.id);
        return LibraryActionResults.SUCCESS;
    }

    /**
     * Deletes a borrower from the library.
     *
     * @param borrower the borrower to delete
     * @return a {@link LibraryActionResults} enum indicating the status of the delete operation
     */
    public LibraryActionResults deleteBorrower(Borrower borrower) {
        logger.info("Deleting borrower with id: {}, name: {}", borrower.id, borrower.name);
        final Borrower borrowerInDatabase = searchForBorrowerByName(borrower.name);
        if (borrowerInDatabase.isEmpty()) {
            logger.info("Borrower not found in the database; cannot be deleted.");
            return LibraryActionResults.NON_REGISTERED_BORROWER_CANNOT_BE_DELETED;
        }
        persistence.deleteBorrower(borrower.id);
        logger.info("Borrower with name: {} and id: {} was deleted", borrowerInDatabase.name, borrowerInDatabase.id);
        return LibraryActionResults.SUCCESS;
    }

    /**
     * Lists all registered books in the library.
     *
     * @return a list of all registered books, or an empty list if none are found
     */
    public List<Book> listAllBooks() {
        logger.info("Received request to list all books");
        return persistence.listAllBooks().orElse(new ArrayList<>());
    }

    /**
     * Lists all registered borrowers in the library.
     *
     * @return a list of all registered borrowers, or an empty list if none are found
     */
    public List<Borrower> listAllBorrowers() {
        logger.info("Received request to list all borrowers");
        return persistence.listAllBorrowers().orElse(new ArrayList<>());
    }

    /**
     * Lists all available (not currently loaned) books in the library.
     *
     * @return a list of available books, or an empty list if none are available
     */
    public List<Book> listAvailableBooks() {
        logger.info("Received request to list available books");
        return persistence.listAvailableBooks().orElse(new ArrayList<>());
    }
}
