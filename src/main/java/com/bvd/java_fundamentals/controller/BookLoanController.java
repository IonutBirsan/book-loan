package com.bvd.java_fundamentals.controller;

import com.bvd.java_fundamentals.model.BookLoan;
import com.bvd.java_fundamentals.service.LoanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class BookLoanController {

    private static final Logger logger = LoggerFactory.getLogger(BookLoanController.class);
    private final LoanService loanService;

    public BookLoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<BookLoan> getAllLoans() {
        logger.info("Received request to retrieve all book loans");
        List<BookLoan> loans = loanService.loadAllLoans();
        logger.info("Successfully retrieved {} book loans", loans.size());
        return loans;
    }

    @PostMapping("/publish")
    public String publishAllLoans() {
        logger.info("Received request to publish all loans to Kafka");
        int count = loanService.publishAllLoans();
        logger.info("Successfully published {} book loans to Kafka topic", count);
        return "Published " + count + " loans to Kafka";
    }
}