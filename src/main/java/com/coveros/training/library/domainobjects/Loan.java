package com.coveros.training.library.domainobjects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.Date;

/**
 * Represents a complete record of a loan of a book to a borrower in the library.
 * <p>
 * This data structure corresponds directly to the details stored in the database.
 * </p>
 */
public final class Loan {

    /**
     * The date the book was checked out.
     */
    public final Date checkoutDate;

    /**
     * The book that is checked out.
     */
    public final Book book;

    /**
     * The borrower who has checked out the book.
     */
    public final Borrower borrower;

    /**
     * The identifier of this loan in the database.
     */
    public final long id;

    /**
     * Constructs a new Loan with the given details.
     *
     * @param book         the book that is checked out
     * @param borrower     the borrower who has checked out the book
     * @param id           the unique identifier for this loan
     * @param checkoutDate the date the book was checked out
     */
    public Loan(Book book, Borrower borrower, long id, Date checkoutDate) {
        this.book = book;
        this.borrower = borrower;
        this.id = id;
        this.checkoutDate = checkoutDate;
    }

    /**
     * Compares this Loan with the specified object for equality.
     *
     * @param obj the object to compare with
     * @return {@code true} if the specified object is equal to this Loan, {@code false} otherwise
     */
    @Override
    public final boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Loan rhs = (Loan) obj;
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(book, rhs.book)
                .append(borrower, rhs.borrower)
                .append(checkoutDate, rhs.checkoutDate)
                .isEquals();
    }

    /**
     * Returns a hash code value for this Loan.
     *
     * @return a hash code value based on the book, borrower, id, and checkout date
     */
    @Override
    public final int hashCode() {
        return new HashCodeBuilder(5, 21)
                .append(book)
                .append(borrower)
                .append(id)
                .append(checkoutDate)
                .toHashCode();
    }

    /**
     * Returns a string representation of this Loan.
     *
     * @return a string representation of this Loan
     */
    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates an empty Loan instance.
     * <p>
     * An empty loan is defined as a loan with an empty book, an empty borrower, an id of 0,
     * and a checkout date of the epoch (new Date(0)).
     * </p>
     *
     * @return an empty {@code Loan} instance
     */
    public static Loan createEmpty() {
        return new Loan(Book.createEmpty(), Borrower.createEmpty(), 0, new Date(0));
    }

    /**
     * Determines whether this Loan instance is empty.
     *
     * @return {@code true} if this Loan is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.equals(createEmpty());
    }
}
