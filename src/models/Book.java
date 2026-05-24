package models;

/**
 * Represents a Book entity in the library.
 * Demonstrates encapsulation via private fields and public getters/setters.
 */
public class Book {

    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, int copies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = copies;
        this.availableCopies = copies;
    }

    // ── Getters ──────────────────────────────────────────────
    public String getIsbn()            { return isbn; }
    public String getTitle()           { return title; }
    public String getAuthor()          { return author; }
    public int    getTotalCopies()     { return totalCopies; }
    public int    getAvailableCopies() { return availableCopies; }

    // ── Setters ──────────────────────────────────────────────
    public void setTitle(String title)   { this.title = title; }
    public void setAuthor(String author) { this.author = author; }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public boolean borrowCopy() {
        if (availableCopies > 0) {
            availableCopies--;
            return true;
        }
        return false;
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    @Override
    public String toString() {
        return "[" + isbn + "] '" + title + "' by " + author
                + " (" + availableCopies + "/" + totalCopies + " available)";
    }
}
