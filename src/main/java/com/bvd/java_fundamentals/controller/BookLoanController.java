package com.bvd.java_fundamentals.controller;

import com.bvd.java_fundamentals.model.BookLoan;
import com.bvd.java_fundamentals.service.LoanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class BookLoanController {

    private final LoanService loanService;

    public BookLoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<BookLoan> getAllLoans() {
        return loanService.loadAllLoans();
    }

    @PostMapping("/publish")
    public String publishAllLoans() {
        int count = loanService.publishAllLoans();
        return "Published " + count + " loans to Kafka";
    }
}
