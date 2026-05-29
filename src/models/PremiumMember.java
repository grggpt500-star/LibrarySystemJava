package models;

/**
 * Group 4 (even number) – Premium Membership feature.
 * Inherits from Member and overrides getBorrowLimit() to allow 6 books.
 * Demonstrates inheritance and polymorphism.
 */
public class PremiumMember extends Member {

    public static final int PREMIUM_LIMIT = 6;

    public PremiumMember(String memberId, String name, String email) {
        super(memberId, name, email);
    }

    /**
     * Polymorphism: overrides Member's limit of 3 → returns 6.
     */
    @Override
    public int getBorrowLimit() {
        return PREMIUM_LIMIT;
    }

    @Override
    public String getMembershipType() {
        return "Premium";
    }
}
