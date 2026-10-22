package utils;

import models.Project;
import models.Task;
import models.TaskStatus;
import services.ConcurrencyService;
import services.ProjectService;
import services.TaskService;
import services.UserService;
import utils.exceptions.TaskNotFoundException;

import java.util.Scanner;

// Task screens: add, view, update, and remove tasks.
class TaskMenu extends MenuScreen {

    private final ProjectService projectService;
    private final TaskService taskService;
    private ProjectMenu projectMenu;

    TaskMenu(Scanner scanner, ProjectService projectService, TaskService taskService, UserService userService) {
        super(scanner, userService);
        this.projectService = projectService;
        this.taskService = taskService;
    }

    void setProjectMenu(ProjectMenu projectMenu) {
        this.projectMenu = projectMenu;
    }

    void open() {
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

    void simulateUpdates() {
        printBanner("PARALLEL TASK UPDATE SIMULATION");
        new ConcurrencyService(taskService).simulate(projectService);
    }

    void addToProject(Project project) {
        printBanner("ADD NEW TASK");
        System.out.println();
        String name = readText("Enter task name: ");
        saveTask(project, name, "");
    }

    void updateOnProject(Project project) {
        if (!requireAdmin("update a task")) {
            return;
        }
        changeStatus(askForTaskOn(project));
    }

    void removeOnProject(Project project) {
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
        }
    }

    void printTasks(Project project) {
        if (project.getTaskCount() == 0) {
            System.out.println("No tasks found.");
            return;
        }
        System.out.println("───────────────────────────────────────────────────────────────────────");
        System.out.printf("%-8s | %-20s | %s%n", "ID", "TASK NAME", "STATUS");
        System.out.println("───────────────────────────────────────────────────────────────────────");
        project.getTasks().forEach(task -> System.out.printf("%-8s | %-20s | %s%n",
                task.getId(), task.getName(), task.getStatus().getLabel()));
        System.out.println("───────────────────────────────────────────────────────────────────────");
    }

    private void addTask() {
        if (projectService.getCount() == 0) {
            System.out.println("No projects yet. Add a project first.");
            return;
        }
        printBanner("ADD NEW TASK");
        System.out.println();
        String name = readText("Enter task name: ");
        Project project = projectMenu.askForProject("Enter project ID: ");
        if (project == null) {
            return;
        }
        saveTask(project, name, "");
    }

    private void saveTask(Project project, String name, String assignedTo) {
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
        Project project = projectMenu.askForProject("Enter project ID (or 0 to return): ");
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
        changeStatus(askForTask());
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
        }
    }

    private Task askForTask() {
        try {
            String taskId = readId("Enter task ID: ", "TSK", true);
            if (taskId == null) {
                return null;
            }
            return taskService.findRequired(taskId);
        } catch (TaskNotFoundException ex) {
            printError(ex);
            System.out.println("Operation aborted. Returning to task menu...");
            return null;
        }
    }

    private Task askForTaskOn(Project project) {
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
        }
    }
}
