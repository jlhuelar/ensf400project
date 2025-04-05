package com.coveros.training.helpers;

import org.apache.logging.log4j.core.util.JsonUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Some simple helper methods for Strings.
 */
public class StringUtils {

    // Private constructor to prevent instantiation.
    private StringUtils() {
        // using a private constructor to hide the implicit public one.
    }

    /**
     * Returns a non-null string.
     * <p>
     * If the input string is {@code null}, an empty string ("") is returned; otherwise,
     * the original string is returned.
     *
     * @param s the input string which may be null
     * @return a non-null string (either the original or an empty string if the input was null)
     */
    public static String makeNotNullable(@Nullable String s) {
        return s == null ? "" : s;
    }

    // A table of some of the values that may need to be escaped in JSON strings.
    static final byte SINGLE_QUOTE    = 39;
    static final byte DOUBLE_QUOTE    = 34;
    static final byte BACKSLASH       = 92;
    static final byte NEW_LINE        = 10;
    static final byte CARRIAGE_RETURN = 13;
    static final byte TAB             = 9;
    static final byte BACKSPACE       = 8;
    static final byte FORM_FEED       = 12;

    /**
     * Given a string, returns a properly escaped version for safe use in JSON.
     *
     * @param value the string value to be escaped
     * @return a JSON-safe string with necessary characters escaped
     */
    public static String escapeForJson(String value) {
        StringBuilder sb = new StringBuilder();
        JsonUtils.quoteAsString(value, sb);
        return sb.toString();
    }
}
