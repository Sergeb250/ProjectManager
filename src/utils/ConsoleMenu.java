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

import java.util.Scanner;

public class ConsoleMenu {

    private static final int MAX_USERS = 10;
    private static final int BANNER_WIDTH = 44;

    private final Scanner scanner;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ReportService reportService;

    private final User[] users;
    private int userCount;
    private User currentUser;

    public ConsoleMenu() {
        scanner = new Scanner(System.in);
        projectService = new ProjectService();
        taskService = new TaskService(projectService);
        reportService = new ReportService();
        users = new User[MAX_USERS];
        userCount = 0;
        addUser(new AdminUser("eric mugisha", "eric.mugisha@amalitech.com"));
        addUser(new RegularUser("shima elyse", "shima.elsyse@gmail.com"));
        currentUser = users[0];
        loadSampleData();
    }

    public void start() {
        int choice;
        do {
            printBanner("JAVA PROJECT MANAGEMENT SYSTEM");
            System.out.println();
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
            choice = ValidationUtils.readChoiceInRange(scanner, "Enter your choice: ", 1, 5);
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
            int choice = ValidationUtils.readChoiceInRange(scanner, "Enter your choice: ", 1, 3);
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
            int choice = ValidationUtils.readChoiceInRange(scanner, "Enter filter choice: ", 1, 5);
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
                int next = ValidationUtils.readChoiceInRange(scanner, "Enter your choice: ", 1, 2);
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
        int choice = ValidationUtils.readChoiceInRange(scanner, "Enter your choice: ", 1, 2);
        if (choice == 2) {
            return false;
        }
        while (true) {
            double min = ValidationUtils.readBudget(scanner, "Enter minimum budget: ");
            double max = ValidationUtils.readBudget(scanner, "Enter maximum budget: ");
            if (max < min) {
                System.out.println(" Error: Maximum budget must be greater than or equal to minimum budget.");
                continue;
            }
            projectService.printByBudget(min, max);
            return true;
        }
    }

    private void showProjectDetails(Project project) {
        while (true) {
            printBanner("PROJECT DETAILS: " + project.getId());
            System.out.println();
            project.displayProject(true);
            System.out.println();
            System.out.println("Options:");
            System.out.println("1. Add New Task");
            System.out.println("2. Update Task Status");
            System.out.println("3. Remove Task");
            System.out.println("4. <<Back");
            int choice = ValidationUtils.readChoiceInRange(scanner, "Enter your choice: ", 1, 4);
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
            int choice = ValidationUtils.readChoiceInRange(scanner, "Enter your choice: ", 1, 5);
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

    private void calculateCompletion() {
        Project project = promptForProject("Enter project ID (or 0 to return): ");
        if (project == null) {
            return;
        }
        reportService.printReport(project);
    }

    private void viewReports() {
        printBanner("PROJECT STATUS REPORT");
        System.out.println();
        reportService.printReport(projectService);
        System.out.println();
        System.out.println("1. <<Back");
        ValidationUtils.readChoiceInRange(scanner, "Enter your choice: ", 1, 1);
    }

    private void switchUser() {
        printBanner("SWITCH USER");
        System.out.println();
        for (int i = 0; i < userCount; i++) {
            System.out.print((i + 1) + ". ");
            users[i].printUserReport();
        }
        System.out.println((userCount + 1) + ". Create User");
        System.out.println((userCount + 2) + ". <<Back");
        int choice = ValidationUtils.readChoiceInRange(scanner, "Enter your choice: ", 1, userCount + 2);
        if (choice == userCount + 2) {
            return;
        }
        if (choice == userCount + 1) {
            createUser();
            return;
        }
        currentUser = users[choice - 1];
        System.out.println("Current User: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
    }

    private void createUser() {
        if (!requireAdmin("create a user")) {
            return;
        }
        if (userCount >= MAX_USERS) {
            System.out.println("The user list is full.");
            return;
        }
        String name = ValidationUtils.readText(scanner, "Enter user name: ");
        String email = ValidationUtils.readEmail(scanner, "Enter email: ");
        if (emailExists(email)) {
            System.out.println(" Error: That email is already used.");
            return;
        }
        System.out.println("1. Regular User");
        System.out.println("2. Admin User");
        System.out.println("3. <<Back");
        int role = ValidationUtils.readChoiceInRange(scanner, "Enter role: ", 1, 3);
        if (role == 3) {
            return;
        }
        User user;
        if (role == 2) {
            user = new AdminUser(name, email);
        } else {
            user = new RegularUser(name, email);
        }
        addUser(user);
        user.printUserReport();
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
        int typeChoice = ValidationUtils.readChoiceInRange(scanner, "Enter project type: ", 1, 3);
        if (typeChoice == 3) {
            return;
        }

        String name = ValidationUtils.readText(scanner, "Enter project name: ");
        String description = ValidationUtils.readText(scanner, "Enter description: ");
        int duration = ValidationUtils.readPositiveInt(scanner, "Enter duration (days): ",
                " Error: Duration must be greater than 0.");
        double budget = ValidationUtils.readBudget(scanner, "Enter budget: ");
        int teamSize = ValidationUtils.readPositiveInt(scanner, "Enter team size: ",
                " Error: Team size must be greater than 0.");
        String leadName = currentUser.getName();

        Project project;
        if (typeChoice == 1) {
            String technology = ValidationUtils.readText(scanner, "Enter technology: ");
            project = new SoftwareProject(name, description, teamSize, duration, budget, leadName, technology);
        } else {
            String equipment = ValidationUtils.readText(scanner, "Enter equipment: ");
            project = new HardwareProject(name, description, teamSize, duration, budget, leadName, equipment);
        }

        projectService.add(project);
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
        String name = ValidationUtils.readText(scanner, "Enter task name: ");
        Project project = promptForProject("Enter project ID: ");
        if (project == null) {
            return;
        }
        addTaskTo(project, name, "");
    }

    private void addTaskTo(Project project) {
        printBanner("ADD NEW TASK");
        System.out.println();
        String name = ValidationUtils.readText(scanner, "Enter task name: ");
        addTaskTo(project, name, "");
    }

    private void addTaskTo(Project project, String name, String assignedTo) {
        if (project.isTaskListFull()) {
            System.out.println("This project cannot take more tasks.");
            return;
        }
        if (project.hasTaskName(name)) {
            System.out.println("Error: A task with that name already exists on this project.");
            return;
        }
        TaskStatus status = ValidationUtils.readStatus(scanner,
                "Enter initial status (Pending/In Progress/Completed): ");
        int hours = ValidationUtils.readPositiveInt(scanner, "Enter hours: ",
                "Error: Hours must be greater than 0.");
        Task task = taskService.add(project, name, assignedTo, status, hours);
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
        project.printTaskTable();
    }

    private void updateTaskStatus() {
        if (!requireAdmin("update a task")) {
            return;
        }
        Task task = promptForTask();
        if (task == null) {
            return;
        }
        TaskStatus status = ValidationUtils.readStatus(scanner, "Enter new status: ");
        taskService.updateStatus(task, status);
        System.out.println("✓ Task \"" + task.getName() + "\" marked as " + status.getLabel() + ".");
    }

    private void updateTaskOn(Project project) {
        if (!requireAdmin("update a task")) {
            return;
        }
        Task task = promptForTaskOn(project);
        if (task == null) {
            return;
        }
        TaskStatus status = ValidationUtils.readStatus(scanner, "Enter new status: ");
        taskService.updateStatus(task, status);
        System.out.println("✓ Task \"" + task.getName() + "\" marked as " + status.getLabel() + ".");
    }

    private void removeTask() {
        if (!requireAdmin("remove a task")) {
            return;
        }
        String taskId = ValidationUtils.readPrefixedId(scanner, "Enter task ID (or 0 to return): ", "TSK", true);
        if (taskId == null) {
            return;
        }
        Task removed = taskService.remove(taskId);
        if (removed == null) {
            System.out.println("Task not found.");
            return;
        }
        System.out.println("✓ Task \"" + removed.getName() + "\" removed.");
    }

    private void removeTaskOn(Project project) {
        if (!requireAdmin("remove a task")) {
            return;
        }
        String taskId = ValidationUtils.readPrefixedId(scanner, "Enter task ID (or 0 to return): ", "TSK", true);
        if (taskId == null) {
            return;
        }
        Task removed = project.removeTaskById(taskId);
        if (removed == null) {
            System.out.println("Task not found.");
            return;
        }
        System.out.println("✓ Task \"" + removed.getName() + "\" removed.");
    }

    private Project promptForProject(String prompt) {
        while (true) {
            String projectId = ValidationUtils.readPrefixedId(scanner, prompt, "PRJ", true);
            if (projectId == null) {
                return null;
            }
            Project project = projectService.findById(projectId);
            if (project == null) {
                System.out.println("Project not found.");
                continue;
            }
            return project;
        }
    }

    private Task promptForTask() {
        while (true) {
            String taskId = ValidationUtils.readPrefixedId(scanner, "Enter task ID: ", "TSK", true);
            if (taskId == null) {
                return null;
            }
            Task task = taskService.findById(taskId);
            if (task == null) {
                System.out.println("Task not found.");
                continue;
            }
            return task;
        }
    }

    private Task promptForTaskOn(Project project) {
        while (true) {
            String taskId = ValidationUtils.readPrefixedId(scanner, "Enter task ID: ", "TSK", true);
            if (taskId == null) {
                return null;
            }
            Task task = project.findTaskById(taskId);
            if (task == null) {
                System.out.println("Task not found.");
                continue;
            }
            return task;
        }
    }

    private User chooseUser(String prompt) {
        for (int i = 0; i < userCount; i++) {
            System.out.println((i + 1) + ". " + users[i].getName() + " (" + users[i].getId() + ", " + users[i].getRole() + ")");
        }
        int choice = ValidationUtils.readChoiceInRange(scanner, prompt, 1, userCount);
        return users[choice - 1];
    }

    private boolean requireAdmin(String action) {
        if (currentUser.canUpdateAndDelete()) {
            return true;
        }
        System.out.println("Only an admin can " + action + ".");
        return false;
    }

    private boolean emailExists(String email) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private void addUser(User user) {
        users[userCount] = user;
        userCount++;
    }

    private void loadSampleData() {
        SoftwareProject alpha = new SoftwareProject(
                "Alpha Tracker", "Task tracking app for startups", 5, 90, 15000, "Alice Johnson", "Java");
        HardwareProject iot = new HardwareProject(
                "IoT Sensor Kit", "Sensor prototype for smart devices", 3, 60, 10000, "Bob Smith", "Sensors");
        SoftwareProject mobile = new SoftwareProject(
                "Mobile Checkout", "Phone checkout for retail stores", 4, 40, 22000, "Alice Johnson", "Kotlin");
        HardwareProject robot = new HardwareProject(
                "Warehouse Robot", "Moving shelf robot for a warehouse", 6, 120, 48000, "Alice Johnson", "Motors");
        SoftwareProject payroll = new SoftwareProject(
                "Payroll Portal", "Staff pay and leave portal", 2, 30, 8000, "Bob Smith", "Java");

        projectService.add(alpha);
        projectService.add(iot);
        projectService.add(mobile);
        projectService.add(robot);
        projectService.add(payroll);

        taskService.add(alpha, "Design Database", "Alice Johnson", TaskStatus.COMPLETED, 8);
        taskService.add(alpha, "Implement API", "Bob Smith", TaskStatus.IN_PROGRESS, 12);
        taskService.add(alpha, "Write Unit Tests", "Bob Smith", TaskStatus.PENDING, 6);
        taskService.add(iot, "Assemble Board", "Bob Smith", TaskStatus.COMPLETED, 10);
        taskService.add(iot, "Calibrate Sensor", "Alice Johnson", TaskStatus.PENDING, 4);
        taskService.add(mobile, "Build Cart", "Alice Johnson", TaskStatus.COMPLETED, 5);
        taskService.add(mobile, "Payment Flow", "Bob Smith", TaskStatus.COMPLETED, 7);
        taskService.add(robot, "Frame Build", "Bob Smith", TaskStatus.PENDING, 20);
    }

    private void printBanner(String title) {
        System.out.println("╔" + "═".repeat(BANNER_WIDTH) + "╗");
        System.out.println("║" + center(title) + "║");
        System.out.println("╚" + "═".repeat(BANNER_WIDTH) + "╝");
    }

    private String center(String title) {
        if (title.length() >= BANNER_WIDTH) {
            return title.substring(0, BANNER_WIDTH);
        }
        int left = (BANNER_WIDTH - title.length()) / 2;
        int right = BANNER_WIDTH - title.length() - left;
        return " ".repeat(left) + title + " ".repeat(right);
    }
}
