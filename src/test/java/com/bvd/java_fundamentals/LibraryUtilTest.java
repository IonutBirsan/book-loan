package com.bvd.java_fundamentals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static com.bvd.java_fundamentals.LibraryUtil.parseCsvLines;
import static org.junit.jupiter.api.Assertions.*;

class LibraryUtilTest {

    private static List<String> loans;

    @BeforeAll
    static void setUp() throws IOException {
        loans = LibraryUtil.loadResourceFile("loans/libraryLoans.csv");
    }

    private static BookLoan loan(String author) {
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

    private static BookLoan createBookLoanWithDiffMembAndGenre(String memberId, String genre) {
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

    private static BookLoan createBookLoanWithDiffTitle(String title) {
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
        @DisplayName("Method 3 , loansByGenre -> test grouping")
        void mapByGenre() {

            List<String> list = List.of("L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ",
                    "L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ",
                    "L-1005, M-002 ,2024-06-05 , Foundation , Fantasy , Isaac Asimov , 14 ",
                    "L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ");

            Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(list);

            Map<String, Long> result = LibraryUtil.loansByGenre(resultMap.get("valid"));

            assertEquals(1L, result.get("Fantasy"));
            assertEquals(3L, result.get("Science Fiction"));
            assertEquals(2, result.size());

        }

        @Test
        @DisplayName("Method 3 , loansByGenre -> test if result keys are alphabetically")
        void testIfGenresAreSorted() {

            List<String> list = List.of("L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ",
                    "L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ",
                    "L-1005, M-002 ,2024-06-05 , Foundation , Fantasy , Isaac Asimov , 14 ",
                    "L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ");

            Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(list);

            Map<String, Long> result = LibraryUtil.loansByGenre(resultMap.get("valid"));

            List<String> keys = new ArrayList<>(result.keySet());
            List<String> sorted = new ArrayList<>(keys);
            Collections.sort(sorted);

            assertEquals(keys, sorted);
        }

        @Test
        @DisplayName("Method 3 , loansByGenre -> empty input list")
        void mapByGenreEmpty() {

            List<BookLoan> list = List.of();

            Map<String, Long> result = LibraryUtil.loansByGenre(list);

            assertNotNull(result, "Result map should not be null");
            assertTrue(result.isEmpty(), "Result map should be empty for empty input");

        }

        @Test
        @DisplayName("Method 3 , loansByGenre -> test empty genre grouping")
        void mapByEmptyGenre() {

            List<String> list = List.of("L-1005, M-002 ,2024-06-05 , Foundation ,  , Isaac Asimov , 14 ",
                    "L-1005, M-002 ,2024-06-05 , Foundation ,  , Isaac Asimov , 14 ",
                    "L-1005, M-002 ,2024-06-05 , Foundation , Fantasy , Isaac Asimov , 14 ",
                    "L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ");

            Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(list);

            Map<String, Long> result = LibraryUtil.loansByGenre(resultMap.get("valid"));

            assertEquals(2L, result.get(""));
        }
    }

    @Nested
    class TopAuthorsByLoans {
        @Test
        @DisplayName("Method 4 , topAuthorsByLoans -> test result size and order")
        void testTopNAuthors() {

            List<BookLoan> loans = List.of(
                    loan("Ionut"),
                    loan("Ionut"),
                    loan("Ionut"),
                    loan("Birsan"),
                    loan("Birsan"),
                    loan("Isaac Asimov")
            );

            List<String> result = LibraryUtil.topAuthorsByLoans(loans, 3);

            assertEquals(3, result.size());        //test size

            assertEquals("Ionut", result.get(0));   // test order results
            assertEquals("Birsan", result.get(1));
            assertEquals("Isaac Asimov", result.get(2));
        }

        @Test
        @DisplayName("Method 4 , topAuthorsByLoans -> test when n > authors.size ")
        void testTopNAuth() {

            List<BookLoan> loans = List.of(
                    loan("Ionut"),
                    loan("Ionut"),
                    loan("Birsan"),
                    loan("Isaac Asimov")
            );

            List<String> result = LibraryUtil.topAuthorsByLoans(loans, 7);

            assertEquals(3, result.size());

        }

        @Test
        @DisplayName("Method 4 , topAuthorsByLoans -> test when n=0  returns empty list")
        void testTopAUthWhenNisZero() {

            List<BookLoan> loans = List.of(
                    loan("Ionut"),
                    loan("Birsan")
            );

            List<String> result = LibraryUtil.topAuthorsByLoans(loans, 0);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Method 4 , topAuthorsByLoans -> test when n<0")
        void testTopAUthWhenNisNegative() {

            List<BookLoan> loans = List.of(
                    loan("Ionut"),
                    loan("Birsan")
            );

            assertThrows(IllegalArgumentException.class, () -> LibraryUtil.topAuthorsByLoans(loans, -5));
        }

        @Test
        @DisplayName("Method 4 , topAuthorsByLoans -> test when list is empty")
        void testTopAUthWhenListIsEmpty() {

            List<BookLoan> loans = List.of();

            List<String> result = LibraryUtil.topAuthorsByLoans(loans, 1);

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class MembersWithGenreDiversity {
        @Test
        @DisplayName("Method 5 , membersWithGenreDiversity -> test members over and under k ")
        void membersWIthGenreDiversity() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffMembAndGenre("Id1", "fiction"),
                    createBookLoanWithDiffMembAndGenre("Id2", "fiction"),
                    createBookLoanWithDiffMembAndGenre("Id1", "SF")
            );

            List<String> result = LibraryUtil.membersWithGenreDiversity(loans, 2);

            assertTrue(result.contains("Id1"));
            assertFalse(result.contains("Id2"));
            assertEquals(1, result.size());
        }


        @Test
        @DisplayName("Method 5 , membersWithGenreDiversity -> test members below threshold")
        void testWithMembersUnderThreshold() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffMembAndGenre("Id1", "fiction"),
                    createBookLoanWithDiffMembAndGenre("Id2", "love"),
                    createBookLoanWithDiffMembAndGenre("Id2", "fiction"),
                    createBookLoanWithDiffMembAndGenre("Id3", "fiction"),
                    createBookLoanWithDiffMembAndGenre("Id1", "SF")
            );

            List<String> result = LibraryUtil.membersWithGenreDiversity(loans, 5);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Method 5 , membersWithGenreDiversity -> test set - genre x 2 counter once")
        void testEachGenreIsCountedOnce() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffMembAndGenre("Id1", "SF"),
                    createBookLoanWithDiffMembAndGenre("Id1", "SF")
            );

            List<String> result = LibraryUtil.membersWithGenreDiversity(loans, 2);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Method 5 , membersWithGenreDiversity -> test given empty list returns empty list")
        void testEmptyList() {

            List<BookLoan> loans = List.of();

            List<String> result = LibraryUtil.membersWithGenreDiversity(loans, 5);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Method 5 , membersWithGenreDiversity -> test when k=0 all members returned")
        void testAllReturnWhenThresholdIsZero() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffMembAndGenre("Id1", "fiction"),
                    createBookLoanWithDiffMembAndGenre("Id2", "love"),
                    createBookLoanWithDiffMembAndGenre("Id2", "fiction"),
                    createBookLoanWithDiffMembAndGenre("Id3", "fiction"),
                    createBookLoanWithDiffMembAndGenre("Id1", "SF")
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
        @DisplayName("Method 6 , findFirstBookContaining -> test book title contains a word")
        void checkHappyFlow() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "Ring");

            assertTrue(result.isPresent());
            assertEquals("Lord Of The Rings", result.get().getBookTitle());
        }

        @Test
        @DisplayName("Method 6 , findFirstBookContaining -> finds no book")
        void checkIfFindsNoBook() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "Fast & Furious");

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Method 6 , findFirstBookContaining -> test case insensitive")
        void checkCaseInsensitive() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "hobbIT");

            assertTrue(result.isPresent());
        }

        @Test
        @DisplayName("Method 6 , findFirstBookContaining -> test first find")
        void checkFirstFind() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter 1"),
                    createBookLoanWithDiffTitle("Harry Potter 2"),
                    createBookLoanWithDiffTitle("Harry Potter 3"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "Harry Potter");

            assertTrue(result.isPresent());
            assertEquals("Harry Potter 1", result.get().getBookTitle());
        }

        @Test
        @DisplayName("Method 6 , findFirstBookContaining -> test search empty string")
        void checkWhenSearchEmptyString() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter 1"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "");

            assertTrue(result.isPresent());
            assertEquals("Harry Potter 1", result.get().getBookTitle());
        }

        @Test
        @DisplayName("Method 6 , findFirstBookContaining -> test when search is null")
        void checkWhenSearchIsNull() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter 1"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, null);

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Method 6 , findFirstBookContaining -> test when list is empty")
        void checkWhenListIsEmpty() {

            List<BookLoan> loans = List.of();

            Optional<BookLoan> result = LibraryUtil.findFirstBookContaining(loans, "Hobbit");

            assertTrue(result.isEmpty());
        }
    }

    @Nested
    class IsBookPresent {
        @Test
        @DisplayName("Method 7 , isBookPresent -> check book is present")
        void checkIfBookIsPresent() {


            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Boolean result = LibraryUtil.isBookPresent(loans, "The Hobbit");

            assertTrue(result);
        }

        @Test
        @DisplayName("Method 7 , isBookPresent -> check case insesitive or substring")
        void checkCaseInsesitiveAndSubString() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Boolean result = LibraryUtil.isBookPresent(loans, "hobbit");

            assertTrue(result);
        }

        @Test
        @DisplayName("Method 7 , isBookPresent -> check book is null")
        void checkIfBookIsNull() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Boolean result = LibraryUtil.isBookPresent(loans, null);

            assertFalse(result);
        }

        @Test
        @DisplayName("Method 7 , isBookPresent -> check empty loan list")
        void checkEmptyLoanList() {

            List<BookLoan> loans = List.of();

            Boolean result = LibraryUtil.isBookPresent(loans, "Hobbit");

            assertFalse(result);
        }

        @Test
        @DisplayName("Method 7 , isBookPresent -> finds no result")
        void checkAndCantFindTheBook() {

            List<BookLoan> loans = List.of(
                    createBookLoanWithDiffTitle("Harry Potter"),
                    createBookLoanWithDiffTitle("Lord Of The Rings"),
                    createBookLoanWithDiffTitle("The Hobbit")
            );

            Boolean result = LibraryUtil.isBookPresent(loans, "abc");

            assertFalse(result);
        }
    }

}
