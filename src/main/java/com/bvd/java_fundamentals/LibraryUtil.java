package com.bvd.java_fundamentals;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/*
 * Implement the methods below so that the requirements are met.
 */

public class LibraryUtil {

    // private constructor to prevent instantiation
    private LibraryUtil() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    static List<String> loadResourceFile(String filename) throws IOException {

        InputStream is = LibraryUtil.class
                .getClassLoader()
                .getResourceAsStream(filename);

        if (is == null) {
            throw new RuntimeException("CSV not found: " + filename);
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

    static String loadLocalJson(String filename) throws IOException {

        InputStream is = LibraryUtil.class
                .getClassLoader()
                .getResourceAsStream(filename);

        if (is == null) {
            throw new RuntimeException("Json not found: " + filename);
        }

        try (is) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    static Map<String, List<BookLoan>> parseJsonLoans(String json) {

        final List<String> jsonLines;
        try {
            jsonLines = MAPPER.readValue(json,
                    new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                    }
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid Json", e);
        }

        Map<Boolean, List<BookLoan>> partitioned = jsonLines.stream()
                .map(LibraryUtil::parseJsonLineOrNull)
                .collect(Collectors.partitioningBy(Objects::nonNull));

        return Map.of("valid", partitioned.get(true),
                "malformed", partitioned.get(false)
        );
    }

    private static BookLoan parseJsonLineOrNull(String insideJson) {
        try {
            Map<String, Object> node = MAPPER.readValue(insideJson, Map.class);

            String loanId = node.get("loanId").toString().trim();
            String memberId = node.get("memberId").toString().trim();
            LocalDate loanDate = LocalDate.parse(node.get("loanDate").toString().trim());
            String bookTitle = node.get("bookTitle").toString().trim();
            String genre = node.get("genre").toString().trim();
            String author = node.get("author").toString().trim();
            int daysLoaned = Integer.parseInt(node.get("daysLoaned").toString().trim());

            if (loanId.isBlank() || memberId.isBlank() || bookTitle.isBlank()
                    || genre.isBlank() || author.isBlank() || daysLoaned < 0) {
                return null;
            }

            return new BookLoan(
                    loanId,
                    memberId,
                    loanDate,
                    bookTitle,
                    genre,
                    author,
                    daysLoaned
            );

        } catch (Exception e) {
            return null;
        }
    }


//     return a map of "valid" and "malformed" lines as keys and list of BookLoan objects as values
//    protected static Map<String, List<BookLoan>> parseCsvLines(final List<String> file) {
//
//
//        Map<String, List<BookLoan>> map = new HashMap<>();
//        List<BookLoan> malformedList = new ArrayList<>(); // daca e malformed de ce il fac BookLoan?
//
//        List<BookLoan> validList = file.stream()
//                .map(line -> {
//                    try {
//                        String[] parts = line.split(",");
//                        String loanId = parts[0].trim();
//                        String memberId = parts[1].trim();
//                        LocalDate date = LocalDate.parse(parts[2].trim());
//                        String bookTitle = parts[3].trim();
//                        String genre = parts[4].trim();
//                        String author = parts[5].trim();
//                        int daysLoaned = Integer.parseInt(parts[6].trim());
//
//                        return new BookLoan(loanId, memberId, date, bookTitle, genre, author, daysLoaned);
//                    } catch (Exception e) {
//                        String[] parts = line.split(",");
//                        String loanId = parts[0].trim();
//                        BookLoan bl = new BookLoan(loanId != null ? loanId : "malforedLoanId", "", LocalDate.now(), "", "", "", 0);
//                        malformedList.add(bl);
//                        return null;
//                    }
//                })
//                .filter(Objects::nonNull)
//                .toList();
//
//        map.put("valid", validList);
//        map.put("malformed", malformedList);
//        return map;
//    }

    // return a map of "valid" and "malformed" lines as keys and list of BookLoan objects as values
    protected static Map<String, List<BookLoan>> parseCsvLines(final List<String> file) {

        Map<Boolean, List<BookLoan>> separateValidAndMalformed = file.stream()
                .map(line -> line.split(","))
                .map(lineParts -> {
                    try {
                        String loanId = lineParts[0].trim();
                        String memberId = lineParts[1].trim();
                        LocalDate date = LocalDate.parse(lineParts[2].trim());
                        String bookTitle = lineParts[3].trim();
                        String genre = lineParts[4].trim();
                        String author = lineParts[5].trim();
                        int daysLoaned = Integer.parseInt(lineParts[6].trim());
                        return new BookLoan(loanId, memberId, date, bookTitle, genre, author, daysLoaned);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .collect(Collectors.partitioningBy(Objects::nonNull));

        return Map.of("valid", separateValidAndMalformed.get(true),
                "malformed", separateValidAndMalformed.get(false));
    }

    // count loans per genre
    // sorted alphabetically by genre
//    protected static Map<String, Long> loansByGenre(final List<BookLoan> loans) {
//        Map<String, Long> map = loans.stream()
//                .collect(Collectors.groupingBy(BookLoan::getGenre,
//                        Collectors.counting()));
//        Map<String, Long> map2 = map.entrySet().stream()
//                .sorted(Map.Entry.comparingByKey())
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue)
//                );
//        return Collections.emptyMap();
//    }

    protected static Map<String, Long> loansByGenre(final List<BookLoan> loans) {

        return loans.stream()
                .collect(Collectors.groupingBy(
                        BookLoan::getGenre,
                        TreeMap::new,
                        Collectors.counting()));
    }

    // get top "n" authors by number of loans
    protected static List<String> topAuthorsByLoans(final List<BookLoan> loans, final int n) {

        Map<String, Long> authorLoans = loans.stream()
                .collect(Collectors.groupingBy(
                        BookLoan::getAuthor,
                        Collectors.counting()));         // merg si legate

        return authorLoans.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .map(Map.Entry::getKey)
                .toList();
    }

    // get members who borrowed books from at least K genres
    protected static List<String> membersWithGenreDiversity(final List<BookLoan> loans, final int k) {

        Map<String, Set<String>> memberGenres = loans.stream()
                .collect(Collectors.groupingBy(
                        BookLoan::getMemberId,
                        Collectors.mapping(
                                BookLoan::getGenre,
                                Collectors.toSet()          // si astea merg combinate
                        )
                ));

        return memberGenres.entrySet().stream()
                .filter(x -> x.getValue().size() >= k)
                .map(Map.Entry::getKey)
                .toList();
    }

    // find the first book title containing a substring (case-insensitive)
    protected static Optional<BookLoan> findFirstBookContaining(final List<BookLoan> loans, final String book) {

        if (book == null) {
            return Optional.empty();
        }

        return loans.stream()
                .filter(x -> x.getBookTitle().toLowerCase().contains(book.toLowerCase()))
                .findFirst();
    }

    // checks if the book is present in the loans (case-insensitive)
    protected static Boolean isBookPresent(final List<BookLoan> loans, final String book) {

        if (book == null) {
            return false;
        }

        return loans.stream()
                .anyMatch(x -> x.getBookTitle().toLowerCase().contains(book.toLowerCase()));
    }

}
