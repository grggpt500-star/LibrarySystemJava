package dao;

import interfaces.IDao;
import models.Loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Loan.
 * In-memory store using a HashMap.
 */
public class LoanDAO implements IDao<Loan> {

    private Map<String, Loan> store = new HashMap<>();

    @Override
    public void add(Loan loan) {
        store.put(loan.getLoanId(), loan);
    }

    @Override
    public Loan getById(String loanId) {
        return store.get(loanId);
    }

    @Override
    public List<Loan> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void update(Loan loan) {
        store.put(loan.getLoanId(), loan);
    }

    @Override
    public boolean delete(String loanId) {
        return store.remove(loanId) != null;
    }

    // ── Helper queries ────────────────────────────────────────

    /** All currently active loans for a given member. */
    public List<Loan> getActiveByMember(String memberId) {
        List<Loan> result = new ArrayList<>();
        for (Loan l : store.values()) {
            if (l.getMemberId().equals(memberId) && l.isActive()) {
                result.add(l);
            }
        }
        return result;
    }

    /** All loans (active + returned) for a member – full history. */
    public List<Loan> getAllByMember(String memberId) {
        List<Loan> result = new ArrayList<>();
        for (Loan l : store.values()) {
            if (l.getMemberId().equals(memberId)) {
                result.add(l);
            }
        }
        return result;
    }

    /** The active loan for a specific book ISBN, or null. */
    public Loan getActiveLoanByIsbn(String isbn) {
        for (Loan l : store.values()) {
            if (l.getIsbn().equals(isbn) && l.isActive()) {
                return l;
            }
        }
        return null;
    }
}
