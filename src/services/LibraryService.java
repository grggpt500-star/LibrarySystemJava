package services;

import dao.BookDAO;
import dao.LoanDAO;
import dao.MemberDAO;
import models.*;
import utils.Validator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Business logic layer.
 * Coordinates all library operations using DAO objects.
 * Enforces all business rules (borrow limits, availability, etc.).
 */
public class LibraryService {

    private BookDAO   bookDAO   = new BookDAO();
    private MemberDAO memberDAO = new MemberDAO();
    private LoanDAO   loanDAO   = new LoanDAO();

    // ═══════════════════════════════════════════════════════════
    // BOOK MANAGEMENT
    // ═══════════════════════════════════════════════════════════

    public Book addBook(String isbn, String title, String author, int copies) {
        Validator.notEmpty(isbn,   "ISBN");
        Validator.notEmpty(title,  "Title");
        Validator.notEmpty(author, "Author");
        Validator.positiveInt(copies, "Copies");
        Book book = new Book(isbn.trim(), title.trim(), author.trim(), copies);
        bookDAO.add(book);
        return book;
    }

    public Book updateBook(String isbn, String newTitle, String newAuthor) {
        Book book = getBookOrThrow(isbn);
        if (newTitle  != null && !newTitle.trim().isEmpty())
            book.setTitle(newTitle.trim());
        if (newAuthor != null && !newAuthor.trim().isEmpty())
            book.setAuthor(newAuthor.trim());
        bookDAO.update(book);
        return book;
    }

    public boolean removeBook(String isbn) {
        getBookOrThrow(isbn);
        return bookDAO.delete(isbn);
    }

    public List<Book> searchBooks(String keyword, String field) {
        Validator.notEmpty(keyword, "Search keyword");
        switch (field.toLowerCase()) {
            case "title":  return bookDAO.searchByTitle(keyword);
            case "author": return bookDAO.searchByAuthor(keyword);
            case "isbn":
                Book b = bookDAO.getById(keyword);
                return b != null ? List.of(b) : List.of();
            default:
                throw new IllegalArgumentException(
                    "Field must be 'title', 'author', or 'isbn'.");
        }
    }

    public List<Book> listBooks() {
        return bookDAO.getAll();
    }

    // ═══════════════════════════════════════════════════════════
    // MEMBER MANAGEMENT
    // ═══════════════════════════════════════════════════════════

    public Member registerMember(String memberId, String name,
                                  String email, boolean premium) {
        Validator.notEmpty(memberId, "Member ID");
        Validator.notEmpty(name,     "Name");
        Validator.validEmail(email);
        Member member = premium
            ? new PremiumMember(memberId.trim(), name.trim(), email.trim())
            : new Member      (memberId.trim(), name.trim(), email.trim());
        memberDAO.add(member);
        return member;
    }

    public Member updateMember(String memberId, String newName, String newEmail) {
        Member member = getMemberOrThrow(memberId);
        if (newName  != null && !newName.trim().isEmpty())
            member.setName(newName.trim());
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            Validator.validEmail(newEmail);
            member.setEmail(newEmail.trim());
        }
        memberDAO.update(member);
        return member;
    }

    public Member getMember(String memberId) {
        return getMemberOrThrow(memberId);
    }

    public List<Member> listMembers() {
        return memberDAO.getAll();
    }

    public List<Loan> getBorrowHistory(String memberId) {
        getMemberOrThrow(memberId);
        return loanDAO.getAllByMember(memberId);
    }

    // ═══════════════════════════════════════════════════════════
    // BORROWING OPERATIONS
    // ═══════════════════════════════════════════════════════════

    public Loan borrowBook(String memberId, String isbn, LocalDate dueDate) {
        Member member = getMemberOrThrow(memberId);
        Book   book   = getBookOrThrow(isbn);

        // Check borrow limit – polymorphism: PremiumMember returns 6
        List<Loan> active = loanDAO.getActiveByMember(memberId);
        if (active.size() >= member.getBorrowLimit()) {
            throw new IllegalStateException(
                "Borrow limit reached (" + member.getBorrowLimit()
                + " books). Return a book before borrowing again.");
        }

        if (!book.isAvailable()) {
            // Group 4 feature: show when the book will be back
            String info = getAvailabilityInfo(isbn);
            throw new IllegalStateException("Book not available. " + info);
        }

        // All checks passed – create the loan
        book.borrowCopy();
        bookDAO.update(book);

        String loanId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Loan loan = new Loan(loanId, memberId, isbn, LocalDate.now(), dueDate);
        loanDAO.add(loan);

        member.addToHistory("Borrowed '" + book.getTitle() + "' on " + LocalDate.now());
        memberDAO.update(member);

        return loan;
    }

    public Loan returnBook(String memberId, String isbn) {
        getMemberOrThrow(memberId);
        Book book = getBookOrThrow(isbn);

        Loan loan = loanDAO.getActiveLoanByIsbn(isbn);
        if (loan == null || !loan.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException(
                "No active loan found for this member and book.");
        }

        loan.close();
        loanDAO.update(loan);

        book.returnCopy();
        bookDAO.update(book);

        Member member = getMemberOrThrow(memberId);
        member.addToHistory("Returned '" + book.getTitle() + "' on " + LocalDate.now());
        memberDAO.update(member);

        return loan;
    }

    public List<Loan> getCurrentLoans(String memberId) {
        getMemberOrThrow(memberId);
        return loanDAO.getActiveByMember(memberId);
    }

    // ═══════════════════════════════════════════════════════════
    // GROUP 4 PREMIUM FEATURE – Book Availability Tracking
    // ═══════════════════════════════════════════════════════════

    public String getBookAvailability(String isbn) {
        Book book = getBookOrThrow(isbn);
        if (book.isAvailable()) {
            return "'" + book.getTitle() + "' is available now.";
        }
        return getAvailabilityInfo(isbn);
    }

    private String getAvailabilityInfo(String isbn) {
        // Find the loan with the earliest due date
        List<Loan> allLoans = loanDAO.getAll();
        Loan earliest = null;
        for (Loan l : allLoans) {
            if (l.getIsbn().equals(isbn) && l.isActive()) {
                if (earliest == null ||
                    l.getDueDate().isBefore(earliest.getDueDate())) {
                    earliest = l;
                }
            }
        }
        if (earliest == null) return "Book may be available soon.";
        long days = earliest.daysUntilDue();
        if (days < 0) {
            return "A copy is overdue (was due " + earliest.getDueDate()
                    + "). May be returned soon.";
        }
        return "Earliest copy due back on " + earliest.getDueDate()
                + " (" + days + " day(s) from today).";
    }

    // ═══════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════

    private Book getBookOrThrow(String isbn) {
        Book book = bookDAO.getById(isbn);
        if (book == null) {
            throw new IllegalArgumentException(
                "Book with ISBN '" + isbn + "' not found.");
        }
        return book;
    }

    private Member getMemberOrThrow(String memberId) {
        Member member = memberDAO.getById(memberId);
        if (member == null) {
            throw new IllegalArgumentException(
                "Member '" + memberId + "' not found.");
        }
        return member;
    }
}
