// Entry point for Group 4 Library Management System
// Authors: Krishna, Sunil
// Last updated: May 2025
import services.LibraryService;
import models.Loan;
import models.Member;
import models.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point – simple text-menu CLI for the Library Management System.
 */
public class Main {

    private static LibraryService service = new LibraryService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the Library Management System");
        boolean running = true;

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": bookMenu();         break;
                case "2": memberMenu();       break;
                case "3": borrowMenu();       break;
                case "4": availabilityMenu(); break;
                case "0": running = false;    break;
                default: System.out.println("Invalid option. Try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    // ── Menus ─────────────────────────────────────────────────

    static void printMainMenu() {
        System.out.println("\n===== Library Management System =====");
        System.out.println("1. Book Management");
        System.out.println("2. Member Management");
        System.out.println("3. Borrowing Operations");
        System.out.println("4. Book Availability (Premium Feature)");
        System.out.println("0. Exit");
        System.out.print("Select option: ");
    }

    static void bookMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Book Management ---");
            System.out.println("1. Add book");
            System.out.println("2. Update book");
            System.out.println("3. Remove book");
            System.out.println("4. Search books");
            System.out.println("5. List all books");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();

            try {
                switch (c) {
                    case "1":
                        System.out.print("ISBN: ");   String isbn   = scanner.nextLine().trim();
                        System.out.print("Title: ");  String title  = scanner.nextLine().trim();
                        System.out.print("Author: "); String author = scanner.nextLine().trim();
                        System.out.print("Copies [1]: ");
                        String copiesStr = scanner.nextLine().trim();
                        int copies = copiesStr.isEmpty() ? 1 : Integer.parseInt(copiesStr);
                        Book book = service.addBook(isbn, title, author, copies);
                        System.out.println("✓ Added: " + book);
                        break;

                    case "2":
                        System.out.print("ISBN: ");       String uIsbn   = scanner.nextLine().trim();
                        System.out.print("New title (blank to keep): ");
                        String uTitle  = scanner.nextLine().trim();
                        System.out.print("New author (blank to keep): ");
                        String uAuthor = scanner.nextLine().trim();
                        Book updated = service.updateBook(uIsbn,
                            uTitle.isEmpty()  ? null : uTitle,
                            uAuthor.isEmpty() ? null : uAuthor);
                        System.out.println("✓ Updated: " + updated);
                        break;

                    case "3":
                        System.out.print("ISBN to remove: ");
                        service.removeBook(scanner.nextLine().trim());
                        System.out.println("✓ Book removed.");
                        break;

                    case "4":
                        System.out.print("Keyword: "); String kw = scanner.nextLine().trim();
                        System.out.print("Field (title/author/isbn) [title]: ");
                        String field = scanner.nextLine().trim();
                        if (field.isEmpty()) field = "title";
                        List<Book> results = service.searchBooks(kw, field);
                        if (results.isEmpty()) System.out.println("No books found.");
                        else results.forEach(b -> System.out.println("  " + b));
                        break;

                    case "5":
                        List<Book> all = service.listBooks();
                        if (all.isEmpty()) System.out.println("No books in system.");
                        else all.forEach(b -> System.out.println("  " + b));
                        break;

                    case "0": back = true; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        }
    }

    static void memberMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Member Management ---");
            System.out.println("1. Register member");
            System.out.println("2. Update member");
            System.out.println("3. View borrowing history");
            System.out.println("4. List all members");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();

            try {
                switch (c) {
                    case "1":
                        System.out.print("Member ID: "); String mid   = scanner.nextLine().trim();
                        System.out.print("Name: ");      String name  = scanner.nextLine().trim();
                        System.out.print("Email: ");     String email = scanner.nextLine().trim();
                        System.out.print("Premium? (y/n) [n]: ");
                        boolean premium = scanner.nextLine().trim().equalsIgnoreCase("y");
                        Member m = service.registerMember(mid, name, email, premium);
                        System.out.println("✓ Registered: " + m);
                        break;

                    case "2":
                        System.out.print("Member ID: "); String umid = scanner.nextLine().trim();
                        System.out.print("New name (blank to keep): ");
                        String uName  = scanner.nextLine().trim();
                        System.out.print("New email (blank to keep): ");
                        String uEmail = scanner.nextLine().trim();
                        Member um = service.updateMember(umid,
                            uName.isEmpty()  ? null : uName,
                            uEmail.isEmpty() ? null : uEmail);
                        System.out.println("✓ Updated: " + um);
                        break;

                    case "3":
                        System.out.print("Member ID: "); String hmid = scanner.nextLine().trim();
                        List<Loan> history = service.getBorrowHistory(hmid);
                        Member hm = service.getMember(hmid);
                        System.out.println("History for " + hm.getName() + ":");
                        if (history.isEmpty()) System.out.println("  No history.");
                        else history.forEach(l -> System.out.println("  " + l));
                        break;

                    case "4":
                        List<Member> members = service.listMembers();
                        if (members.isEmpty()) System.out.println("No members registered.");
                        else members.forEach(mem -> System.out.println("  " + mem));
                        break;

                    case "0": back = true; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        }
    }

    static void borrowMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Borrowing Operations ---");
            System.out.println("1. Borrow book");
            System.out.println("2. Return book");
            System.out.println("3. View my current loans");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            String c = scanner.nextLine().trim();

            try {
                switch (c) {
                    case "1":
                        System.out.print("Member ID: "); String bmid = scanner.nextLine().trim();
                        System.out.print("Book ISBN: "); String bisbn = scanner.nextLine().trim();
                        System.out.print("Loan days [14]: ");
                        String daysStr = scanner.nextLine().trim();
                        int days = daysStr.isEmpty() ? 14 : Integer.parseInt(daysStr);
                        LocalDate due = LocalDate.now().plusDays(days);
                        Loan loan = service.borrowBook(bmid, bisbn, due);
                        System.out.println("✓ Borrowed! Loan ID: " + loan.getLoanId()
                            + "  Due: " + loan.getDueDate());
                        break;

                    case "2":
                        System.out.print("Member ID: "); String rmid  = scanner.nextLine().trim();
                        System.out.print("Book ISBN: "); String risbn = scanner.nextLine().trim();
                        Loan returned = service.returnBook(rmid, risbn);
                        System.out.println("✓ Returned. Loan " + returned.getLoanId() + " closed.");
                        break;

                    case "3":
                        System.out.print("Member ID: "); String cmid = scanner.nextLine().trim();
                        List<Loan> loans = service.getCurrentLoans(cmid);
                        Member cm = service.getMember(cmid);
                        System.out.println("Active loans for " + cm.getName()
                            + " (limit: " + cm.getBorrowLimit() + "):");
                        if (loans.isEmpty()) System.out.println("  No active loans.");
                        else loans.forEach(l -> System.out.println("  " + l));
                        break;

                    case "0": back = true; break;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        }
    }

    static void availabilityMenu() {
        System.out.println("\n--- Book Availability Tracker (Premium Feature) ---");
        System.out.print("Enter ISBN to check: ");
        String isbn = scanner.nextLine().trim();
        try {
            System.out.println("✓ " + service.getBookAvailability(isbn));
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
}
