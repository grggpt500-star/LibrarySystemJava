package utils;

/**
 * Static helper methods for validating user input.
 * Used across the service layer for error handling.
 */
public class Validator {

    public static void notEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }

    public static void positiveInt(int value, String fieldName) {
        if (value < 1) {
            throw new IllegalArgumentException(
                fieldName + " must be a positive integer.");
        }
    }

    public static void validEmail(String email) {
        if (email == null || !email.matches("[^@]+@[^@]+\\.[^@]+")) {
            throw new IllegalArgumentException(
                "'" + email + "' is not a valid email address.");
        }
    }
}
