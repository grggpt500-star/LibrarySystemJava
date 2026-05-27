package dao;

import interfaces.IDao;
import models.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Book.
 * Stores data in-memory using a HashMap.
 * Implements IDao – demonstrating the interface contract.
 */
public class BookDAO implements IDao<Book> {

    // In-memory store: isbn -> Book
    private Map<String, Book> store = new HashMap<>();

    @Override
    public void add(Book book) {
        if (store.containsKey(book.getIsbn())) {
            throw new IllegalArgumentException(
                "Book with ISBN '" + book.getIsbn() + "' already exists.");
        }
        store.put(book.getIsbn(), book);
    }

    @Override
    public Book getById(String isbn) {
        return store.get(isbn);   // returns null if not found
    }

    @Override
    public List<Book> getAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void update(Book book) {
        if (!store.containsKey(book.getIsbn())) {
            throw new IllegalArgumentException(
                "Book '" + book.getIsbn() + "' not found.");
        }
        store.put(book.getIsbn(), book);
    }

    @Override
    public boolean delete(String isbn) {
        return store.remove(isbn) != null;
    }

    // ── Extra search methods ──────────────────────────────────

    public List<Book> searchByTitle(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : store.values()) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    public List<Book> searchByAuthor(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : store.values()) {
            if (b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }
}
