package com.coveros.training.library.domainobjects;

import com.coveros.training.helpers.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Represents the data that we consider full and complete to define
 * a particular book in the library. This coincides neatly with the details in the database.
 */
public final class Book {

    /**
     * The title of the book.
     */
    public final String title;

    /**
     * The identifier number in our database.
     */
    public final long id;

    /**
     * Constructs a Book with the given id and title.
     *
     * @param id    the identifier number
     * @param title the title of the book
     */
    public Book(long id, String title) {
        this.title = title;
        this.id = id;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Book rhs = (Book) obj;
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(title, rhs.title)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder(13, 33)
                .append(id)
                .append(title)
                .toHashCode();
    }

    /**
     * Returns the book details formatted as a JSON string.
     *
     * @return a JSON string representing the book
     */
    public final String toOutputString() {
        return String.format("{\"Title\": \"%s\", \"Id\": \"%s\"}", StringUtils.escapeForJson(title), id);
    }

    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates an empty Book.
     *
     * @return an empty Book instance
     */
    public static Book createEmpty() {
        return new Book(0, "");
    }

    /**
     * Checks if the book is empty.
     *
     * @return {@code true} if this book is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.equals(createEmpty());
    }
}
