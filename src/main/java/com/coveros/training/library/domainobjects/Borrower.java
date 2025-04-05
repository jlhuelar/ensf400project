package com.coveros.training.library.domainobjects;

import com.coveros.training.helpers.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An immutable data value representing the data for a borrower.
 * <p>
 * A borrower is a person who borrows a book from a library.
 * Note that we make our fields public because they are final,
 * so there's no need to have methods wrapping them.
 */
public final class Borrower {

    /**
     * The identifier for this borrower in the database.
     */
    public final long id;

    /**
     * The name of the borrower.
     */
    public final String name;

    /**
     * Constructs a Borrower with the given id and name.
     *
     * @param id   the identifier for the borrower
     * @param name the name of the borrower
     */
    public Borrower(long id, String name) {
        this.id = id;
        this.name = name;
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
        Borrower rhs = (Borrower) obj;
        return new EqualsBuilder()
                .append(id, rhs.id)
                .append(name, rhs.name)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .toHashCode();
    }

    /**
     * Returns the borrower details formatted as a JSON string.
     *
     * @return a JSON string representing the borrower
     */
    public final String toOutputString() {
        return String.format("{\"Name\": \"%s\", \"Id\": \"%s\"}", StringUtils.escapeForJson(name), id);
    }

    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Creates an empty Borrower.
     *
     * @return an empty Borrower instance
     */
    public static Borrower createEmpty() {
        return new Borrower(0, "");
    }

    /**
     * Checks if the borrower is empty.
     *
     * @return {@code true} if this borrower is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return this.equals(createEmpty());
    }
}
