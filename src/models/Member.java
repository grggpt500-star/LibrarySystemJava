package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for library members.
 * Demonstrates encapsulation, and is extended by PremiumMember (inheritance).
 */
public class Member {

    public static final int BORROW_LIMIT = 3;

    private String memberId;
    private String name;
    private String email;
    private List<String> borrowHistory; // stores human-readable history strings

    public Member(String memberId, String name, String email) {
        this.memberId     = memberId;
        this.name         = name;
        this.email        = email;
        this.borrowHistory = new ArrayList<>();
    }

    // ── Getters ──────────────────────────────────────────────
    public String       getMemberId()      { return memberId; }
    public String       getName()          { return name; }
    public String       getEmail()         { return email; }
    public List<String> getBorrowHistory() { return new ArrayList<>(borrowHistory); }

    // ── Setters ──────────────────────────────────────────────
    public void setName(String name)   { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    public void addToHistory(String record) {
        borrowHistory.add(record);
    }

    /**
     * Polymorphism: PremiumMember overrides this to return 6.
     */
    public int getBorrowLimit() {
        return BORROW_LIMIT;
    }

    public String getMembershipType() {
        return "Standard";
    }

    @Override
    public String toString() {
        return "[" + memberId + "] " + name
                + " (" + getMembershipType() + ") – " + email;
    }
}
