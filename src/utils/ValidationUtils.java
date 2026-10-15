package utils;

import models.TaskStatus;
import utils.exceptions.InvalidInputException;
import utils.exceptions.InvalidProjectDataException;

import java.util.Scanner;

public class ValidationUtils {

    private ValidationUtils() {
    }

    public static int readChoiceInRange(Scanner scanner, String prompt, int min, int max)
            throws InvalidInputException {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(text);
            if (choice < min || choice > max) {
                throw new InvalidInputException("Invalid option.");
            }
            return choice;
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Please enter a number.");
        }
    }

    public static int readPositiveInt(Scanner scanner, String prompt, String message)
            throws InvalidInputException, InvalidProjectDataException {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        try {
            int value = Integer.parseInt(text);
            if (value <= 0) {
                throw new InvalidProjectDataException(message);
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Please enter a number.");
        }
    }

    public static double readBudget(Scanner scanner, String prompt)
            throws InvalidInputException, InvalidProjectDataException {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        try {
            double value = Double.parseDouble(text);
            if (value <= 0) {
                throw new InvalidProjectDataException("Budget must be greater than 0.");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Please enter a number.");
        }
    }

    public static TaskStatus readStatus(Scanner scanner, String prompt) throws InvalidInputException {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        TaskStatus status = TaskStatus.fromText(text);
        if (status == null) {
            throw new InvalidInputException("Invalid status. Please choose from [Pending, In Progress, Completed].");
        }
        return status;
    }

    public static String readText(Scanner scanner, String prompt) throws InvalidInputException {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        if (text.isEmpty()) {
            throw new InvalidInputException("Please enter a value.");
        }
        return text;
    }

    public static String readEmail(Scanner scanner, String prompt) throws InvalidInputException {
        String email = readText(scanner, prompt);
        int at = email.indexOf('@');
        int dot = email.lastIndexOf('.');
        if (at > 0 && dot > at + 1 && dot < email.length() - 1) {
            return email;
        }
        throw new InvalidInputException("Please enter a valid email address.");
    }

    public static String readPrefixedId(Scanner scanner, String prompt, String prefix, boolean allowZero)
            throws InvalidInputException {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        if (allowZero && text.equals("0")) {
            return null;
        }
        String normalized = normalizeId(text, prefix);
        if (normalized == null) {
            throw new InvalidInputException("Invalid input. Please enter a valid numeric or prefixed ID (e.g., "
                    + prefix + "001).");
        }
        return normalized;
    }

    private static String normalizeId(String text, String prefix) {
        if (text.matches("(?i)" + prefix + "\\d{3,}")) {
            return prefix + text.substring(prefix.length()).toUpperCase(java.util.Locale.US);
        }
        if (text.matches("\\d+")) {
            try {
                int number = Integer.parseInt(text);
                if (number <= 0) {
                    return null;
                }
                return String.format("%s%03d", prefix, number);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }
}
