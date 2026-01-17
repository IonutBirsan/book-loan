package com.bvd.java_fundamentals.service;

import com.bvd.java_fundamentals.LibraryUtil;
import com.bvd.java_fundamentals.model.BookLoan;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LoanService {

    private static final String CSV_PATH = "loans/libraryLoans.csv";
    private static final String JSON_PATH = "loans/libraryLoans.json";

    public List<BookLoan> loadAllLoans() {
        try {
            // Load raw data from resources
            List<String> csvLines = LibraryUtil.loadResourceFile(CSV_PATH);
            String jsonContent = LibraryUtil.loadLocalJson(JSON_PATH);

            // Parse CSV & JSON into maps
            Map<String, List<BookLoan>> csv = LibraryUtil.parseCsvLines(csvLines);
            Map<String, List<BookLoan>> json = LibraryUtil.parseJsonLoans(jsonContent);

            // Combine valid loans from both sources
            List<BookLoan> allLoans = new ArrayList<>();
            allLoans.addAll(csv.getOrDefault("valid", List.of()));
            allLoans.addAll(json.getOrDefault("valid", List.of()));

            return allLoans;

        } catch (IOException e) {
            throw new RuntimeException("Failed to load loans from resources", e);
        }
    }
}