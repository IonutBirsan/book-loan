package com.bvd.java_fundamentals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("Method 1 , loadResourceFile -> not null")
    void fileNotNull() {

        assertNotNull(loans, "Loaded file is not null");
    }

    @Test
    @DisplayName("Method 1 , loadResourceFile -> not empty")
    void fileNotEmpty() {

        assertFalse(loans.isEmpty(), "Loaded file is not empty");
    }

    @Test
    @DisplayName("Method 1 , loadResourceFile -> file size")
    void checkTotalLines() {
        assertEquals(35, loans.size());
    }

    @Test
    @DisplayName("Method 1 , loadResourceFile -> throw exception when file is missing")
    void throwExceptionWhenFileIsMissing() {
        String wrongFilePath = "file/not/found";
        RuntimeException e = assertThrows(RuntimeException.class, () -> LibraryUtil.loadResourceFile(wrongFilePath));

        assertEquals("CSV not found: " + wrongFilePath, e.getMessage());
    }


    @Test
    @DisplayName("Method 2 , parseCsvLines -> return map with 2 keys")
    void returnMapWithMalformedAndValid() {

        Map<String, List<BookLoan>> map = LibraryUtil.parseCsvLines(loans);

        assertTrue(map.containsKey("malformed") && map.containsKey("valid"));
    }

    @Test
    @DisplayName("Method 2 , parseCsvLines -> check parsing of one valid row")
    void checkParsingAListOfOneValidRow() {

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
    @DisplayName("Method 2 , parseCsvLines -> check parsing of malformed rows")
    void checkMalformedRow() {

        List<String> row = List.of("L-1005,  ,2024-06-05  Science Fiction , Isaac Asimov , 14 , sdada , fdsfdsfsdf",           // bad line
                "L-1005, M-002 , Foundation , Science Fiction , Isaac Asimov , 14 ",                                           //missing date
                "L-1005, M-002 ,06-Ian-2027 , Foundation , Science Fiction , Isaac Asimov , 14 ",                              // bad date
                "L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14.76 ",                            //bad daysLoan
                "L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , bla ",                              //daysLoan string
                "L-1005, M-002 , Science Fiction , Isaac Asimov , 14 ");                                                       //missing fields

        Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(row);

        assertEquals(6, resultMap.get("malformed").size());
        assertEquals(0, resultMap.get("valid").size());
    }

    @Test
    @DisplayName("Method 2 , parseCsvLines -> check parsing of one malformed row and one valid row")
    void checkMalformedRowAndValidROw() {

        List<String> row = List.of("L-1005, M-002 ,2024-06-05 , Foundation , Science Fiction , Isaac Asimov , 14 ",
                "L-1005,  ,2024-06-05  Science Fiction , Isaac Asimov , 14 ");

        Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(row);

        assertEquals(1, resultMap.get("malformed").size());
        assertEquals(1, resultMap.get("valid").size());
    }

    @Test
    @DisplayName("Method 2 , parseCsvLines -> empty list")
    void returnNoExceptionWIthEmptyList() {

        List<String> row = List.of();

        Map<String, List<BookLoan>> resultMap = LibraryUtil.parseCsvLines(row);

        assertEquals(0, resultMap.get("valid").size());
        assertEquals(0, resultMap.get("malformed").size());
    }

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
        assertEquals(2,result.size());

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
