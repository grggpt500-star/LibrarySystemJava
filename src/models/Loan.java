package models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a single borrowing transaction (one member, one book).
 */
public class Loan {

    private static final int DEFAULT_LOAN_DAYS = 14;

    private String    loanId;
    private String    memberId;
    private String    isbn;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null = still active

    public Loan(String loanId, String memberId, String isbn,
                LocalDate borrowDate, LocalDate dueDate) {
        this.loanId     = loanId;
        this.memberId   = memberId;
        this.isbn       = isbn;
        this.borrowDate = (borrowDate != null) ? borrowDate : LocalDate.now();
        this.dueDate    = (dueDate    != null) ? dueDate
                          : this.borrowDate.plusDays(DEFAULT_LOAN_DAYS);
        this.returnDate = null;
    }

    // ── Getters ──────────────────────────────────────────────
    public String    getLoanId()     { return loanId; }
    public String    getMemberId()   { return memberId; }
    public String    getIsbn()       { return isbn; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate()    { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public boolean isActive() {
        return returnDate == null;
    }

    public void close() {
        this.returnDate = LocalDate.now();
    }

    public long daysUntilDue() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }

    @Override
    public String toString() {
        String status = isActive() ? "Active" : "Returned " + returnDate;
        return "Loan " + loanId + ": member=" + memberId
                + " isbn=" + isbn + " due=" + dueDate + " [" + status + "]";
    }
}
