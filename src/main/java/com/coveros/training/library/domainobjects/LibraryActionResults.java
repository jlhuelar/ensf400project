package com.coveros.training.library.domainobjects;

/**
 * Represents the possible outcomes of library-related business functions.
 * <p>
 * For example, if you attempt to register a book that is already registered,
 * the operation may return {@code ALREADY_REGISTERED_BOOK}.
 * </p>
 */
public enum LibraryActionResults {

    /** The book is already registered in the library. */
    ALREADY_REGISTERED_BOOK,

    /**
     * The book is not registered in the library,
     * so it cannot be deleted.
     */
    NON_REGISTERED_BOOK_CANNOT_BE_DELETED,

    /**
     * The borrower is not registered in the library,
     * so they cannot be deleted.
     */
    NON_REGISTERED_BORROWER_CANNOT_BE_DELETED,

    /** The borrower is already registered in the library. */
    ALREADY_REGISTERED_BORROWER,

    /** The book is not registered in the library. */
    BOOK_NOT_REGISTERED,

    /** The borrower is not registered in the library. */
    BORROWER_NOT_REGISTERED,

    /**
     * The book is currently checked out by someone,
     * so it cannot be lent again.
     */
    BOOK_CHECKED_OUT,

    /** The operation was successful. */
    SUCCESS,

    /**
     * No book title was provided where one was required.
     */
    NO_BOOK_TITLE_PROVIDED,

    /**
     * No borrower information was provided where it was required.
     */
    NO_BORROWER_PROVIDED,

    /**
     * A null value is used when initializing a variable of this type.
     */
    NULL
}
