package utils;

import models.TaskStatus;
import utils.exceptions.InvalidInputException;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            throws InvalidInputException {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        try {
            int value = Integer.parseInt(text);
            if (value <= 0) {
                throw new InvalidInputException(message);
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Please enter a number.");
        }
    }

    public static double readBudget(Scanner scanner, String prompt) throws InvalidInputException {
        System.out.print(prompt);
        String text = scanner.nextLine().trim();
        try {
            double value = Double.parseDouble(text);
            if (value <= 0) {
                throw new InvalidInputException("Budget must be greater than 0.");
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
        if (RegexValidator.isEmail(email)) {
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
            throw new InvalidInputException("Invalid ID format. Use pattern " + prefix + "### (e.g., "
                    + prefix + "001).");
        }
        return normalized;
    }

    private static String normalizeId(String text, String prefix) {
        Pattern prefixed = Pattern.compile("(?i)" + Pattern.quote(prefix) + "\\d{3}");
        Matcher matcher = prefixed.matcher(text);
        String candidate = null;
        if (matcher.matches()) {
            candidate = prefix + text.substring(prefix.length()).toUpperCase(java.util.Locale.US);
        } else if (text.matches("\\d{1,3}")) {
            try {
                int number = Integer.parseInt(text);
                if (number > 0) {
                    candidate = String.format("%s%03d", prefix, number);
                }
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        if (candidate == null) {
            return null;
        }
        if (prefix.equals("PRJ") && RegexValidator.isProjectId(candidate)) {
            return candidate;
        }
        if (prefix.equals("TSK") && RegexValidator.isTaskId(candidate)) {
            return candidate;
        }
        return null;
    }
}
