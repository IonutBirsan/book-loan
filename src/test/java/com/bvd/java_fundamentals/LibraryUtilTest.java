package com.bvd.java_fundamentals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LibraryUtilTest {

    private static List<String> loans;

    @BeforeAll
    static void setUp() throws IOException {
        loans = LibraryUtil.loadResourceFile("loans/libraryLoans.csv");
    }

    private static BookLoan loanWithAuthor(String author) {
        return new BookLoan(
                "LoanId1",
                "memberId1",
                LocalDate.parse("2026-01-01"),
                "AnyTitle",
                "AnyGenre",
                author,
                31
        );
    }

    private static BookLoan loanWithGenre(String genre) {
        return new BookLoan(
                "LoanId1",
                "memberId1",
                LocalDate.of(2026, 1, 1),
                "Any Title",
                genre,
                "Any Author",
                31
        );
    }

    private static BookLoan loanWithMemberAndGenre(String memberId, String genre) {
        return new BookLoan(
                "LoanId1",
                memberId,
                LocalDate.parse("2026-01-01"),
                "AnyTitle",
                genre,
                "Ionut",
                31
        );
    }

    private static BookLoan loanWithTitle(String title) {
        return new BookLoan(
                "LoanId1",
                "memberId1",
                LocalDate.parse("2026-01-01"),
                title,
                "AnyGenre",
                "anyAuthor",
                31
        );
    }

    @Nested
    class LoadResourceFile {
        @Test
        @DisplayName("loadResourceFile returns a non-null list")
        void shouldReturnNonNullLines() {

            assertNotNull(loans, "Loaded file is not null");
        }

        @Test
        @DisplayName("loadResourceFile returns a non-empty list")
        void shouldReturnNonEmptyLines() {

            assertFalse(loans.isEmpty(), "Loaded file is not empty");
        }

        @Test
        @DisplayName("loadResourceFile returns the expected number of lines")
        void shouldReturnExpectedLineCount() {
            assertEquals(35, loans.size());
        }

        @Test
        @DisplayName("loadResourceFile throws exception when resource is missing")
        void shouldThrowWhenResourceIsMissing() {
            String wrongFilePath = "file/not/found";
            RuntimeException e = assertThrows(RuntimeException.class, () -> LibraryUtil.loadResourceFile(wrongFilePath));

            assertEquals("CSV not found: " + wrongFilePath, e.getMessage());
        }
    }

    @Nested
    class ParseCsvLines {

        @Test
        @DisplayName("parseCsvLines returns map with valid and malformed keys")
        void shouldReturnMapWithValidAndMalformedKeys() {

            Map<String, List<BookLoan>> map = LibraryUtil.parseCsvLines(loans);

            assertTrue(map.containsKey("malformed") && map.containsKey("valid"));
        }

        @Test
        @DisplayName("parseCsvLines correctly parses a single valid CSV row")
        void shouldParseSingleValidRow() {

            List<String> row = List.of("L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ");

            Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(row);

            assertEquals(1, resultMap.get("valid").size());
            assertEquals("L-1005", resultMap.get("valid").get(0).getLoanId());
            assertEquals("M-002", resultMap.get("valid").get(0).getMemberId());
            assertEquals(LocalDate.parse("2024-06-05"), resultMap.get("valid").get(0).getLoanDate());
            assertEquals("Foundation", resultMap.get("valid").get(0).getBookTitle());
            assertEquals("Science Fiction", resultMap.get("valid").get(0).getGenre());
            assertEquals("Isaac Asimov", resultMap.get("valid").get(0).getAuthor());
            assertEquals(14, resultMap.get("valid").get(0).getDaysLoaned());
            assertEquals(0, resultMap.get("malformed").size());
        }

        @Test
        @DisplayName("parseCsvLines classifies malformed CSV rows")
        void shouldClassifyMalformedRows() {

            List<String> row = List.of("L-1005,  ,2024-06-05  Science Fiction , Isaac Asimov , 14 , sdada , fdsfdsfsdf",           // bad line
                    "L-1005, M-002 , Foundation , Science Fiction , Isaac Asimov , 14 ",                                           // missing date
                    "L-1005, M-002 ,06-Ian-2027 , Foundation , Science Fiction , Isaac Asimov , 14 ",                              // bad date
                    "L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14.76 ",                            // bad daysLoan
                    "L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , bla ",                              // daysLoan string
                    "L-1005, M-002 , Science Fiction , Isaac Asimov , 14 ");                                                       // missing fields

            Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(row);

            assertEquals(6, resultMap.get("malformed").size());
            assertEquals(0, resultMap.get("valid").size());
        }

        @Test
        @DisplayName("parseCsvLines separates valid and malformed rows")
        void shouldSplitValidAndMalformedRows() {

            List<String> row = List.of("L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ",
                    "L-1005,  ,2024-06-05  Science Fiction , Isaac Asimov , 14 ");

            Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(row);

            assertEquals(1, resultMap.get("malformed").size());
            assertEquals(1, resultMap.get("valid").size());
        }

        @Test
        @DisplayName("parseCsvLines returns empty lists for empty input")
        void shouldReturnEmptyListsWhenInputIsEmpty() {

            List<String> row = List.of();

            Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(row);

            assertEquals(0, resultMap.get("valid").size());
            assertEquals(0, resultMap.get("malformed").size());
        }
    }

    @Nested
    class LoansByGenre {
        @Test
        @DisplayName("loansByGenre groups and counts loans per genre")
        void shouldGroupAndCountByGenre() {

            List<BookLoan> loans = List.of(
                    loanWithGenre("Science Fiction"),
                    loanWithGenre("Science Fiction"),
                    loanWithGenre("Fantasy"),
                    loanWithGenre("Science Fiction")
            );

            Map<String, Long> result = LibraryUtil.loansByGenre(loans);

            assertEquals(2, result.size());

            assertEquals(1L, result.get("Fantasy"));
            assertEquals(3L, result.get("Science Fiction"));
        }

        @Test
        @DisplayName("loansByGenre returns genres sorted alphabetically")
        void shouldReturnGenresSortedAlphabetically() {

            List<BookLoan> loans = List.of(
                    loanWithGenre("Science Fiction"),
                    loanWithGenre("Fantasy"),
                    loanWithGenre("Science Fiction")
            );

            Map<String, Long> result = LibraryUtil.loansByGenre(loans);

            List<String> keys = new ArrayList<>(result.keySet());
            List<String> sorted = new ArrayList<>(keys);
            Collections.sort(sorted);

            assertEquals(keys, sorted);
        }

        @Test
        @DisplayName("loansByGenre returns empty map for empty input")
        void shouldReturnEmptyMapWhenInputIsEmpty() {

            Map<String, Long> result = LibraryUtil.loansByGenre(List.of());

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("loansByGenre counts empty genre as a valid key")
        void shouldCountEmptyGenreAsKey() {

            List<BookLoan> loans = List.of(
                    loanWithGenre(""),
                    loanWithGenre(""),
                    loanWithGenre("Fantasy"),
                    loanWithGenre("Science Fiction")
            );

            Map<String, Long> result = LibraryUtil.loansByGenre(loans);

            assertEquals(3, result.size());
            assertEquals(2L, result.get(""));
        }
    }

    @Nested
    class TopAuthorsByLoans {
        @Test
        @DisplayName("topAuthorsByLoans returns top N authors ordered by loan count")
        void shouldReturnTopNAuthorsInDescendingOrder() {

            List<BookLoan> loans = List.of(
                    loanWithAuthor("Ionut"),
                    loanWithAuthor("Ionut"),
                    loanWithAuthor("Ionut"),
                    loanWithAuthor("Birsan"),
                    loanWithAuthor("Birsan"),
                    loanWithAuthor("Isaac Asimov")
            );

            List<String> result = LibraryUtil.topAuthorsByLoans(loans, 3);

            assertEquals(3, result.size());        //test size

            assertEquals("Ionut", result.get(0));   // test order results
            assertEquals("Birsan", result.get(1));
            assertEquals("Isaac Asimov", result.get(2));
        }

        @Test
        @DisplayName("topAuthorsByLoans returns all authors when N exceeds total authors ")
        void shouldReturnAllAuthorsWhenNExceedsDistinctAuthors() {

            List<BookLoan> loans = List.of(
                    loanWithAuthor("Ionut"),
                    loanWithAuthor("Ionut"),
                    loanWithAuthor("Birsan"),
                    loanWithAuthor("Isaac Asimov")
            );

            List<String> result = LibraryUtil.topAuthorsByLoans(loans, 7);

            assertEquals(3, result.size());

        }

        @Test
        @DisplayName("topAuthorsByLoans returns empty list when N is zero")
        void shouldReturnEmptyListWhenNIsZero() {

            List<BookLoan> loans = List.of(
                    loanWithAuthor("Ionut"),
                    loanWithAuthor("Birsan")
            );

            List<String> result = LibraryUtil.topAuthorsByLoans(loans, 0);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("topAuthorsByLoans throws exception when N is negative")
        void shouldThrowWhenNIsNegative() {

            List<BookLoan> loans = List.of(
                    loanWithAuthor("Ionut"),
                    loanWithAuthor("Birsan")
            );

            assertThrows(IllegalArgumentException.class, () -> LibraryUtil.topAuthorsByLoans(loans, -5));
        }

        @Test
        @DisplayName("topAuthorsByLoans returns empty list when no loans exist")
        void shouldReturnEmptyListWhenNoLoans() {

            List<BookLoan> loans = List.of();

            List<String> result = LibraryUtil.topAuthorsByLoans(loans, 1);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class MembersWithGenreDiversity {
        @Test
        @DisplayName("membersWithGenreDiversity returns members meeting genre threshold")
        void shouldReturnMembersMeetingThreshold() {

            List<BookLoan> loans = List.of(
                    loanWithMemberAndGenre("Id1", "fiction"),
                    loanWithMemberAndGenre("Id2", "fiction"),
                    loanWithMemberAndGenre("Id1", "SF")
            );

            List<String> result = LibraryUtil.membersWithGenreDiversity(loans, 2);

            assertTrue(result.contains("Id1"));
            assertFalse(result.contains("Id2"));
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("membersWithGenreDiversity returns empty when threshold is not met")
        void shouldReturnEmptyWhenNoMemberMeetsThreshold() {

            List<BookLoan> loans = List.of(
                    loanWithMemberAndGenre("Id1", "fiction"),
                    loanWithMemberAndGenre("Id2", "love"),
                    loanWithMemberAndGenre("Id2", "fiction"),
                    loanWithMemberAndGenre("Id3", "fiction"),
                    loanWithMemberAndGenre("Id1", "SF")
            );

            List<String> result = LibraryUtil.membersWithGenreDiversity(loans, 5);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("membersWithGenreDiversity counts each genre only once per member")
        void shouldCountDistinctGenresOnly() {

            List<BookLoan> loans = List.of(
                    loanWithMemberAndGenre("Id1", "SF"),
                    loanWithMemberAndGenre("Id1", "SF")
            );

            List<String> result = LibraryUtil.membersWithGenreDiversity(loans, 2);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("membersWithGenreDiversity returns empty list for empty input")
        void shouldReturnEmptyWhenNoLoans() {

            List<BookLoan> loans = List.of();

            List<String> result = LibraryUtil.membersWithGenreDiversity(loans, 5);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("membersWithGenreDiversity returns all members when K is zero")
        void shouldReturnAllMembersWhenKIsZero() {

            List<BookLoan> loans = List.of(
                    loanWithMemberAndGenre("Id1", "fiction"),
                    loanWithMemberAndGenre("Id2", "love"),
                    loanWithMemberAndGenre("Id2", "fiction"),
                    loanWithMemberAndGenre("Id3", "fiction"),
                    loanWithMemberAndGenre("Id1", "SF")
            );

            List<String> result = LibraryUtil.membersWithGenreDiversity(loans, 0);

            assertEquals(3, result.size());
            assertTrue(result.contains("Id1"));
            assertTrue(result.contains("Id2"));
            assertTrue(result.contains("Id3"));
        }
    }

    @Nested
    class FindFirstBookContaining {
        @Test
        @DisplayName("findFirstBookContaining returns first matching book")
        void shouldReturnFirstMatchingLoan() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "Ring");

            assertTrue(result.isPresent());
            assertEquals("Lord Of The Rings", result.get().getBookTitle());
        }

        @Test
        @DisplayName("findFirstBookContaining returns empty when no book matches")
        void shouldReturnEmptyWhenNoMatch() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "Fast & Furious");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("findFirstBookContaining is case-insensitive")
        void shouldBeCaseInsensitive() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "hobbIT");

            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("findFirstBookContaining returns first occurrence when multiple match")
        void shouldReturnFirstOccurrenceWhenMultipleMatch() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter 1"),
                    loanWithTitle("Harry Potter 2"),
                    loanWithTitle("Harry Potter 3"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "Harry Potter");

            assertTrue(result.isPresent());
            assertEquals("Harry Potter 1", result.get().getBookTitle());
        }

        @Test
        @DisplayName("findFirstBookContaining returns first loan when search string is empty")
        void shouldReturnFirstLoanWhenSearchIsEmptyString() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "");

            assertTrue(result.isPresent());
            assertEquals("Harry Potter", result.get().getBookTitle());
        }

        @Test
        @DisplayName("findFirstBookContaining returns empty when search string is null")
        void shouldReturnEmptyWhenSearchIsNull() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter 1"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, null);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("findFirstBookContaining returns empty when loan list is empty")
        void shouldReturnEmptyWhenNoLoans() {

            List<BookLoan> loans = List.of();

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "Hobbit");

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class IsBookPresent {
        @Test
        @DisplayName("isBookPresent returns true when book exists")
        void shouldReturnTrueWhenMatchExists() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Boolean result = LibraryUtil.isBookPresent(loans, "The Hobbit");

            assertTrue(result);
        }

        @Test
        @DisplayName("isBookPresent is case-insensitive and supports substring matching")
        void shouldBeCaseInsensitiveAndAllowSubstringMatch() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Boolean result = LibraryUtil.isBookPresent(loans, "hobbit");

            assertTrue(result);
        }

        @Test
        @DisplayName("isBookPresent returns false when search value is null")
        void shouldReturnFalseWhenSearchIsNull() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Boolean result = LibraryUtil.isBookPresent(loans, null);

            assertFalse(result);
        }

        @Test
        @DisplayName("isBookPresent returns false when loan list is empty")
        void shouldReturnFalseWhenNoLoans() {

            List<BookLoan> loans = List.of();

            Boolean result = LibraryUtil.isBookPresent(loans, "Hobbit");

            assertFalse(result);
        }

        @Test
        @DisplayName("isBookPresent returns false when book is not found")
        void shouldReturnFalseWhenNoMatch() {

            List<BookLoan> loans = List.of(
                    loanWithTitle("Harry Potter"),
                    loanWithTitle("Lord Of The Rings"),
                    loanWithTitle("The Hobbit")
            );

            Boolean result = LibraryUtil.isBookPresent(loans, "abc");

            assertFalse(result);
        }
    }

}
