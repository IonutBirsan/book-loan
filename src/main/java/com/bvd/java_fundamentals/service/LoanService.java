package com.bvd.java_fundamentals.service;

import com.bvd.java_fundamentals.LibraryUtil;
import com.bvd.java_fundamentals.model.BookLoan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);
    private static final String CSV_PATH = "loans/libraryLoans.csv";
    private static final String JSON_PATH = "loans/libraryLoans.json";

    private final BookLoanProducer producer;

    public LoanService(BookLoanProducer producer) {
        this.producer = producer;
    }

    public List<BookLoan> loadAllLoans() {
        logger.debug("Starting to load book loans from data sources");

        try {
            // Load raw data from resources
            logger.debug("Loading CSV file from: {}", CSV_PATH);
            List<String> csvLines = LibraryUtil.loadResourceFile(CSV_PATH);

            logger.debug("Loading JSON file from: {}", JSON_PATH);
            String jsonContent = LibraryUtil.loadLocalJson(JSON_PATH);

            // Parse CSV & JSON into maps
            logger.debug("Parsing CSV data");
            Map<String, List<BookLoan>> csv = LibraryUtil.parseCsvLines(csvLines);

            logger.debug("Parsing JSON data");
            Map<String, List<BookLoan>> json = LibraryUtil.parseJsonLoans(jsonContent);

            // Combine valid loans from both sources
            List<BookLoan> allLoans = new ArrayList<>();
            allLoans.addAll(csv.getOrDefault("valid", List.of()));
            allLoans.addAll(json.getOrDefault("valid", List.of()));

            int malformedCsv = csv.getOrDefault("malformed", List.of()).size();
            int malformedJson = json.getOrDefault("malformed", List.of()).size();

            logger.info("Loaded {} valid book loans (CSV: {}, JSON: {})",
                    allLoans.size(),
                    csv.getOrDefault("valid", List.of()).size(),
                    json.getOrDefault("valid", List.of()).size());

            if (malformedCsv > 0 || malformedJson > 0) {
                logger.warn("Found {} malformed entries (CSV: {}, JSON: {})",
                        malformedCsv + malformedJson, malformedCsv, malformedJson);
            }

            return allLoans;

        } catch (IOException e) {
            logger.error("Failed to load loan data from files: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to load loans from resources", e);
        }
    }

    public int publishAllLoans() {
        logger.info("Starting to publish all loans to Kafka");

        List<BookLoan> loans = loadAllLoans();
        int successCount = 0;
        int failCount = 0;

        for (BookLoan loan : loans) {
            try {
                producer.send(loan);
                successCount++;

                if (successCount % 10 == 0) {
                    logger.debug("Published {} loans so far...", successCount);
                }
            } catch (Exception e) {
                failCount++;
                logger.error("Failed to publish loan {}: {}", loan.getLoanId(), e.getMessage());
            }
        }

        if (failCount > 0) {
            logger.warn("Published {}/{} loans successfully, {} failed",
                    successCount, loans.size(), failCount);
        } else {
            logger.info("Successfully published all {} loans to Kafka", successCount);
        }

        return successCount;
    }
}