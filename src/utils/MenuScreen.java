package utils;

import models.TaskStatus;
import models.User;
import services.UserService;
import utils.exceptions.InvalidInputException;

import java.util.Scanner;

// Shared console input. Each menu screen uses this instead of copying the same prompts.
class MenuScreen {

    protected final Scanner scanner;
    protected final UserService userService;

    MenuScreen(Scanner scanner, UserService userService) {
        this.scanner = scanner;
        this.userService = userService;
    }

    protected int readChoice(String prompt, int min, int max) {
        while (true) {
            try {
                return ValidationUtils.readChoiceInRange(scanner, prompt, min, max);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            }
        }
    }

    protected String readText(String prompt) {
        while (true) {
            try {
                return ValidationUtils.readText(scanner, prompt);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            }
        }
    }

    protected String readEmail(String prompt) {
        while (true) {
            try {
                return ValidationUtils.readEmail(scanner, prompt);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            }
        }
    }

    protected double readBudget(String prompt) {
        while (true) {
            try {
                return ValidationUtils.readBudget(scanner, prompt);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            }
        }
    }

    protected int readPositiveInt(String prompt, String message) {
        while (true) {
            try {
                return ValidationUtils.readPositiveInt(scanner, prompt, message);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            }
        }
    }

    protected TaskStatus readStatus(String prompt) {
        while (true) {
            try {
                return ValidationUtils.readStatus(scanner, prompt);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            }
        }
    }

    protected String readId(String prompt, String prefix, boolean allowZero) {
        while (true) {
            try {
                return ValidationUtils.readPrefixedId(scanner, prompt, prefix, allowZero);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            }
        }
    }

    protected boolean requireAdmin(String action) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.canUpdateAndDelete()) {
            return true;
        }
        System.out.println("Only an admin can " + action + ".");
        return false;
    }

    protected void printError(Exception ex) {
        System.out.println(" Error: " + ex.getClass().getSimpleName() + " – " + ex.getMessage());
    }

    protected void printBanner(String title) {
        int width = 44;
        System.out.println("╔" + "═".repeat(width) + "╗");
        System.out.println("║" + center(title, width) + "║");
        System.out.println("╚" + "═".repeat(width) + "╝");
    }

    private String center(String title, int width) {
        if (title.length() >= width) {
            return title.substring(0, width);
        }
        int left = (width - title.length()) / 2;
        int right = width - title.length() - left;
        return " ".repeat(left) + title + " ".repeat(right);
    }
}
