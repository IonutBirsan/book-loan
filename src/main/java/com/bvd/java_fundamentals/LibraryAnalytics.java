package com.bvd.java_fundamentals;

import java.io.IOException;
import java.util.List;

import static com.bvd.java_fundamentals.LibraryUtil.findFirstBookContaining;
import static com.bvd.java_fundamentals.LibraryUtil.isBookPresent;
import static com.bvd.java_fundamentals.LibraryUtil.loansByGenre;
import static com.bvd.java_fundamentals.LibraryUtil.membersWithGenreDiversity;
import static com.bvd.java_fundamentals.LibraryUtil.parseCsvLines;
import static com.bvd.java_fundamentals.LibraryUtil.topAuthorsByLoans;

/*
 * The scenario is a library system managing book loans.
 * The focus is collections, streams, String handling, OO basics, and edge cases.

 * The CSV library data has to be loaded from a give file.
 * Each element (considered to be a line in CSV) has the following format:
        loanId,memberId,loanDate,bookTitle,genre,author,daysLoaned
            - loanDate is ISO date yyyy-MM-dd
            - daysLoaned is an integer

 * Tasks:
    - Implement BookLoan class (decide list of fields and methods based on the tasks requirements)
    - Implement logic to load the CSV data from a file located in the resources folder
    - Implement business logic for the methods from LibraryUtil utility class
        * loadResourceFile: loads the CSV file from resources folder
        * parseCsvLines: parse the loaded CSV file into a map of string and list of BookLoan objects, the keys should be "valid" and "malformed"
        * loansByGenre: count loans per genre, sorted alphabetically by genre.
        * topAuthorsByLoans: get top "n" authors by number of loans.
        * membersWithGenreDiversity: get members who borrowed books from at least "k" genres.
        * findFirstBookContaining: find the first book title containing a substring (case-insensitive).
        * isBookPresent: checks if the book is present in the loans (case-insensitive).
     - Extra mile:
        * Load the json file instead of CSV, serialize into BookLoan object and adapt the parsing logic accordingly using ObjectMapper from Jackson library.
        * Write unit tests in LibraryUtilTest for utility methods to ensure all edge cases are covered.
 */
public class LibraryAnalytics {

    protected static List<String> loadedFile;
    static String FilePath = "loans/libraryLoans.csv";

    static {
        try {
            loadedFile = LibraryUtil.loadResourceFile(FilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /* The expected output of the main method is:
        Loaded 34 entries from the CSV file.
        Valid loans: 31
        Malformed loans: 3
        Loans by genre: {Classic=8, Dystopian=5, Fantasy=8, Horror=4, Science Fiction=7}
        Top 2 authors: [J.R.R. Tolkien, Fyodor Dostoevsky]
        Members with genre diversity (>=3 genres): [M-005, M-006, M-007, M-008, M-009, M-010]
        First book containing 'Dune': Optional[BookLoan{loanId='L-1006', memberId='M-004', loanDate=2024-06-06, bookTitle='Dune', genre='Science Fiction', author='Frank Herbert', daysLoaned=14}]
        Is book present: 'Harry Potter': false
    */
    public static void main(String[] args) {
        var loans = parseCsvLines(loadedFile);
        List<BookLoan> loanList = loans.get("valid");

        System.out.printf("Loaded %s entries from the CSV file.%n", loadedFile.size());
        System.out.println("Valid loans: " + loanList.size());
        System.out.println("Malformed loans: " + loans.get("malformed").size());
        System.out.println("Loans by genre: " + loansByGenre(loanList));
        System.out.println("Top 2 authors: " + topAuthorsByLoans(loanList, 2));
        System.out.println("Members with genre diversity (>=3 genres): " + membersWithGenreDiversity(loanList, 3));
        System.out.println("First book containing 'Dune': " + findFirstBookContaining(loanList, "Dune"));
        System.out.println("Is book present: 'Harry Potter': " + isBookPresent(loanList, "Harry Potter"));
    }
}
