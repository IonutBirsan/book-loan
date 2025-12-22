package com.bvd.java_fundamentals;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 * Implement the methods below so that the requirements are met.
 */
public class LibraryUtil {

    // private constructor to prevent instantiation
    private LibraryUtil() {
    }

    // load resource file from resources folder
    static List<String> loadResourceFile(final String fileName) {
        // Write your code here and replace the return statement
        return Collections.emptyList();
    }

    // retrieve loans from csv lines
    // return a map of "valid" and "malformed" lines as keys and list of BookLoan objects as values
    protected static Map<String, List<BookLoan>> parseCsvLines(final List<String> file) {
        // Write your code here and replace the return statement
        return Collections.emptyMap();
    }

    // count loans per genre
    // sorted alphabetically by genre
    protected static Map<String, Long> loansByGenre(final List<BookLoan> loans) {
        // Write your code here and replace the return statement
        return Collections.emptyMap();
    }

    // get top "n" authors by number of loans
    protected static List<String> topAuthorsByLoans(final List<BookLoan> loans, final int n) {
        // Write your code here and replace the return statement
        return Collections.emptyList();
    }

    // get members who borrowed books from at least K genres
    protected static List<String> membersWithGenreDiversity(final List<BookLoan> loans, final int k) {
        // Write your code here and replace the return statement
        return Collections.emptyList();
    }

    // find the first book title containing a substring (case-insensitive)
    protected static Optional<BookLoan> findFirstBookContaining(final List<BookLoan> loans, final String book) {
        // Write your code here and replace the return statement
        return Optional.empty();
    }

    // checks if the book is present in the loans (case-insensitive)
    protected static Boolean isBookPresent(final List<BookLoan> loans, final String book) {
        // Write your code here and replace the return statement
        return null;
    }
}
