package com.bvd.java_fundamentals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LibraryUtilTest {

    private static List<String> loans;

    @BeforeAll
    static void setUp() {
        loans = LibraryAnalytics.loadedFile;
    }

    @Test
    void testLoadResourceFile_returnsFile() {
        assertNotNull(loans);
        assertFalse(loans.isEmpty());
        assertEquals(34, loans.size());
    }
}
