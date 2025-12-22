package com.bvd.java_fundamentals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;

/*
 * Implement the methods below so that the requirements are met.
 */

public class LibraryUtil {

    // private constructor to prevent instantiation
    private LibraryUtil() {
    }


    static List<String> loadResourceFile() throws IOException {

        // filename ??

        InputStream is = LibraryUtil.class
                .getClassLoader()
                .getResourceAsStream("loans/libraryLoans.csv");

        if (is == null) {
            throw new RuntimeException("CSV not found");
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines;
    }


    // return a map of "valid" and "malformed" lines as keys and list of BookLoan objects as values
    protected static Map<String, List<BookLoan>> parseCsvLines(final List<String> file) {

        System.out.println(file);
        Map<String, List<BookLoan>> map = new HashMap<>();


        List<BookLoan> validList = file.stream()
                .map(line -> {
                    try {
                        String[] parts = line.split(",");
                        String loanId = parts[0];
                        String memberId = parts[1];
                        LocalDate date = LocalDate.parse(parts[2]);
                        String bookTitle = parts[3];
                        String genre = parts[4];
                        String author = parts[5];
                        Integer daysLoaned = Integer.parseInt(parts[6]);

                        return new BookLoan(loanId, memberId, date, bookTitle, genre, author, daysLoaned);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        List<BookLoan> malformedList = new ArrayList<>(); // daca e malformed de ce il fac BookLoan?

        map.put("valid", validList);
        map.put("malformed", malformedList);
        return map;
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
