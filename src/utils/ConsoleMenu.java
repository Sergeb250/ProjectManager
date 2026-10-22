package utils;

import models.User;
import services.ProjectService;
import services.ReportService;
import services.TaskService;
import services.UserService;

import java.util.Scanner;

// Main menu only. Projects, tasks, users, and reports each have their own class.
public class ConsoleMenu extends MenuScreen {

    private final ProjectMenu projectMenu;
    private final TaskMenu taskMenu;
    private final UserMenu userMenu;
    private final ReportMenu reportMenu;

    public ConsoleMenu(ProjectService projectService, TaskService taskService,
                       ReportService reportService, UserService userService) {
        super(new Scanner(System.in), userService);
        taskMenu = new TaskMenu(scanner, projectService, taskService, userService);
        projectMenu = new ProjectMenu(scanner, projectService, taskService, userService, taskMenu);
        taskMenu.setProjectMenu(projectMenu);
        userMenu = new UserMenu(scanner, userService);
        reportMenu = new ReportMenu(scanner, projectService, reportService, userService);
    }

    public void start() {
        int choice;
        do {
            printBanner("JAVA PROJECT MANAGEMENT SYSTEM v3.0");
            System.out.println();
            User currentUser = userService.getCurrentUser();
            System.out.println("Current User: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            System.out.println();
            System.out.println("Main Menu:");
            System.out.println("-----------");
            System.out.println("1. Manage Projects");
            System.out.println("2. Manage Tasks");
            System.out.println("3. View Status Reports");
            System.out.println("4. Switch User");
            System.out.println("5. Simulate Concurrent Updates");
            System.out.println("6. Exit");
            System.out.println();
            choice = readChoice("Enter your choice: ", 1, 6);
            switch (choice) {
                case 1:
                    projectMenu.open();
                    break;
                case 2:
                    taskMenu.open();
                    break;
                case 3:
                    reportMenu.open();
                    break;
                case 4:
                    userMenu.open();
                    break;
                case 5:
                    taskMenu.simulateUpdates();
                    break;
                case 6:
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        } while (choice != 6);
    }
}
