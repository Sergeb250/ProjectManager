package utils;

import models.TaskStatus;

import java.util.Scanner;

public class ValidationUtils {

    private ValidationUtils() {
    }

    public static int readChoiceInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(text);
                if (choice < min || choice > max) {
                    System.out.println("Invalid option.");
                    continue;
                }
                return choice;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public static int readPositiveInt(Scanner scanner, String prompt) {
        return readPositiveInt(scanner, prompt, "Please enter a number greater than 0.");
    }

    public static int readPositiveInt(Scanner scanner, String prompt, String nonPositiveMessage) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(text);
                if (value <= 0) {
                    System.out.println(nonPositiveMessage);
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public static double readBudget(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(text);
                if (value < 0) {
                    System.out.println(" Error: Budget must be 0 or more.");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public static TaskStatus readStatus(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            TaskStatus status = TaskStatus.fromText(text);
            if (status == null) {
                System.out.println(" Error: Invalid status. Please choose from [Pending, In Progress, Completed].");
                continue;
            }
            return status;
        }
    }

    public static String readText(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            if (!text.isEmpty()) {
                return text;
            }
            System.out.println("Please enter a value.");
        }
    }

    public static String readEmail(Scanner scanner, String prompt) {
        while (true) {
            String email = readText(scanner, prompt);
            int at = email.indexOf('@');
            int dot = email.lastIndexOf('.');
            if (at > 0 && dot > at + 1 && dot < email.length() - 1) {
                return email;
            }
            System.out.println(" Error: Please enter a valid email address.");
        }
    }

    public static String readPrefixedId(Scanner scanner, String prompt, String prefix, boolean allowZero) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim();
            if (allowZero && text.equals("0")) {
                return null;
            }
            String normalized = normalizeId(text, prefix);
            if (normalized == null) {
                System.out.println(" Error: Invalid input. Please enter a valid numeric or prefixed ID (e.g., "
                        + prefix + "001).");
                continue;
            }
            return normalized;
        }
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
