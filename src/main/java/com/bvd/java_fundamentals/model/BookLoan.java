package com.bvd.java_fundamentals.model;

import java.time.LocalDate;

public class BookLoan {

    private String loanId;

    private String memberId;

    private LocalDate loanDate;

    private String bookTitle;

    private String genre;

    private String author;

    private Integer daysLoaned;

    public BookLoan(String loanId, String memberId, LocalDate loanDate, String bookTitle, String genre, String author, Integer daysLoaned) {
        this.loanId = loanId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.bookTitle = bookTitle;
        this.genre = genre;
        this.author = author;
        this.daysLoaned = daysLoaned;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getDaysLoaned() {
        return daysLoaned;
    }

    public void setDaysLoaned(Integer daysLoaned) {
        this.daysLoaned = daysLoaned;
    }

    @Override
    public String toString() {
        return "BookLoan{" +
                "loanId='" + loanId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", loanDate=" + loanDate +
                ", bookTitle='" + bookTitle + '\'' +
                ", genre='" + genre + '\'' +
                ", author='" + author + '\'' +
                ", daysLoaned=" + daysLoaned +
                '}';
    }
}
