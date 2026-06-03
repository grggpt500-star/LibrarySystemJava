# Library Management System
**Group 4 – Assessment C | Java**

## Requirements
> Tested with Java 21 (OpenJDK)
- Java 17 or higher
- No external libraries needed

---

## Compile & Run

### 1. Compile all source files
```bash
cd LibrarySystemJava
javac -d out -sourcepath src src/Main.java src/LibraryTest.java
```

### 2. Run the application
```bash
java -cp out Main
```

### 3. Run the tests
```bash
java -cp out LibraryTest
```

---

## Project Structure

```
LibrarySystemJava/
└── src/
    ├── Main.java                   # CLI entry point
    ├── LibraryTest.java            # All unit + integration tests
    ├── interfaces/
    │   └── IDao.java               # Generic DAO interface
    ├── models/
    │   ├── Book.java               # Book entity
    │   ├── Member.java             # Base member class
    │   ├── PremiumMember.java      # Inherits Member (Group 4 feature)
    │   └── Loan.java               # Loan transaction entity
    ├── dao/
    │   ├── BookDAO.java            # CRUD for books
    │   ├── MemberDAO.java          # CRUD for members
    │   └── LoanDAO.java            # CRUD for loans
    ├── services/
    │   └── LibraryService.java     # Business logic layer
    └── utils/
        └── Validator.java          # Input validation helpers
```

---

## Group 4 Premium Feature (Even Number)
- **Premium Membership**: premium members can borrow up to **6 books** at a time (standard = 3)
- **Book Availability Tracking**: when a book is unavailable, the system shows the **earliest due-back date**

---

## OOP Concepts Applied

| Concept          | Where Used                                                         |
|------------------|--------------------------------------------------------------------|
| Encapsulation    | Private fields + getters/setters in all model classes              |
| Inheritance      | `PremiumMember` extends `Member`                                   |
| Polymorphism     | `getBorrowLimit()` returns 3 (Member) or 6 (PremiumMember)        |
| Interface        | `IDao<T>` enforces CRUD contract on all DAO classes                |
| DAO Pattern      | Data access fully separated from business logic in LibraryService  |

> Last updated: May 2025