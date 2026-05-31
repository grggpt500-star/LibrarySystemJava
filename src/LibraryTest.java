import models.*;
import dao.*;
import services.LibraryService;
import utils.Validator;

import java.time.LocalDate;
import java.util.List;

/**
 * Unit + Integration tests for the Library Management System.
 * Runs without any external testing framework (plain Java assertions).
 * Usage: compile and run this file, all results print to console.
 */
public class LibraryTest {

    static int passed = 0;
    static int failed = 0;

    public static void main(String[] args) {
        System.out.println("===== Running Library Management System Tests =====\n");

        // Unit Tests – Models
        testBook();
        testMember();
        testLoan();

        // Unit Tests – DAOs
        testBookDAO();
        testMemberDAO();
        testLoanDAO();

        // Unit Tests – Validator
        testValidator();

        // Integration Tests – LibraryService
        testLibraryService();

        // Summary
        System.out.println("\n===== Results =====");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total:  " + (passed + failed));
    }

    // ═══════════════════════════════════════════════════════════
    // MODEL TESTS
    // ═══════════════════════════════════════════════════════════

    static void testBook() {
        System.out.println("-- TestBook --");
        Book book = new Book("978-001", "Python 101", "Jane Doe", 2);

        assertTrue("initial availability", book.isAvailable());
        assertEquals("initial available copies", 2, book.getAvailableCopies());

        book.borrowCopy();
        assertEquals("after borrow", 1, book.getAvailableCopies());

        book.borrowCopy();
        assertFalse("no copies left", book.isAvailable());

        assertFalse("over-borrow returns false", book.borrowCopy());

        book.returnCopy();
        assertEquals("after return", 1, book.getAvailableCopies());

        assertTrue("str contains title", book.toString().contains("Python 101"));
    }

    static void testMember() {
        System.out.println("-- TestMember --");
        Member std = new Member("M01", "Alice", "alice@test.com");
        assertEquals("standard limit", 3, std.getBorrowLimit());
        assertEquals("standard type", "Standard", std.getMembershipType());

        PremiumMember pm = new PremiumMember("M02", "Bob", "bob@test.com");
        assertEquals("premium limit", 6, pm.getBorrowLimit());      // polymorphism
        assertEquals("premium type", "Premium", pm.getMembershipType());

        std.addToHistory("Borrowed something");
        assertEquals("history size", 1, std.getBorrowHistory().size());
    }

    static void testLoan() {
        System.out.println("-- TestLoan --");
        Loan loan = new Loan("LN01", "M01", "978-001", null, null);

        assertTrue("active on creation", loan.isActive());
        assertEquals("default due 14 days",
            LocalDate.now().plusDays(14), loan.getDueDate());

        loan.close();
        assertFalse("closed loan inactive", loan.isActive());
    }

    // ═══════════════════════════════════════════════════════════
    // DAO TESTS
    // ═══════════════════════════════════════════════════════════

    static void testBookDAO() {
        System.out.println("-- TestBookDAO --");
        BookDAO dao = new BookDAO();
        Book b = new Book("978-001", "Java 101", "Smith", 1);
        dao.add(b);

        assertNotNull("get by id", dao.getById("978-001"));

        try {
            dao.add(new Book("978-001", "Dup", "Author", 1));
            fail("duplicate isbn should throw");
        } catch (IllegalArgumentException e) {
            pass("duplicate isbn throws");
        }

        dao.delete("978-001");
        assertNull("deleted book null", dao.getById("978-001"));

        dao.add(new Book("978-002", "Python Guide", "Doe", 1));
        assertEquals("search by title", 1, dao.searchByTitle("python").size());
        assertEquals("search by author", 1, dao.searchByAuthor("doe").size());
        assertEquals("no match returns empty", 0, dao.searchByTitle("zzz").size());
    }

    static void testMemberDAO() {
        System.out.println("-- TestMemberDAO --");
        MemberDAO dao = new MemberDAO();
        Member m = new Member("M01", "Alice", "alice@test.com");
        dao.add(m);

        assertEquals("get name", "Alice", dao.getById("M01").getName());

        try {
            dao.add(new Member("M01", "Dup", "d@d.com"));
            fail("duplicate id should throw");
        } catch (IllegalArgumentException e) {
            pass("duplicate member throws");
        }

        dao.delete("M01");
        assertNull("deleted member null", dao.getById("M01"));
    }

    static void testLoanDAO() {
        System.out.println("-- TestLoanDAO --");
        LoanDAO dao = new LoanDAO();
        Loan loan = new Loan("LN01", "M01", "978-001", null, null);
        dao.add(loan);

        assertEquals("active by member", 1, dao.getActiveByMember("M01").size());
        assertNotNull("active by isbn", dao.getActiveLoanByIsbn("978-001"));

        loan.close();
        dao.update(loan);
        assertNull("null after close", dao.getActiveLoanByIsbn("978-001"));
        assertEquals("history still has 1", 1, dao.getAllByMember("M01").size());
    }

    // ═══════════════════════════════════════════════════════════
    // VALIDATOR TESTS
    // ═══════════════════════════════════════════════════════════

    static void testValidator() {
        System.out.println("-- TestValidator --");
        try {
            Validator.notEmpty("", "Field");
            fail("empty string should throw");
        } catch (IllegalArgumentException e) { pass("empty throws"); }

        try {
            Validator.notEmpty("   ", "Field");
            fail("whitespace should throw");
        } catch (IllegalArgumentException e) { pass("whitespace throws"); }

        try {
            Validator.validEmail("bademail");
            fail("bad email should throw");
        } catch (IllegalArgumentException e) { pass("bad email throws"); }

        try {
            Validator.validEmail("good@test.com");
            pass("valid email passes");
        } catch (Exception e) { fail("valid email threw: " + e.getMessage()); }

        try {
            Validator.positiveInt(0, "Copies");
            fail("zero should throw");
        } catch (IllegalArgumentException e) { pass("zero throws"); }
    }

    // ═══════════════════════════════════════════════════════════
    // INTEGRATION TESTS – LibraryService
    // ═══════════════════════════════════════════════════════════

    static void testLibraryService() {
        System.out.println("-- TestLibraryService (Integration) --");
        LibraryService svc = new LibraryService();

        // Seed data
        svc.addBook("978-001", "Python 101", "Jane Doe", 1);
        svc.addBook("978-002", "Data Science", "John Smith", 2);
        svc.registerMember("M01", "Alice", "alice@test.com", false);
        svc.registerMember("M02", "Bob", "bob@test.com", false);
        svc.registerMember("M03", "Charlie", "charlie@test.com", true);

        // Book management
        try {
            svc.addBook("978-001", "Dup", "Author", 1);
            fail("duplicate book should throw");
        } catch (Exception e) { pass("duplicate book throws"); }

        assertEquals("search by title", 1, svc.searchBooks("python","title").size());

        svc.removeBook("978-002");
        assertEquals("removed book gone", 0, svc.searchBooks("978-002","isbn").size());
        // Re-add for later tests
        svc.addBook("978-002", "Data Science", "John Smith", 2);

        // Member management
        try {
            svc.registerMember("M01", "Dup", "d@d.com", false);
            fail("duplicate member should throw");
        } catch (Exception e) { pass("duplicate member throws"); }

        Member updated = svc.updateMember("M01", null, "new@test.com");
        assertEquals("email updated", "new@test.com", updated.getEmail());

        try {
            svc.registerMember("M99", "Bad", "bademail", false);
            fail("bad email should throw");
        } catch (Exception e) { pass("invalid email throws"); }

        // Borrow operations
        Loan loan = svc.borrowBook("M01", "978-001", null);
        assertNotNull("loan created", loan);
        assertTrue("loan active", loan.isActive());

        // Book should now be unavailable
        List<Book> books = svc.searchBooks("978-001", "isbn");
        assertFalse("book unavailable", books.get(0).isAvailable());

        // Return
        svc.returnBook("M01", "978-001");
        books = svc.searchBooks("978-001", "isbn");
        assertTrue("book available after return", books.get(0).isAvailable());

        // Standard member borrow limit = 3
        svc.addBook("978-003", "Book C", "Author C", 1);
        svc.addBook("978-004", "Book D", "Author D", 1);
        svc.borrowBook("M02", "978-001", null);
        svc.borrowBook("M02", "978-002", null);
        svc.borrowBook("M02", "978-003", null);
        try {
            svc.borrowBook("M02", "978-004", null);
            fail("4th borrow should fail for standard member");
        } catch (Exception e) { pass("standard limit enforced"); }

        // Premium member can borrow more than 3
        assertEquals("premium limit is 6", 6, svc.getMember("M03").getBorrowLimit());
        svc.addBook("978-005", "Book E", "Author E", 1);
        svc.addBook("978-006", "Book F", "Author F", 1);
        svc.addBook("978-007", "Book G", "Author G", 1);
        svc.borrowBook("M03", "978-004", null);
        svc.borrowBook("M03", "978-005", null);
        svc.borrowBook("M03", "978-006", null);
        Loan p4 = svc.borrowBook("M03", "978-007", null); // 4th book – should succeed
        assertNotNull("premium can borrow 4th book", p4);

        // Availability tracking (Group 4 feature) – use a fresh book with 1 copy
        svc.addBook("978-010", "Avail Test Book", "Test Author", 1);
        String avail = svc.getBookAvailability("978-010");
        assertTrue("available now message", avail.contains("available now"));

        // Borrow it so it becomes unavailable
        svc.registerMember("M04", "Diana", "diana@test.com", false);
        svc.borrowBook("M04", "978-010", null);
        String notAvail = svc.getBookAvailability("978-010");
        assertTrue("due back message", notAvail.contains("due back"));

        // Borrow history recorded
        List<Loan> history = svc.getBorrowHistory("M01");
        assertTrue("history not empty", history.size() >= 1);

        // Errors
        try {
            svc.borrowBook("M01", "FAKE-ISBN", null);
            fail("nonexistent book should throw");
        } catch (Exception e) { pass("nonexistent book throws"); }

        try {
            svc.borrowBook("FAKE-ID", "978-001", null);
            fail("nonexistent member should throw");
        } catch (Exception e) { pass("nonexistent member throws"); }
    }

    // ═══════════════════════════════════════════════════════════
    // TEST HELPERS
    // ═══════════════════════════════════════════════════════════

    static void assertTrue(String name, boolean condition) {
        if (condition) pass(name); else fail(name + " (expected true, got false)");
    }

    static void assertFalse(String name, boolean condition) {
        if (!condition) pass(name); else fail(name + " (expected false, got true)");
    }

    static void assertEquals(String name, Object expected, Object actual) {
        if (expected.equals(actual)) pass(name);
        else fail(name + " (expected=" + expected + ", actual=" + actual + ")");
    }

    static void assertNotNull(String name, Object obj) {
        if (obj != null) pass(name); else fail(name + " (expected non-null)");
    }

    static void assertNull(String name, Object obj) {
        if (obj == null) pass(name); else fail(name + " (expected null)");
    }

    static void pass(String name) {
        System.out.println("  PASS: " + name);
        passed++;
    }

    static void fail(String name) {
        System.out.println("  FAIL: " + name);
        failed++;
    }
}
