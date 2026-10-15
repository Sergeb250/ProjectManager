package utils;

import models.AdminUser;
import models.HardwareProject;
import models.Project;
import models.RegularUser;
import models.SoftwareProject;
import models.Task;
import models.TaskStatus;
import models.User;
import services.ProjectService;
import services.ReportService;
import services.TaskService;
import services.UserService;
import utils.exceptions.EmptyProjectException;
import utils.exceptions.InvalidInputException;
import utils.exceptions.ProjectNotFoundException;
import utils.exceptions.TaskNotFoundException;

import java.util.Locale;
import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner scanner;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ReportService reportService;
    private final UserService userService;

    public ConsoleMenu(ProjectService projectService, TaskService taskService,
                       ReportService reportService, UserService userService) {
        scanner = new Scanner(System.in);
        this.projectService = projectService;
        this.taskService = taskService;
        this.reportService = reportService;
        this.userService = userService;
    }

    public void start() {
        int choice;
        do {
            printBanner("JAVA PROJECT MANAGEMENT SYSTEM");
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
            System.out.println("5. Exit");
            System.out.println();
            choice = readChoice("Enter your choice: ", 1, 5);
            switch (choice) {
                case 1:
                    manageProjects();
                    break;
                case 2:
                    manageTasks();
                    break;
                case 3:
                    viewReports();
                    break;
                case 4:
                    switchUser();
                    break;
                case 5:
                    System.out.println("Goodbye.");
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        } while (choice != 5);
    }

    private void manageProjects() {
        while (true) {
            printBanner("MANAGE PROJECTS");
            System.out.println();
            System.out.println("1. Add Project");
            System.out.println("2. Browse Project Catalog");
            System.out.println("3. <<Back");
            int choice = readChoice("Enter your choice: ", 1, 3);
            switch (choice) {
                case 1:
                    addProject();
                    break;
                case 2:
                    browseCatalog();
                    break;
                case 3:
                    return;
                default:
                    break;
            }
        }
    }

    private void browseCatalog() {
        while (true) {
            printBanner("PROJECT CATALOG");
            System.out.println();
            System.out.println("Filter Options:");
            System.out.println("1. View All Projects (" + projectService.getCount() + ")");
            System.out.println("2. Software Projects Only");
            System.out.println("3. Hardware Projects Only");
            System.out.println("4. Search by Budget Range");
            System.out.println("5. <<Back");
            int choice = readChoice("Enter filter choice: ", 1, 5);
            if (choice == 5) {
                return;
            }
            switch (choice) {
                case 1:
                    projectService.printAll();
                    break;
                case 2:
                    projectService.printByType("Software");
                    break;
                case 3:
                    projectService.printByType("Hardware");
                    break;
                case 4:
                    if (!searchByBudget()) {
                        continue;
                    }
                    break;
                default:
                    break;
            }

            while (true) {
                System.out.println();
                System.out.println("1. View Project Details");
                System.out.println("2. <<Back");
                int next = readChoice("Enter your choice: ", 1, 2);
                if (next == 2) {
                    break;
                }
                Project project = promptForProject("Enter project ID (or 0 to go back): ");
                if (project == null) {
                    continue;
                }
                showProjectDetails(project);
            }
        }
    }

    private boolean searchByBudget() {
        System.out.println();
        System.out.println("1. Enter Budget Range");
        System.out.println("2. <<Back");
        int choice = readChoice("Enter your choice: ", 1, 2);
        if (choice == 2) {
            return false;
        }
        while (true) {
            try {
                double min = readBudget("Enter minimum budget: ");
                double max = readBudget("Enter maximum budget: ");
                if (max < min) {
                    throw new InvalidInputException(
                            "Maximum budget must be greater than or equal to minimum budget.");
                }
                projectService.printByBudget(min, max);
                return true;
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            } finally {
            }
        }
    }

    private void showProjectDetails(Project project) {
        while (true) {
            printBanner("PROJECT DETAILS: " + project.getId());
            System.out.println();
            try {
                printProject(project);
            } catch (EmptyProjectException ex) {
                printError(ex);
            } finally {
            }
            System.out.println();
            System.out.println("Options:");
            System.out.println("1. Add New Task");
            System.out.println("2. Update Task Status");
            System.out.println("3. Remove Task");
            System.out.println("4. <<Back");
            int choice = readChoice("Enter your choice: ", 1, 4);
            switch (choice) {
                case 1:
                    addTaskTo(project);
                    break;
                case 2:
                    updateTaskOn(project);
                    break;
                case 3:
                    removeTaskOn(project);
                    break;
                case 4:
                    return;
                default:
                    break;
            }
        }
    }

    private void manageTasks() {
        while (true) {
            printBanner("MANAGE TASKS");
            System.out.println();
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Update Task Status");
            System.out.println("4. Remove Task");
            System.out.println("5. <<Back");
            int choice = readChoice("Enter your choice: ", 1, 5);
            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    viewTasks();
                    break;
                case 3:
                    updateTaskStatus();
                    break;
                case 4:
                    removeTask();
                    break;
                case 5:
                    return;
                default:
                    break;
            }
        }
    }

    private void viewReports() {
        printBanner("PROJECT STATUS REPORT");
        System.out.println();
        reportService.printReport(projectService);
        System.out.println();
        System.out.println("1. <<Back");
        readChoice("Enter your choice: ", 1, 1);
    }

    private void switchUser() {
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

    private void addProject() {
        if (projectService.isFull()) {
            System.out.println("The project list is full.");
            return;
        }

        System.out.println("1. Software Project");
        System.out.println("2. Hardware Project");
        System.out.println("3. <<Back");
        int typeChoice = readChoice("Enter project type: ", 1, 3);
        if (typeChoice == 3) {
            return;
        }

        String name = readText("Enter project name: ");
        String description = readText("Enter description: ");
        int duration = readPositiveInt("Enter duration (days): ", "Duration must be greater than 0.");
        double budget = readBudget("Enter budget: ");
        int teamSize = readPositiveInt("Enter team size: ", "Team size must be greater than 0.");
        String leadName = userService.getCurrentUser().getName();

        Project project;
        if (typeChoice == 1) {
            String technology = readText("Enter technology: ");
            project = new SoftwareProject(name, description, teamSize, duration, budget, leadName, technology);
        } else {
            String equipment = readText("Enter equipment: ");
            project = new HardwareProject(name, description, teamSize, duration, budget, leadName, equipment);
        }

        projectService.addProject(project);
        System.out.println("✓ Project successfully created.");
        System.out.println("ID: " + project.getId() + " | Type: " + project.getType());
    }

    private void addTask() {
        if (projectService.getCount() == 0) {
            System.out.println("No projects yet. Add a project first.");
            return;
        }
        printBanner("ADD NEW TASK");
        System.out.println();
        String name = readText("Enter task name: ");
        Project project = promptForProject("Enter project ID: ");
        if (project == null) {
            return;
        }
        addTaskTo(project, name, "");
    }

    private void addTaskTo(Project project) {
        printBanner("ADD NEW TASK");
        System.out.println();
        String name = readText("Enter task name: ");
        addTaskTo(project, name, "");
    }

    private void addTaskTo(Project project, String name, String assignedTo) {
        if (taskService.isTaskListFull(project)) {
            System.out.println("This project cannot take more tasks.");
            return;
        }
        if (taskService.hasTaskName(project, name)) {
            System.out.println("Error: A task with that name already exists on this project.");
            return;
        }
        TaskStatus status = readStatus("Enter initial status (Pending/In Progress/Completed): ");
        int hours = readPositiveInt("Enter hours: ", "Hours must be greater than 0.");
        Task task = taskService.addTask(project, name, assignedTo, status, hours);
        if (task == null) {
            System.out.println(" Error: A task with that name already exists on this project.");
            return;
        }
        System.out.println("✓ Task \"" + task.getName() + "\" added successfully to Project " + project.getId() + "!");
        System.out.println("ID: " + task.getId());
    }

    private void viewTasks() {
        Project project = promptForProject("Enter project ID (or 0 to return): ");
        if (project == null) {
            return;
        }
        System.out.println("Tasks for " + project.getId() + " " + project.getName() + ":");
        printTasks(project);
    }

    private void updateTaskStatus() {
        if (!requireAdmin("update a task")) {
            return;
        }
        changeStatus(promptForTask());
    }

    private void updateTaskOn(Project project) {
        if (!requireAdmin("update a task")) {
            return;
        }
        changeStatus(promptForTaskOn(project));
    }

    private void changeStatus(Task task) {
        if (task == null) {
            return;
        }
        TaskStatus status = readStatus("Enter new status: ");
        taskService.updateStatus(task, status);
        System.out.println("✓ Task \"" + task.getName() + "\" marked as " + status.getLabel() + ".");
    }

    private void removeTask() {
        if (!requireAdmin("remove a task")) {
            return;
        }
        try {
            String taskId = readId("Enter task ID (or 0 to return): ", "TSK", true);
            if (taskId == null) {
                return;
            }
            Task removed = taskService.remove(taskId);
            if (removed == null) {
                throw new TaskNotFoundException("Task '" + taskId + "' was not found in the project.");
            }
            System.out.println("✓ Task \"" + removed.getName() + "\" removed.");
        } catch (TaskNotFoundException ex) {
            printError(ex);
            System.out.println("Operation aborted. Returning to task menu...");
        } finally {
        }
    }

    private void removeTaskOn(Project project) {
        if (!requireAdmin("remove a task")) {
            return;
        }
        try {
            String taskId = readId("Enter task ID (or 0 to return): ", "TSK", true);
            if (taskId == null) {
                return;
            }
            Task removed = taskService.remove(project, taskId);
            if (removed == null) {
                throw new TaskNotFoundException("Task '" + taskId + "' was not found in the project.");
            }
            System.out.println("✓ Task \"" + removed.getName() + "\" removed.");
        } catch (TaskNotFoundException ex) {
            printError(ex);
            System.out.println("Operation aborted. Returning to task menu...");
        } finally {
        }
    }

    private Project promptForProject(String prompt) {
        while (true) {
            try {
                String projectId = readId(prompt, "PRJ", true);
                if (projectId == null) {
                    return null;
                }
                Project project = projectService.findById(projectId);
                if (project == null) {
                    throw new ProjectNotFoundException("Project ID '" + projectId + "' does not exist.");
                }
                return project;
            } catch (ProjectNotFoundException ex) {
                printError(ex);
                System.out.println("Please try again.");
            } finally {
            }
        }
    }

    private Task promptForTask() {
        try {
            String taskId = readId("Enter task ID: ", "TSK", true);
            if (taskId == null) {
                return null;
            }
            Task task = taskService.findById(taskId);
            if (task == null) {
                throw new TaskNotFoundException("Task '" + taskId + "' was not found in the project.");
            }
            return task;
        } catch (TaskNotFoundException ex) {
            printError(ex);
            System.out.println("Operation aborted. Returning to task menu...");
            return null;
        } finally {
        }
    }

    private Task promptForTaskOn(Project project) {
        try {
            String taskId = readId("Enter task ID: ", "TSK", true);
            if (taskId == null) {
                return null;
            }
            Task task = taskService.findTaskById(project, taskId);
            if (task == null) {
                throw new TaskNotFoundException("Task '" + taskId + "' was not found in the project.");
            }
            return task;
        } catch (TaskNotFoundException ex) {
            printError(ex);
            System.out.println("Operation aborted. Returning to task menu...");
            return null;
        } finally {
        }
    }

    private void printProject(Project project) throws EmptyProjectException {
        System.out.println("Project Name: " + project.getName());
        System.out.println("Type: " + project.getType());
        System.out.println("Team Size: " + project.getTeamSize());
        System.out.printf(Locale.US, "Budget: $%,.2f%n", project.getBudget());
        System.out.println("Description: " + project.getDescription());
        System.out.println(project.getProjectDetails());
        System.out.println();
        System.out.println("Associated Tasks:");
        printTasks(project);
        if (project.getTaskCount() == 0) {
            throw new EmptyProjectException("Project '" + project.getId() + "' has no tasks.");
        }
        System.out.printf(Locale.US, "Completion Rate: %.2f%%%n", taskService.calculateCompletionPercentage(project));
    }

    private void printTasks(Project project) {
        if (project.getTaskCount() == 0) {
            System.out.println("No tasks found.");
            return;
        }
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("%-8s | %-20s | %s%n", "ID", "TASK NAME", "STATUS");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        for (int i = 0; i < project.getTaskCount(); i++) {
            Task task = taskService.getTaskAt(project, i);
            System.out.printf("%-8s | %-20s | %s%n",
                    task.getId(), task.getName(), task.getStatus().getLabel());
        }
        System.out.println("───────────────────────────────────────────────────────────────────────");
    }

    private boolean requireAdmin(String action) {
        if (userService.getCurrentUser().canUpdateAndDelete()) {
            return true;
        }
        System.out.println("Only an admin can " + action + ".");
        return false;
    }

    private int readChoice(String prompt, int min, int max) {
        while (true) {
            try {
                return ValidationUtils.readChoiceInRange(scanner, prompt, min, max);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            } finally {
            }
        }
    }

    private String readText(String prompt) {
        while (true) {
            try {
                return ValidationUtils.readText(scanner, prompt);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            } finally {
            }
        }
    }

    private String readEmail(String prompt) {
        while (true) {
            try {
                return ValidationUtils.readEmail(scanner, prompt);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            } finally {
            }
        }
    }

    private double readBudget(String prompt) {
        while (true) {
            try {
                return ValidationUtils.readBudget(scanner, prompt);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            } finally {
            }
        }
    }

    private int readPositiveInt(String prompt, String message) {
        while (true) {
            try {
                return ValidationUtils.readPositiveInt(scanner, prompt, message);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            } finally {
            }
        }
    }

    private TaskStatus readStatus(String prompt) {
        while (true) {
            try {
                return ValidationUtils.readStatus(scanner, prompt);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            } finally {
            }
        }
    }

    private String readId(String prompt, String prefix, boolean allowZero) {
        while (true) {
            try {
                return ValidationUtils.readPrefixedId(scanner, prompt, prefix, allowZero);
            } catch (InvalidInputException ex) {
                printError(ex);
                System.out.println("Please try again.");
            } finally {
            }
        }
    }

    private void printError(Exception ex) {
        System.out.println("❌ Error: " + ex.getClass().getSimpleName() + " – " + ex.getMessage());
    }

    private void printBanner(String title) {
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
