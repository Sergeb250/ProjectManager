package utils;

import models.AdminUser;
import models.RegularUser;
import models.User;
import services.UserService;

import java.util.Scanner;

// User screens: switch the logged-in user or create a new one.
class UserMenu extends MenuScreen {

    UserMenu(Scanner scanner, UserService userService) {
        super(scanner, userService);
    }

    void open() {
        printBanner("SWITCH USER");
        System.out.println();
        for (int i = 0; i < userService.getCount(); i++) {
            User user = userService.getAt(i);
            String canUpdate = user.canUpdateAndDelete() ? "Yes" : "No";
            System.out.println((i + 1) + ". " + user.getId() + " | " + user.getName() + " | " + user.getRole()
                    + " | Can update and delete: " + canUpdate);
        }
        System.out.println((userService.getCount() + 1) + ". Create User");
        System.out.println((userService.getCount() + 2) + ". <<Back");
        int choice = readChoice("Enter your choice: ", 1, userService.getCount() + 2);
        if (choice == userService.getCount() + 2) {
            return;
        }
        if (choice == userService.getCount() + 1) {
            createUser();
            return;
        }
        User selected = userService.getAt(choice - 1);
        userService.setCurrentUser(selected);
        System.out.println("Current User: " + selected.getName() + " (" + selected.getRole() + ")");
    }

    private void createUser() {
        if (!requireAdmin("create a user")) {
            return;
        }
        if (userService.isFull()) {
            System.out.println("The user list is full.");
            return;
        }
        String name = readText("Enter user name: ");
        String email = readEmail("Enter email: ");
        if (userService.emailExists(email)) {
            System.out.println(" Error: That email is already used.");
            return;
        }
        System.out.println("1. Regular User");
        System.out.println("2. Admin User");
        System.out.println("3. <<Back");
        int role = readChoice("Enter role: ", 1, 3);
        if (role == 3) {
            return;
        }
        User user;
        if (role == 2) {
            user = new AdminUser(name, email);
        } else {
            user = new RegularUser(name, email);
        }
        userService.addUser(user);
        String canUpdate = user.canUpdateAndDelete() ? "Yes" : "No";
        System.out.println(user.getId() + " | " + user.getName() + " | " + user.getRole()
                + " | Can update and delete: " + canUpdate);
        System.out.println("✓ User created. ID: " + user.getId());
    }
}
